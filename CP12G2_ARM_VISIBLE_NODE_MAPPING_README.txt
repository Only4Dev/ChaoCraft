ChaoCraft CP12G.2 — SA2 Visible-Node Arm Mapping

Source-backed discovery
-----------------------
The original ChaoSaTool.dae controllers show visible Chao geometry bound only
to these SA2 nodes:

1, 3, 6, 8, 10, 13, 16, 18, 19, 21, 22, 23, 25, 27, 37, 39

Critically, arm helper nodes 2, 4, 9 and 11 do NOT deform visible Chao geometry.

Previous Child Viewer mapping
-----------------------------
Right:
  shoulder_R -> 2
  Arm_R      -> 3
  Hand_R     -> 4

Left:
  shoulder_L -> 9
  Arm_L      -> 10
  Hand_L     -> 11

This made SA2 attachment/helper translations (especially node 4 in
002FCB68 Playing with Doll) stretch the Viewer smooth arm.

Correct visible-body mapping
----------------------------
Right Viewer arm weights -> node 3
Left Viewer arm weights  -> node 10

The existing high-poly geometry, morphs, UVs, normals, indices and every other
segment remain byte-for-byte conceptually unchanged. Only Child_Arms skin
influence IDs/merged weights changed.

Offline deformation strain
--------------------------
002FCB68 frame 19:
  CURRENT max edge ratio: 5.22267x
  FIXED   max edge ratio: ~1.00000001x

003077B0 frame 37:
  CURRENT max edge ratio: 3.08069x
  FIXED   max edge ratio: ~1.00000001x

037 Sprint:
  CURRENT max edge ratio: 1.35078x
  FIXED   max edge ratio: ~1.00000001x

Changed Child_Arms vertices: 1449
Before nonzero influence counts: {11: 560, 10: 358, 9: 82, 4: 685, 3: 493, 2: 122}
After nonzero influence counts: {10: 672, 3: 850}

Expected PASS
-------------
- 002FCB68 frame 19: no stretched ribbon arm.
- 003077B0: arm/hand no longer follows invisible helper-node translation.
- 037 remains smooth in time and the arm itself now behaves rigidly like the
  original visible SA2 arm piece.
- No changes to body/legs/head/tail/wings/reflection/HeadDeco/performance path.

Emotion finding
---------------
Node 33 (object_000127A4) is an invisible attachment/helper above the head,
child of node 32. It is not a visible body deform node and is the correct
candidate for the Emotion attachment. Its integration is intentionally kept
separate from this arm-weight checkpoint.
