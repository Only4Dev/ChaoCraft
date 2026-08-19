ChaoCraft CP12D — Native SA2 GPU Skinning Foundation

Blender result: Viewer high-poly can use the original SA2 40-node rig directly.
No animation retarget is required.

Validated mapping:
LEFT (Viewer negative X): shoulder_L->009, Arm_L->010, Hand_L->011,
Foot_L->013, Wing_L->039.
RIGHT (Viewer positive X): shoulder_R->002, Arm_R->003, Hand_R->004,
Foot_R->006, Wing_R->037.
Other: UpperBody->001, Chin->016, Hips->001, Tail->008.

Implementation:
- cmesh v4 adds two compact skin influences per vertex.
- child.cmesh keeps the exact existing geometry/morph data and appends the
  original Viewer weights remapped directly to SA2 node indices.
- Bone index + 10-bit weight are packed into Overlay UV shorts so the existing
  Minecraft entity VertexFormat and shared immutable VBO cache remain intact.
- Material/reflection vertex shaders accept 40 SA2 bone matrices.
- F8 Child animation now uses smooth GPU skinning; CP12B rigid splitting is disabled.
- Animation pose/time is draw-time only and never changes VisualKey/VBO identity.
- Production world Chao remains unanimated until the F8 Sprint golden-reference passes.

PASS:
F8 -> Anim -> Sprint animation_0028D358 should stay fully assembled and match
the Blender 1:1 Sprint, including arms, feet and wings. Play/Pause/Restart,
frame scrub and Speed should continue to work without per-frame VBO rebuilds.
