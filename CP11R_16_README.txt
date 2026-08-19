CP11R.16 — Head/Animal Anchor + Native Memory Hotfix

APPLY
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

FIX 1 — COOKING POT
- Viewer Pot.mat is NOT white:
  ChaosMaterial.shader
  _Cube = CookinPot 1
  _Ref = 1
  _Emission = 0
  no _MainTex
- Exact Viewer CookinPot cubemap is included.
- Pot submesh now uses the existing Chao reflection shader.

FIX 2 — ANIMAL PART AUTO-ANCHORS
Viewer CalcColorGroups / CalcPartsLocations weights:
  Young = 1 - Age
  Normal = Normal * Age / 100
  Swim = Swim / 100
  Fly = Fly / 100
  Run = Run / 100
  Power = Power / 100

Previous ChaoCraft incorrectly multiplied Swim/Fly/Run/Power by Age too.
This caused attachment positions to drift from Viewer while changing shapes/age.
FACE/FOREHEAD corrections from CP14/15 remain intact.

FIX 3 — F8 SLIDER NATIVE MEMORY CHURN
- Production GPU cache behavior is unchanged.
- F8 now tracks the current preview appearance.
- When a slider creates a new preview state, the previous preview VBO is closed
  immediately instead of remaining warm for ~10 seconds.
- This prevents dozens of transient multi-MiB VBO variants from accumulating
  during slider scrubbing.
- Preview cache is still reused normally while the state remains unchanged.

TEST
1. Cooking Pot should have the yellow/gold reflective material from Viewer.
2. Test Face/Forehead/Ears/Horns across Adult age and Normal/Swim/Fly/Run/Power sliders.
3. Compare attachment motion directly against Viewer.
4. Drag Fly/Swim/etc sliders continuously for several minutes in F8.
5. GPU cache should not churn upward from old preview states.
6. No native-memory crash.
7. World performance/reflection/lighting must remain unchanged.
