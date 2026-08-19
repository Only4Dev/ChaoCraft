CP11R.14 — Animal Parts Auto-Fit 1:1

SOURCE OF TRUTH
- Current post-R13 renderer is NOT modified by this checkpoint.
- ChaoAnimalAnchorProfiles.java and ChaoRenderCache.java were unchanged by R1-R13,
  so they are taken from the last local src snapshot / committed baseline.
- Rules implemented are the Chao Viewer CalcPartsLocations() and
  SetBlendShapeWeights() behavior audited before this patch.

APPLY
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Gradle Build + Run in IntelliJ

CHANGES
1. Adult FACE anchor:
   old: ofp ? FacePosition : mouthPosition
   Viewer: ofp ? mouthPosition : FacePosition

2. FOREHEAD anchor:
   old: mouthPosition
   Viewer: FacePosition

3. SizeDown composition:
   old implementation cleared Normal/Swim/Fly/Run/Power for a replaced segment.
   new implementation preserves existing morph weights and adds SizeDown=100%,
   matching independent Unity blend-shape weights.

4. Head replacement:
   Head now receives SizeDown when FACE / FOREHEAD / HORNS / EARS Animal Parts
   occupy that host region.

5. Existing per-zone behavior remains:
   Arms -> Arms
   Legs -> Legs
   Tail -> Tail
   Wings -> Wings
   Head decoration slots -> Head

NO CHANGES
- No Animal Part mesh gets invented blend shapes.
- No changes to reflection, lighting, F8, VBO cache, shaders or performance systems.
- Child attachment anchors remain as before.

TEST
- Adult Otter Face / nosehair-whisker parts across different adult stat sliders.
- Several FACE and FOREHEAD parts on Normal/Swim/Fly/Run/Power adults.
- Arms/Legs/Tail/Wings with extreme adult shapes.
- Confirm original body geometry retracts cleanly instead of clipping through parts.
- Confirm non-replaced body zones preserve their normal adult morph.
- Check F8 and world; both should agree on placement.
