ChaoCraft CP12H.2 — Shared Animal Parts + Reflection Cache De-duplication

Observed pressure
-----------------
CP12H.1 functionality passed, but cycling Animal Parts + HeadDeco + Reflection
eventually drove the shared body cache above 100 entries and caused repeated
native/VBO allocation OutOfMemoryError.

This checkpoint removes two avoidable multiplicative dimensions.

Animal Parts
------------
Before:
  exact animal selection was embedded in every complete body GPU entry.

Now:
  - one immutable shared VBO set per:
        child/adult source variant + animal type + slot
  - Chao-specific anchor translation is draw-time
  - Child SA2 attachment transform is draw-time
  - exact animal TYPE is removed from body cache identity
  - occupied SLOT presence remains in body identity because SizeDown/replacement
    morphs genuinely change native Chao body geometry.

This is the same resource-sharing architecture already proven for HeadDeco.

Reflection
----------
Full-reflection texture variants:
  SILVER/GOLD/GARNET/RUBY/TOPAZ/SAPPHIRE/AQUAMARINE/AMETHYST/PERIDOT/
  EMERALD/ONYX/PEARL/MOON

share identical body/reflection geometry and cubemap UV generation.

They now canonicalize to GOLD for GPU cache identity. The requested cubemap
texture is rebound at draw time, so cycling those colors reuses one geometry
entry instead of creating one full body VBO state per texture.

Special reflection categories remain separate because their material contract
differs:
  NONE
  BRIGHT
  SHINY
  TT_METAL

Unchanged
---------
- CP12G Rig Bible and body animation
- CP12G.4 SA Tools parity
- CP12G.1 skinned reflection
- CP12F shared HeadDeco
- CP12H.1 Child Animal Part animation
- Emotion node33
- VBO-stable fractional animation
- production/world body cache architecture

Startup preload
---------------
The existing startup preload remains:
- source meshes
- textures
- 279 animation clips

Shared GPU attachments are warmed lazily on first real use because eagerly
uploading every animal/slot combination would increase native GPU pressure.
Once warmed, they persist and are reused.

PASS
----
1. Visual regression:
   - Animal Parts positions/animation unchanged.
   - HeadDeco unchanged.
   - all reflection colors still display correctly.
   - 037 and audited animations unchanged.

2. Reflection stress:
   Cycle jewel reflection colors repeatedly.
   Full-reflection color changes should no longer create unique full-body VBO
   entries for every texture.

3. Animal stress:
   Switch animals within already-occupied slots.
   The body should reuse the same slot-occupancy state while only the small
   shared attachment VBO changes.

4. Logs:
   First use may log:
     [Performance] Warmed shared AnimalPart child / OTTER / ARMS once...
   Re-selecting the same animal+slot should not warm it again.

5. Combined slow stress:
   Animal Parts + HeadDeco + reflection changes should no longer exhibit the
   previous monotonic cache explosion / rapid native allocation failure.
