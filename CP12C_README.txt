CP12C — Exact SA2 Rig Import

REQUIRES
- CP12B.1

SOURCE / GOLDEN REFERENCE
- ChaoSaTool.dae exported from SA Tools
- 40 original AL_RootObject joints
- Original SA2 Sprint JSON animation_0028D358
- SA Tools MatrixFunctions / ProcessTransforms behavior

MATHEMATICAL VALIDATION
- local = Translation * Rz * Ry * Rx * Scale
- max error reconstructing all 40 DAE bind matrices: 9.436943762e-08
- Nodes 17/20 preserve non-unit scale ~1.0032, 1.3802, 0.3000
- Offline Sprint reconstruction stayed assembled.

IMPLEMENTED
1. ChaoSa2RigDefinition:
   - 40-node hierarchy
   - original bind position/rotation
   - exact DAE bind scale
   - bind-world + inverse-bind-world precomputed once

2. ChaoAnimationPose:
   - source Position/Rotation sampling
   - exact bind Scale
   - T * Rz * Ry * Rx * S
   - animatedWorld * inverseBindWorld
   - final SA2 -> Child ChaoCraft coordinate conversion
   - no VBO rebuild per animation frame

3. F8 diagnostic identifies exact-rig mode.

UNCHANGED
- appearance/morphs/colors/materials/reflection/lighting
- Animal Parts / Head Deco
- R18 cache/preload/lifecycle
- per-clip persistent Speed tuning
- production world renderer remains unanimated while F8 validates the rig

TEST
1. F8 -> Anim
2. Neutral / Normal Child
3. 037 animation_0028D358
4. Speed 0.50
5. Restart -> Play
6. Compare against SA Tools.

PASS
- no exploded/detached limbs
- left/right limbs stay on their pivots
- body/head hierarchy stays assembled
- VBO remains stable during playback

NEXT AFTER PASS
- exact DAE/controller mesh ownership instead of side heuristics
- exact nodes for eyes/face/ears/horns and attachments
- bind-pivot generalization for Adult/Chaos evolution models
- gameplay animation state runtime
