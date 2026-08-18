package com.chaocraft.client.render.mesh;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Reads ChaoCraft's compact little-endian morph mesh format. */
public final class ChaoMeshLoader {
	private static final byte[] MAGIC = {'C', 'H', 'M', '1'};
	private static final int VERSION = 1;

	private ChaoMeshLoader() {
	}

	public static ChaoMeshModel load(InputStream input) throws IOException {
		try (LittleEndianInput data = new LittleEndianInput(input)) {
			for (byte expected : MAGIC) {
				if (data.readByte() != expected) {
					throw new IOException("Invalid Chao mesh magic");
				}
			}

			int version = data.readInt();
			if (version != VERSION) {
				throw new IOException("Unsupported Chao mesh version: " + version);
			}

			int segmentCount = data.readInt();
			if (segmentCount < 1 || segmentCount > 64) {
				throw new IOException("Invalid Chao mesh segment count: " + segmentCount);
			}

			List<ChaoMeshModel.Segment> segments = new ArrayList<>(segmentCount);
			for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
				String name = data.readString();
				int vertexCount = data.readInt();
				int indexCount = data.readInt();
				if (vertexCount < 1 || vertexCount > 100_000 || indexCount < 3 || indexCount > 1_000_000) {
					throw new IOException("Invalid geometry counts for segment " + name);
				}

				float[] positions = new float[vertexCount * 3];
				float[] normals = new float[vertexCount * 3];
				float[] uvs = new float[vertexCount * 2];
				int morphCount = ChaoMorphTarget.values().length;
				float[][] morphPositions = new float[morphCount][vertexCount * 3];
				float[][] morphNormals = new float[morphCount][vertexCount * 3];

				for (int vertex = 0; vertex < vertexCount; vertex++) {
					int p = vertex * 3;
					int uv = vertex * 2;
					positions[p] = data.readFloat();
					positions[p + 1] = data.readFloat();
					positions[p + 2] = data.readFloat();
					normals[p] = data.readFloat();
					normals[p + 1] = data.readFloat();
					normals[p + 2] = data.readFloat();
					uvs[uv] = data.readFloat();
					uvs[uv + 1] = data.readFloat();

					for (int morph = 0; morph < morphCount; morph++) {
						morphPositions[morph][p] = data.readFloat();
						morphPositions[morph][p + 1] = data.readFloat();
						morphPositions[morph][p + 2] = data.readFloat();
						morphNormals[morph][p] = data.readFloat();
						morphNormals[morph][p + 1] = data.readFloat();
						morphNormals[morph][p + 2] = data.readFloat();
					}
				}

				int[] indices = new int[indexCount];
				for (int index = 0; index < indexCount; index++) {
					int vertexIndex = data.readInt();
					if (vertexIndex < 0 || vertexIndex >= vertexCount) {
						throw new IOException("Out-of-range index in segment " + name + ": " + vertexIndex);
					}
					indices[index] = vertexIndex;
				}

				segments.add(new ChaoMeshModel.Segment(
						name, vertexCount, positions, normals, uvs,
						morphPositions, morphNormals, indices
				));
			}
			return new ChaoMeshModel(List.copyOf(segments));
		}
	}

	private static final class LittleEndianInput implements AutoCloseable {
		private final DataInputStream input;

		private LittleEndianInput(InputStream input) {
			this.input = new DataInputStream(new BufferedInputStream(input));
		}

		private byte readByte() throws IOException {
			return input.readByte();
		}

		private int readUnsignedShort() throws IOException {
			int b0 = input.read();
			int b1 = input.read();
			if ((b0 | b1) < 0) {
				throw new EOFException();
			}
			return b0 | (b1 << 8);
		}

		private int readInt() throws IOException {
			int b0 = input.read();
			int b1 = input.read();
			int b2 = input.read();
			int b3 = input.read();
			if ((b0 | b1 | b2 | b3) < 0) {
				throw new EOFException();
			}
			return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
		}

		private float readFloat() throws IOException {
			return Float.intBitsToFloat(readInt());
		}

		private String readString() throws IOException {
			int length = readUnsignedShort();
			byte[] bytes = new byte[length];
			input.readFully(bytes);
			return new String(bytes, StandardCharsets.UTF_8);
		}

		@Override
		public void close() throws IOException {
			input.close();
		}
	}
}
