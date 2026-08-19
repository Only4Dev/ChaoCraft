ChaoCraft CP12D — CPU Reference Diagnostic

Purpose
-------
Prove the already-validated Blender/SA2 skinning math inside Minecraft while
completely bypassing the experimental CP12D GPU skin transport.

Correct-base restoration
------------------------
- ChaoRenderer is based on the exact CP12B.1 renderer (CP12C did not change it).
- Chao material/reflection shaders are restored to the final pre-CP12D CP11R
  lineage. This restores the previously approved production/F8 shading path.
- Production world rendering keeps the CP11R.18 shared warm VBO architecture.
- child.cmesh v4 remains only because its immutable Viewer skin weights are the
  data source for this diagnostic.

F8 Child Anim diagnostic
------------------------
When an animation is active on a Child:
1. ChaoAnimationPose evaluates the original SA2 40-node pose.
2. Each bone delta is converted with the Offline Mirror v3 proven formula:
       D_render = M * D_viewer * inverse(M)
3. Viewer weights from child.cmesh v4 skin the morphed Child vertices on CPU.
4. The completed animated positions/normals are uploaded to ONE isolated
   transient F8 VBO for that frame.
5. The old approved F8 RenderLayer shader draws it with DEFAULT overlay UV.

This deliberately rebuilds only the isolated debug VBO when the animation frame
changes. It is NOT the final runtime architecture and never enters production
VisualKey/shared-cache identity. Leaving Anim immediately releases it.

Expected interpretation
-----------------------
If Sprint now matches the Blender golden reference AND F8 shading returns:
  geometry + cmesh weights + Java pose + M*D*M^-1 are confirmed in Minecraft,
  and the remaining fault is exclusively CP12D's GPU transport/shader design.

If Sprint is still wrong here, the fault is before GPU transport and we will
inspect the Java/asset values directly against the offline mirror.
