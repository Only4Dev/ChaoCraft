ChaoCraft CP12D.2 — Verified Render-Space Skinning + F8 Shading Restore

Scope
-----
This is a targeted correction of CP12D/CP12D.1. It does NOT change:
- the 279 animation resources,
- the SA2 40-node hierarchy/bind constants,
- Viewer->SA2 bone mapping,
- cmesh v4 geometry or skin weights,
- morph behavior,
- VisualKey/VBO cache semantics.

Animation fix
-------------
The offline Blender mirror proved that the current CP12D applies a Viewer-space
bone delta to a vertex that has already been transformed into ChaoCraft's VBO
local space.

For every bone CP12D.2 now uploads:

    D_gpu = M * D_viewer * inverse(M)

where M is exactly createLocalPositionMatrix(visualType), i.e. the same fixed
transform used before the vertex entered the cached VBO.

Offline golden-reference result for Sprint:
- CURRENT total RMS: ~1.08 .. 1.42
- FIXED total RMS:   ~0.000007 .. 0.000011
- FIXED max error:   ~0.000060

All six Child segments, including Belly, converged to numerical/weight
quantization noise.

F8 shading fix
--------------
CP12D forced skinned Child batches through ChaoMaterialShader, but its
PreviewFullBright path also disabled normal diffuse shading and effectively
made ordinary body materials flat/emissive.

CP12D.2 changes preview full-bright semantics:
- world/lightmap darkness is ignored in F8,
- normal-based diffuse shading remains active,
- ordinary materials are NOT made emissive by preview mode,
- LightUv and PreviewFullBright are explicitly uploaded for skinned preview.

The emotion ball remains on its existing non-skinned preview path.

Expected PASS
-------------
1. Minecraft loads without shader errors.
2. F8 Body: Child regains readable shading/volume and color separation.
3. F8 Anim -> Sprint:
   - pose matches the Blender 1:1 golden reference,
   - arms/legs/wings/head/tail remain assembled and move correctly,
   - Belly follows correctly.
4. Play/Pause/Restart/frame scrub/speed remain functional.
5. Animation frame changes do not rebuild VBOs.
6. World Chao static appearance remains unchanged; production animation is
   still intentionally deferred until F8 passes.
