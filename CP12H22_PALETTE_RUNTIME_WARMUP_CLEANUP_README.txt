ChaoCraft CP12H.2.2 — Palette Runtime + Warmup Cleanup

Why this checkpoint is conservative
-----------------------------------
Color/Tone are not simple standalone textures in the current Viewer-faithful
renderer. Tone can change which material passes exist (for example mask layers
are suppressed/replaced in some Monotone paths), and the final palette also
depends on type/alignment/evolution.

Therefore this checkpoint does NOT falsely merge all Color/Tone body VBOs yet.
Doing so without explicit per-pass runtime material metadata would risk visual
regressions in Two-Tone/Monotone, belly, horns, wings, eyelids, Hero/Dark and
Chaos cases.

What is optimized now
---------------------
1. Single authoritative shared GPU warmup
   CP12H.2.1 warmed once from EntityRenderer construction and again after the
   initial CLIENT_RESOURCES reload. Constructor warmup is removed.

   Warmup is now scheduled only by the hard/resource-cache invalidation path,
   so initial startup should report exactly one completion summary.

2. Finite Color/Tone palette profiles
   Startup pre-resolves base palette/material states for:
   - every ChaoVisualType
   - alignment -100 / 0 / +100
   - every ChaoColorType
   - Two-Tone + Monotone

   These are tiny immutable CPU palette records, not duplicate body VBOs.

3. Runtime palette reuse
   Exact base Chao profiles reuse their prewarmed palette directly.
   Growing/intermediate slider states continue through the authoritative palette
   resolver so accuracy remains unchanged.

4. Existing finite GPU warmup remains
   - base model references
   - all HeadDeco
   - all Child Animal Parts
   - all Adult Animal Parts

What is intentionally NOT done
------------------------------
- no 28x duplication of full body VBOs
- no slider/morph combination preload
- no unsafe removal of Color/Tone from VisualKey while Tone still changes
  material-pass topology

A later shader/material-metadata refactor can move Color/Tone fully draw-time.
That should be done by tagging each material pass explicitly, not by guessing
from textures/colors.

Expected PASS
-------------
Startup:
- exactly ONE line:
  [Performance] Shared GPU warmup complete: ...
- summary now includes "palette profiles"
- no hundreds of individual warmed-part lines

Visual:
- Color Matrix remains identical
- Two-Tone / Monotone unchanged
- Hero/Dark/Chaos colors unchanged
- reflection / Animal Parts / HeadDeco unchanged
- Child animations unchanged

Performance:
- base Color/Tone switches reuse prewarmed palette records
- intermediate growth/sliders remain lazy and accurate
- no extra body VBO preload/native-memory multiplication
