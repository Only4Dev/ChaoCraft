CP11R.11 — F8 Preview Studio Lighting

SOURCE OF TRUTH:
- ChaoRenderer/chao_material: current CP11R.10 lineage.
- chao_reflection: current unchanged CP11R.8 lineage.
- No GitHub files were used for code changed after the last commit.

APPLY:
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

CHANGES:
- F8 preview gets explicit neutral/studio light instead of sampling whatever
  lightmap texture happens to be bound during GUI rendering.
- World rendering still uses real Minecraft day/night/block light.
- Preview keeps CP11R.10 normal-based 3D shading; it is NOT flat/fullbright.
- Reflection preview uses the same studio-light override.
- Cache identity, torch optimization, build queue, BufferBuilder/OOM fixes and
  gameplay reflections are untouched.

PASS:
1. In-world Chao look identical to CP11R.10.
2. F8 preview is bright/readable like the original Chao Viewer.
3. F8 still has clear head/belly/limb shading and volume.
4. F8 reflection remains camera-reactive.
5. Gameplay day/night/torch behavior remains unchanged.
