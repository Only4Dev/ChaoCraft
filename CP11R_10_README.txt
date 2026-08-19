CP11R.10 — Restore Chao Shading (keep R8/R9 performance fixes)

SOURCE:
- Built from the current local checkpoint lineage: CP11R.8 + CP11R.9.
- GitHub was not used for files modified after the last commit.

APPLY:
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

CHANGES:
- Restores normal-based volume/shading to the base Chao material.
- Uses a Minecraft-like two-direction diffuse formula:
    min(1, (dotLight0 + dotLight1) * .6 + .4)
- Directions are symmetric/top-biased; removes the bottom-lit artifact.
- Dynamic Minecraft lightmap is STILL draw-time.
- Torches/day-night STILL do not produce new VBO variants.
- Native OOM recovery / safer BufferBuilder / build queue remain untouched.
- Hero halo fullbright change from R9 is reverted: halo uses normal lighting again.
- Viewer Bright reflection keeps its actual _Emission=.5.
- Reflection cubemap/material logic is untouched.

PASS:
1. Normal Child regains visible 3D volume like the pre-R8 screenshots.
2. Belly/head/arms/feet have clear natural shading again.
3. Chaos forms no longer look flat/fullbright.
4. Hero halo behaves like before rather than forced bright at night.
5. Reflections remain correct.
6. Repeated torch placement/removal remains crash-free.
7. Day/night changes do not trigger VBO build storms.
