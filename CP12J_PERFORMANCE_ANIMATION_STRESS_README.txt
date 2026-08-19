CP12J — Animation Performance Finalization + Stress Matrices

Goals
- Preserve the now-correct Child/Adult/Chaos visual and animation behavior.
- Reduce draw-time CPU/allocation overhead after Adult attachments became animated.
- Warm finite/reusable resources before gameplay where memory-safe.
- Make Visual Lab matrices isolated from the current preview accessories.
- Add 50- and 100-Chao randomized animation stress matrices.

Key changes
1. Animation draw optimization
   - One render-space 40-node palette is computed per Chao draw and reused by body,
     rigid nodes, Animal Parts, HeadDeco and skinning shader batches.
   - Removes repeated basis construction/inversion + 40-matrix palette rebuilding
     once per batch.
   - Child/Adult/Chaos behavior and node mappings are unchanged.

2. Faster first-use GPU admission
   - Runtime unique VBO build gate moves from 50 ms (~20/s) to 16.67 ms (~60/s).
   - Still permits at most one expensive new build per render interval; cache hits
     remain unlimited.
   - Matrix server spawning remains staggered at 2 Chao/tick, so client build
     capacity can now keep ahead of matrix admission instead of guaranteed backlog.

3. Expanded startup warmup
   Existing warmup remains: all .cmesh CPU meshes, all animation clips, 114 bind
   profiles, palettes, textures, HeadDeco VBOs and Animal Part VBOs.
   Added: 21 canonical no-accessory production body VBOs (7 visual types x N/H/D)
   when ChaoCraft owns the shader. Combinatorial colors/parts/reflections are NOT
   prebuilt because that would explode GPU memory.

4. Matrix isolation
   Color / Reflection / Animal matrices now inherit only body morphology
   (type, age, alignment, Swim/Fly/Run/Power). They reset unrelated preview state:
   accessories, HeadDeco, face/mouth, emotion, color/reflection as appropriate.

5. Animation stress matrices
   Visual Lab Test tab adds:
   - Anim Stress (50)
   - Anim Ultimate (100)
   Every spawned Chao gets randomized type, morph values, color, monotone,
   reflection, eight Animal Part slots, HeadDeco, face/mouth/emotion state and a
   unique animation index with randomized phase.
   Stress animation metadata is DataTracker-only and deliberately NOT persisted.
   Normal gameplay Chao use -1 and therefore stay on the existing renderer path.

Expected validation
1. .\gradlew build
2. Verify Child and several Adult/Chaos clips + Emotion/Parts/Deco still match CP12I.2E.
3. Verify Color/Reflection/Animal matrices no longer clone unrelated preview extras.
4. Run Anim Stress (50), wait until all 50 are spawned, observe sustained animation.
5. Clear Matrix, then run Anim Ultimate (100).
6. Send console [Performance] warnings if any threshold is crossed.

Expected warmup log now includes canonical body VBO count and KiB.
