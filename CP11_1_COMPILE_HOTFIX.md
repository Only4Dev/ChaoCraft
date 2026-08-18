# CP11.1 compile hotfix
- Visual Lab performance snapshot now uses the existing `flash(...)` status helper.
- FPS sampling now calls `client.getCurrentFps()` on the active MinecraftClient instance.
- Restores the missing `ChaoRenderMetrics` import in `ChaoRenderer`.
