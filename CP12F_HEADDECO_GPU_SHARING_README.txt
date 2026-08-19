ChaoCraft CP12F — HeadDeco GPU Sharing / Native-memory Stability

Observed failure
----------------
HeadDeco + Animal Matrix / slider stress produced:
- escalating VBO build pressure,
- repeated OutOfMemoryError recovery,
- invisible Chao,
- GL/native allocation failure,
- JVM/native crash.

The Animal Matrix also inherited the current F8 HeadDeco, so all 34 spawned
animal Chao carried the same unrelated hat.

Root architecture issue
-----------------------
HeadDeco geometry was built inside buildGpuBatches(), meaning every complete
Chao visual state owned fresh VBO copies of the same immutable hat.

A slider change or Animal Matrix therefore duplicated HeadDeco GPU objects
alongside each body state. The body cache's byte budget underestimates native
driver/object fragmentation pressure, so many small duplicated buffers can
still exhaust native/driver allocation before the nominal 384 MiB ceiling.

Fix
---
1. HeadDeco GPU geometry is now renderer-shared:
   - one immutable VBO set per ChaoHeadDecoType,
   - source Hats -90 X orientation baked once,
   - Chao-specific anchor translation applied at draw time,
   - shared HeadDeco VBOs survive normal visual-state churn,
   - hard resource/cache clear closes them explicitly.

2. Complete body cache no longer contains HeadDeco VBOs.

3. Body cache canonicalizes exact HeadDeco identity:
   - NONE
   - active normal HeadDeco
   - active HeadDeco that hides emotion ball
   Thus Eggshell -> Pumpkin -> Skull does not rebuild an identical full body.

4. Animal Matrix explicitly clears HeadDeco:
   - matrix remains an Animal Parts audit,
   - no accidental 34x hat duplication.

5. Existing approved shading, body VBO cache, CP12E GPU animation math and
   production rendering are otherwise unchanged.

Future animation compatibility
------------------------------
This architecture is intentional preparation for animated accessories:
HeadDeco geometry is persistent and anchor/bone transforms can be updated at
draw time without rebuilding its VBO.

PASS
----
A. F8 HeadDeco:
   - appearance/anchor/shading unchanged.
   - switching hats logs one warm build per hat type, not per body state.

B. Slider stress with one HeadDeco:
   - body may rebuild only at normal debounced visual-state cadence,
   - HeadDeco VBO is reused,
   - no progressive native-memory collapse / invisible Chao.

C. Animal Matrix:
   - spawned animals do NOT inherit preview HeadDeco.
   - matrix remains stable under the prior stress sequence.

D. Production / animation:
   - 037 remains smooth/correct.
   - normal no-HeadDeco stress remains unchanged.
