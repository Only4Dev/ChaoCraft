# CP07.4 — Native renderer hardening + Visual Lab QA

This checkpoint addresses the failures found while stress-testing CP07.3.

## Renderer safety
- Fixes the world-time rollback cache bug that could close a freshly built VBO and then draw the closed VAO after changing worlds.
- GL/VBO cleanup is marshalled to the Minecraft client/render thread.
- Closed cache entries/batches are never submitted to OpenGL.
- GPU cache has a hard 128 MiB estimated budget, 192-entity ceiling, 2 light variants/entity, and stale-entry pruning.
- Authoritative float state is quantized only for rendering: age 0.01, alignment/evolution 1 point. Server data stays full precision.

## Visual Lab
- Preview no longer uses InventoryScreen.drawEntity; it calls the production ChaoRenderer directly in GUI space.
- Slider changes are debounced. Continuous dragging changes only the draft UI; the production VBO updates after a short pause or mouse release.
- Debug state packets are also debounced and capped after settling.
- Added Base Matrix (15), Adult Extremes (75), Child Extremes (15), and Clear Matrix.

## Stress test
1. Open F8 and verify the Chao appears in the right preview.
2. Drag Swim/Fly/Run/Power violently for 1–2 minutes. VBO/s should stay near zero while continuously dragging and spike only when pausing/releasing.
3. F3+T, then leave/re-enter the world and switch to a new world. There should be no GL_INVALID_OPERATION / invalid VAO messages.
4. Spawn Adult Extremes (75), walk around them, then clear them. Cache MB must remain bounded and eventually fall after clearing/stale pruning.
