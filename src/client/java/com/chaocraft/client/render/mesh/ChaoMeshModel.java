package com.chaocraft.client.render.mesh;

import java.util.List;

/** Immutable runtime representation of a compiled morphable Chao mesh family. */
public record ChaoMeshModel(List<Segment> segments) {
	public record Segment(
			String name,
			int vertexCount,
			float[] positions,
			float[] normals,
			float[] uvs,
			float[][] morphPositionDeltas,
			float[][] morphNormalDeltas,
			int[] indices
	) {
	}
}
