ChaoCraft CP12G.1 — Child Reflection + Native GPU Skinning

Scope
-----
Fixes only the Child reflection animation pass.

Cause
-----
CP12E deliberately excluded CUBEMAP_STRIP/reflection batches from the native
skinned VBO format. The ordinary Child body animated while reflection rendered
the same body in bind/static pose, producing the visible gold duplicate.

Fix
---
- Reflection Child batches use the same stable skinned VBO layout (UV3/UV4).
- chao_reflection_skinning applies the same verified Mtotal SA2 palette.
- Existing approved chao_reflection fragment shader is reused unchanged.
- Reflection remains camera-reactive and draw-time.
- No frame-dependent VBO rebuilds.

PASS
----
F8 -> Child -> reflective -> Anim -> 037:
- no static/duplicate gold body,
- reflection follows the animated surface exactly,
- Sprint remains smooth/correct,
- reflection appearance unchanged,
- VBO count remains stable.
