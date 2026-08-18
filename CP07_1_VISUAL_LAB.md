# CP07.1 — ChaoCraft Visual Lab

Permanent in-game visual QA tool for ChaoCraft.

## Open

- Press **F8** while in a world.
- The lab targets the Chao under the crosshair when possible, otherwise the nearest Chao within 24 blocks.
- Use `< Chao` / `Chao >` to cycle nearby Chao.

The screen edits the real `ChaoEntity` / `ChaoAppearanceState` through the normal server-authoritative data-tracker path. It does not contain a second preview renderer.

## Live controls

- Child / Adult stage
- Adult family: Normal / Swim / Fly / Run / Power
- Alignment: -100 Dark .. 0 Neutral .. +100 Hero
- Age: 0..1
- Swim / Fly / Run / Power evolution channels
- Eyes: 0..12
- Eyelid: 0..2
- Mouth: 0..12
- Auto/custom face
- Emotion: Auto / Neutral / Hero / Dark / None
- Halo: Default / Tilted
- 16 presets: Child + all 15 adult first-evolution families
- Reset / Randomize / Auto Face + Ball
- Copy State / Paste State

## Adult Matrix

`Spawn Adult Matrix (15)` spawns real Chao entities in this order:

| | Normal | Swim | Fly | Run | Power |
|---|---|---|---|---|---|
| Neutral | NN | NS | NF | NR | NP |
| Hero | HN | HS | HF | HR | HP |
| Dark | DN | DS | DF | DR | DP |

`Clear Matrix` removes only entities created by the Visual Lab matrix.

## Validation

Run locally:

```powershell
.\gradlew build
```

Then open a creative/test world, summon or use a Chao, press F8, and compare the 15 presets/matrix against Chao Viewer.

## Compact inspector + live preview (CP07.2)

The lab now uses a compact 330px control column with 18px controls and a live preview panel on the right when enough screen width is available.

The preview renders the selected real `ChaoEntity` through Minecraft's registered entity renderer (`InventoryScreen.drawEntity`), so it reuses ChaoCraft's production morph/material/VBO path rather than maintaining a separate preview renderer. Move the mouse to inspect the Chao from different angles while changing sliders.

On narrow GUI layouts the preview hides automatically and the control column expands to remain usable.
