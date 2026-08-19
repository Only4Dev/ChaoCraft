CP12J.3a — BuiltBuffer double-release hotfix

Apply over CP12J.3.

Fix:
- Removes the explicit BuiltBuffer.release() after VertexBuffer.upload().
- Minecraft 1.20.1 already releases/consumes the BuiltBuffer during upload.
- Keeps CP12J.3 warmup pinning and all other performance/audit changes intact.

Expected:
- Shared GPU warmup completes again instead of stopping with IllegalStateException.
- F8 / Visual Lab opens without "Buffer has already been released!".
