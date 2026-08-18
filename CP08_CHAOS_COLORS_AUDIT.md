# CP08 — Chaos Chao + Real Color/Tone Audit

## Scope

This checkpoint extends the production appearance pipeline using Chao Viewer's extracted runtime/save logic as the reference.

### Chaos Chao
- Neutral Chaos, Hero Chaos and Dark Chaos use their extracted five-part meshes.
- Material/submesh assignment mirrors `ChangeNeutralChaos`, `ChangeHeroChaos` and `ChangeDarkChaos`.
- Auto faces use the Viewer's Chaos eye slots (Eye11/Eye12/Eye13 mapping).
- Emotion anchors use the family-specific Viewer positions.
- Chaos ignores the normal second-evolution morph channels in the render cache, matching the fixed Chaos body meshes.

### SA2 Color and tone fields
`ColorType` and `Monotone` are persisted appearance fields in the extracted save/viewer logic, so they are part of the real Chao appearance model rather than Visual Lab-only debug tints.

Implemented ColorT values:
Normal, White, Grey, Black, Brown, Red, Orange, Yellow, Green, Lime Green, Sky Blue, Blue, Purple, Pink.

Two-Tone/Monotone behavior follows the Viewer material/palette branches, including the Child colored palette groups and the Neutral Swim special cover behavior.

### Visual Lab
- Body tab: Color and Two-Tone/Monotone controls.
- Type cycle/presets include Neutral/Hero/Dark Chaos.
- `Chaos Matrix (3)` spawns the three Chaos alignment families.
- `Color Matrix (28)` uses the current virtual Chao as its base and spawns all 14 colors in both Two-Tone and Monotone, allowing the same audit on any Child/adult/Chaos family.
- Summoned Chao persist and synchronize ColorType/Monotone server-authoritatively.

### Texture sampling parity
Viewer-derived Chao material masks/body maps use bilinear filtering metadata to match the Unity texture import behavior more closely, most visibly on thin Hero Fly ear markings.

## Intentionally deferred
Animal/Monster Parts are not included here. Finish the base body/material/color audit first, then layer animal parts onto a verified appearance renderer.
