#!/usr/bin/env python3
"""Compile AssetRipper Unity Mesh YAML into ChaoCraft's compact .cmesh format.

This development-only tool keeps Unity/FBX parsing out of the runtime mod. It
extracts base geometry, UVs, normals, triangle indices, and the six adult Chao
blend-shape deltas used by the Viewer's Neutral/Normal family.
"""
from __future__ import annotations

import argparse
import re
import struct
from pathlib import Path

MORPHS = ("Normal", "Swim", "Fly", "Run", "Power", "SizeDown")
PARTS = ("Arms", "Belly", "Head", "Legs", "Tail", "Wings")
MAGIC = b"CHM1"
VERSION = 1


def align16(value: int) -> int:
    return (value + 15) // 16 * 16


def parse_mesh(path: Path) -> dict:
    text = path.read_text(encoding="utf-8", errors="replace")
    name = re.search(r"^\s*m_Name:\s*(.+)$", text, re.MULTILINE).group(1).strip()
    vertex_count = int(re.search(r"^\s*m_VertexCount:\s*(\d+)", text, re.MULTILINE).group(1))
    vertex_bytes = bytes.fromhex(re.search(r"^\s*_typelessdata:\s*([0-9a-fA-F]+)", text, re.MULTILINE).group(1))
    index_bytes = bytes.fromhex(re.search(r"^\s*m_IndexBuffer:\s*([0-9a-fA-F]+)", text, re.MULTILINE).group(1))

    vertex_data = text[text.index("  m_VertexData:"):text.index("  m_CompressedMesh:")]
    channel_block = vertex_data[vertex_data.index("    m_Channels:"):vertex_data.index("    m_DataSize:")]
    channels = [tuple(map(int, match.groups())) for match in re.finditer(
        r"- stream:\s*(\d+)\s+offset:\s*(\d+)\s+format:\s*(\d+)\s+dimension:\s*(\d+)",
        channel_block,
    )]

    stream_strides: dict[int, int] = {}
    for stream, offset, data_format, dimension in channels:
        if dimension == 0:
            continue
        if data_format != 0:
            raise ValueError(f"{name}: unsupported non-float vertex channel format {data_format}")
        stream_strides[stream] = max(stream_strides.get(stream, 0), offset + 4 * dimension)

    stream_offsets: dict[int, int] = {}
    cursor = 0
    streams = sorted(stream_strides)
    for index, stream in enumerate(streams):
        stream_offsets[stream] = cursor
        cursor += stream_strides[stream] * vertex_count
        if index < len(streams) - 1:
            cursor = align16(cursor)
    if cursor != len(vertex_bytes):
        raise ValueError(f"{name}: unexpected vertex buffer layout ({cursor} != {len(vertex_bytes)})")

    def read_channel(channel_index: int, vertex_index: int) -> tuple[float, ...]:
        stream, offset, data_format, dimension = channels[channel_index]
        base = stream_offsets[stream] + vertex_index * stream_strides[stream] + offset
        return struct.unpack_from("<" + "f" * dimension, vertex_bytes, base)

    positions = [read_channel(0, i) for i in range(vertex_count)]
    normals = [read_channel(1, i) for i in range(vertex_count)]
    uv0 = [read_channel(3, i) for i in range(vertex_count)]
    indices = list(struct.unpack("<" + "H" * (len(index_bytes) // 2), index_bytes))

    shapes = text[text.index("  m_Shapes:"):text.index("  m_BindPose:")]
    morph_vertex_block = shapes[shapes.index("    vertices:"):shapes.index("    shapes:")]
    entry_pattern = re.compile(
        r"- vertex:\s*\{x:\s*([^,]+), y:\s*([^,]+), z:\s*([^}]+)\}\s*"
        r"normal:\s*\{x:\s*([^,]+), y:\s*([^,]+), z:\s*([^}]+)\}\s*"
        r"tangent:\s*\{x:\s*([^,]+), y:\s*([^,]+), z:\s*([^}]+)\}\s*"
        r"index:\s*(\d+)",
        re.MULTILINE,
    )
    entries = []
    for match in entry_pattern.finditer(morph_vertex_block):
        values = [float(value) for value in match.groups()[:9]]
        entries.append((int(match.group(10)), tuple(values[:3]), tuple(values[3:6])))

    frame_block = shapes[shapes.index("    shapes:"):shapes.index("    channels:")]
    frames = [tuple(map(int, match.groups())) for match in re.finditer(
        r"- firstVertex:\s*(\d+)\s+vertexCount:\s*(\d+)\s+hasNormals:\s*(\d+)\s+hasTangents:\s*(\d+)",
        frame_block,
    )]
    channel_shapes = shapes[shapes.index("    channels:"):shapes.index("    fullWeights:")]
    morph_channels = [(match.group(1).strip(), int(match.group(2)), int(match.group(3))) for match in re.finditer(
        r"- name:\s*(.+?)\s+nameHash:\s*\d+\s+frameIndex:\s*(\d+)\s+frameCount:\s*(\d+)",
        channel_shapes,
    )]

    morphs: dict[str, tuple[list[tuple[float, float, float]], list[tuple[float, float, float]]]] = {}
    for channel_name, frame_index, frame_count in morph_channels:
        if frame_count != 1:
            raise ValueError(f"{name}: multi-frame blend shape {channel_name} is not supported")
        first, count, _, _ = frames[frame_index]
        position_deltas = [(0.0, 0.0, 0.0) for _ in range(vertex_count)]
        normal_deltas = [(0.0, 0.0, 0.0) for _ in range(vertex_count)]
        for vertex_index, position_delta, normal_delta in entries[first:first + count]:
            position_deltas[vertex_index] = position_delta
            normal_deltas[vertex_index] = normal_delta
        morphs[channel_name] = (position_deltas, normal_deltas)

    missing = [morph for morph in MORPHS if morph not in morphs]
    if missing:
        raise ValueError(f"{name}: missing required morphs: {', '.join(missing)}")

    return {
        "name": name,
        "positions": positions,
        "normals": normals,
        "uv0": uv0,
        "indices": indices,
        "morphs": morphs,
    }


def write_string(output, value: str) -> None:
    encoded = value.encode("utf-8")
    output.write(struct.pack("<H", len(encoded)))
    output.write(encoded)


def compile_family(input_dir: Path, family: str, output_path: Path) -> None:
    meshes = [parse_mesh(input_dir / f"{family}_{part}.asset") for part in PARTS]
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("wb") as output:
        output.write(MAGIC)
        output.write(struct.pack("<II", VERSION, len(meshes)))
        for mesh in meshes:
            write_string(output, mesh["name"])
            vertex_count = len(mesh["positions"])
            output.write(struct.pack("<II", vertex_count, len(mesh["indices"])))
            for vertex_index in range(vertex_count):
                output.write(struct.pack("<3f", *mesh["positions"][vertex_index]))
                output.write(struct.pack("<3f", *mesh["normals"][vertex_index]))
                output.write(struct.pack("<2f", *mesh["uv0"][vertex_index]))
                for morph in MORPHS:
                    position_delta, normal_delta = mesh["morphs"][morph]
                    output.write(struct.pack("<3f", *position_delta[vertex_index]))
                    output.write(struct.pack("<3f", *normal_delta[vertex_index]))
            output.write(struct.pack("<" + "I" * len(mesh["indices"]), *mesh["indices"]))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("input_dir", type=Path, help="AssetRipper Assets/Mesh directory")
    parser.add_argument("output", type=Path, help="Output .cmesh file")
    parser.add_argument("--family", default="Neutral_Normal")
    arguments = parser.parse_args()
    compile_family(arguments.input_dir, arguments.family, arguments.output)
