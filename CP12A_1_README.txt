CP12A.1 — Animation Resource Path Hotfix

CAUSE
Minecraft Identifier resource paths must be lowercase.
The original SA Tools filenames contain uppercase A-F in the hexadecimal motion
address, so Fabric rejected most of them as invalid resource-pack paths.
Only 29/279 clips happened to have fully lowercase-compatible filenames.

IMPORTANT APPLY STEP
Because ZIP extraction cannot delete the old uppercase files, delete the old
animation resource directory FIRST:

PowerShell from C:\Dev\ChaoCraft:
Remove-Item -Recurse -Force "src\client\resources\assets\chaocraft\animations\chao\original"

Then extract this ZIP over C:\Dev\ChaoCraft.

WHAT CHANGED
- All 279 physical resource filenames are lowercase.
- JSON contents are unchanged.
- Original internal SA2 motion names such as animation_0028D358 remain unchanged.
- No Java, renderer, F8 UI, cache, visuals, or animation data changed.

EXPECTED
Startup:
Preloaded 279 original SA2 Chao animation clips ...

No:
Invalid path in mod resource-pack chaocraft: ...animations/chao/original/...

F8 Anim:
all 279 exported clips are selectable.
