# CP10 — Source-exact HF materials, Reflection, Animal Parts

## Hero Fly source parity
The previous CP09 ear-specific material swap was removed. Hero Fly now follows the Chao Viewer source/material data directly:
- HFBody `_Layer2` = `HF_Body2`
- HFBody `_Layer3` = `HF_Body1`
- HFBody `_Layer4` = `HF_Body3`
No ear-specific visual heuristic remains.

## Reflection
Appearance state now persists/syncs the 17 Viewer reflection modes:
None, Shiny, Silver, Gold, Garnet, Ruby, Topaz, Sapphire, Aquamarine, Amethyst, Peridot, Emerald, Onyx, Pearl, Moon, Bright, TTMetal.

The implementation uses the Viewer reflection-material membership, reflection strength/emission rules, and extracted reflection textures. Because AssetRipper only recovered a dummy body for the original Unity Chao shader, the cubemap sampling itself is reproduced in the Minecraft renderer rather than copied from unavailable shader source.

F8 Body tab can cycle Reflection. Test tab includes Reflection Matrix (17).

## Animal Parts
The Viewer animal system is ported as eight independent slots:
Arms, Legs, Tail, Wings, Face, Horns, Ears, Forehead.

- 34 animal variants from the Viewer data are represented.
- Child and Adult meshes/materials are separate where the Viewer supplies them.
- Adult attachment anchors follow the palette position groups used by `CalcPartsLocations()`.
- Replaced base Arms/Legs/Tail/Wings use the Viewer `SizeDown` behavior.
- Animal parts persist and synchronize through the authoritative Chao appearance state.
- F8 Parts tab edits each slot independently and can clear all parts.
- Test tab includes Animal Matrix (34).

## QA
1. Build and launch.
2. F8: verify Reflection modes and Reflection Matrix.
3. F8 Parts: cycle every slot in Child and Adult, then Summon Chao.
4. Verify Summoned state survives save/reload.
5. Spawn Animal Matrix and inspect attachment placement/materials.
6. After state settles, VBO/s should return to 0.
