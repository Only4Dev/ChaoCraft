ChaoCraft CP12D.1 — Shader Compile Hotfix

Fix:
- GLSL reserved keyword `packed` was incorrectly used as a local variable
  inside decodeInfluence() in both Chao vertex shaders.
- Renamed it to `encoded`.

No other CP12D behavior or assets are changed.

Expected:
- Resource reload completes without the previous C0000 errors.
- chao_material and chao_reflection shaders register successfully.
- Minecraft reaches the world without the null ShaderProgram crash.
- Then F8 -> Anim -> Sprint can finally exercise CP12D GPU skinning.
