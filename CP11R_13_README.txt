CP11R.13 — Restore the old F8 rendering pipeline

Apply:
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

This is built on the CURRENT local checkpoint lineage. R12 did not modify
ChaoRenderer.java, so R11 is the current Java renderer source.

Changes:
- F8 uses an isolated preview GPU cache.
- Preview VBOs are always built with 0x00F000F0 full-bright packed light.
- Base F8 batches use Minecraft's standard entity RenderLayer shader, just like
  the viewer before the R8 production lighting rewrite.
- F8 no longer uses ChaoMaterialShader for ordinary body/material passes.
- Reflection in F8 still uses the validated ChaoReflectionShader.
- World renderer remains completely unchanged:
  draw-time lighting, no light cache variants, torch optimization, build queue,
  safer BufferBuilder and native-OOM recovery all remain active.
- Releasing the preview entity clears both production and preview cache bindings.

Expected:
- World looks/performs exactly as before.
- F8 returns to the old smooth 3D shading/lighting.
- F8 is independent from world day/night.
- F8 Reflection remains functional.
