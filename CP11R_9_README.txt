CP11R.9 — Uniform Lighting Visual Fix

Built directly on the current CP11R.8 lineage, not GitHub.

Apply:
- Extract over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

Changes:
- Removes R8 synthetic directional shapeLight.
- Keeps R8 draw-time lightmap and no-light-VBO-variants architecture.
- Keeps R8 native OOM recovery and safer BufferBuilder allocation.
- Bright now carries Viewer _Emission=0.5 through the draw-time shader.
- Hero halo stays visibly self-lit at night; it does not illuminate the world.
- Removes repeated material-shader load log spam.

Expected:
- Chao regain the uniform/natural lighting appearance from R7.
- No bottom-up / matte look.
- Gold reflection keeps the approved reflective material.
- Halo remains clearly visible in darkness.
- Repeated torch placement/removal remains stable and does not rebuild VBOs per light level.
