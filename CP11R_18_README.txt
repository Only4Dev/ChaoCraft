CP11R.18 — Client Visual Runtime / Warm Cache Foundation

SOURCE OF TRUTH
- Built on the complete current local checkpoint lineage through CP11R.17.
- Earlier CP11R ZIPs were overlaid in order to reconstruct the current working tree.
- GitHub was used only to verify unchanged build/API context, not as the source
  for post-commit ChaoCraft rendering code.

APPLY
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Run: .\gradlew build
- Then Run Client from IntelliJ.

NO VISUAL CHANGES
This checkpoint does NOT change:
- Chao colors/material formulas
- morph formulas
- Animal Part placement
- Chaos anchors
- Head Deco
- Reflection
- lighting/shading
- F8 legacy visual pipeline

IMPLEMENTED

1) ATOMIC RESOURCE PRELOAD
- All .cmesh files continue to be parsed once during client resource reload.
- Every merged Chao entity texture is now eagerly loaded/bound once after reload.
- Core Chao shaders continue to load through Fabric shader registration.
- No complete Chao appearance combinations are precomputed.

2) WARM GPU CACHE ACROSS WORLD TRANSITIONS
Old:
  Save/Quit -> destroy every production VBO
  Join      -> rebuild everything

New:
  Save/Quit -> remove world/entity UUID bindings only
               keep immutable shared visual VBOs warm
  Join      -> rebase warm entries to the new world clock
               reuse matching visual states immediately

Hard clear still occurs on resource reload/F3+T.

3) TRANSIENT CPU MORPH ARRAYS
- Morph weights, palette resolution and prepared position/normal arrays are now
  generated only on an actual GPU cache miss.
- They are released after VBO upload.
- Cache hits do not build a second CPU-side morphed copy of the Chao.

4) F8 SLIDER COALESCING
- Rapid slider changes are debounced for 120 ms.
- While dragging, F8 keeps showing the last stable VBO.
- Only the latest settled state is uploaded.
- The previous preview VBO is released after the replacement succeeds.
- This prevents create/upload/delete storms from every mouse-motion value.

5) BACKGROUND GPU RELEASE PACING
- Stale/idle production VBO cleanup is capped at 8 entries per prune pass.
- Avoids a large glDeleteBuffers burst after world transitions.
- Global memory hard ceiling behavior is unchanged.

6) GL_OUT_OF_MEMORY RECOVERY
- New VBO uploads check OpenGL for GL_OUT_OF_MEMORY.
- If NVIDIA/AMD reports driver-side allocation failure without Java throwing OOM:
    * abort that visual build
    * release idle GPU states
    * pause new builds for 5 seconds
    * keep previous stable Chao/invisibility rather than pushing until process death
- Check occurs only during VBO upload/cache misses, not every render frame.

7) TELEMETRY FIX
- F8 preview cache no longer overwrites production cache-size telemetry.
- Watchdog cached MiB/shared-entry values now describe the production cache.

8) ANIMATION-READY CACHE CONTRACT
- Animation pose/time is explicitly excluded from GPU VisualKey semantics.
- Future animations must be applied at draw time (bone/segment matrices/shader),
  never by rebuilding appearance VBOs every animation frame.

SERVER
- No server rendering work added.
- Server remains responsible only for Chao authoritative state/NBT/simulation.

EXPECTED TEST
A. Startup:
   - log should show mesh preload
   - log should additionally show Chao texture preload once

B. Save/Quit/Rejoin:
   - summon/load a large matrix
   - allow warm-up
   - Save/Quit and immediately rejoin
   - many matching Chao should hit warm VBOs rather than rebuild from zero

C. F8:
   - aggressively drag Age/Swim/Fly/Run/Power sliders
   - preview may visually trail by ~120 ms while dragging
   - when released it catches up to the exact final state
   - no VBO churn storm / GL_OUT_OF_MEMORY

D. Production:
   - world visuals must be pixel-equivalent to CP11R.17
   - Reflection, lighting, Animal Parts, Head Deco unchanged

E. Stress:
   - matrices + movement + Save/Quit/Rejoin several times
   - send latest.log
   - especially report any:
       GL_OUT_OF_MEMORY
       "Chao VBO allocation pressure detected"
       GPU cache churn
       low FPS warnings
