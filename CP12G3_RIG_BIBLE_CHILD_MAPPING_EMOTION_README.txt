ChaoCraft CP12G.3 — Full SA2 Rig Bible + Child Mapping + Emotion Node33

Scope
-----
Foundation checkpoint before auditing the remaining 279 SA2 animations.

1. Adds a complete machine-readable 0..39 rig registry.
2. Rebuilds Child skin weights against the visible nodes proven by ChaoSaTool.dae.
3. Keeps every pivot/helper/attachment alive in ChaoAnimationPose.
4. Makes Child Emotion follow node33 at draw time.
5. Supersedes the unapplied CP12G.2 arm-only patch.

Child mapping
-------------
Visible deform nodes:
  1,3,6,8,10,13,16,18,19,21,22,23,25,27,37,39

The new child.cmesh rigidly maps:
  Belly      -> 1
  Arms +X/-X -> 3 / 10
  Legs +X/-X -> 6 / 13
  Tail       -> 8
  Head body  -> 16
  Horn/upper -> 23 / 25
  Eyes       -> 18 / 21
  Eyelids    -> 19 / 22
  Mouth      -> 27
  Wings      -> 37 / 39

Helpers 2/4/5/7/9/11/etc receive zero visible body weights.

Emotion
-------
Child Emotion keeps its already-approved static mesh/anchor in the cached body
entry, but its DrawBatch is tagged with node33. During F8 animation the node33
delta is converted through the same verified Mtotal basis as body skinning and
applied at draw time.

No per-frame VBO rebuild is introduced.

Binary rewrite
--------------
Skin influence records changed: 4053
Final mapping counts:
  Child_Arms -> node 3: 850 vertices
  Child_Arms -> node 10: 672 vertices
  Child_Belly -> node 1: 735 vertices
  Child_Head -> node 16: 2091 vertices
  Child_Head -> node 18: 363 vertices
  Child_Head -> node 19: 241 vertices
  Child_Head -> node 21: 333 vertices
  Child_Head -> node 22: 235 vertices
  Child_Head -> node 23: 467 vertices
  Child_Head -> node 25: 379 vertices
  Child_Head -> node 27: 280 vertices
  Child_Legs -> node 6: 545 vertices
  Child_Legs -> node 13: 495 vertices
  Child_Tail -> node 8: 544 vertices
  Child_Wings -> node 37: 384 vertices
  Child_Wings -> node 39: 384 vertices

Expected PASS
-------------
- 037 Run/Sprint remains smooth and assembled.
- 002FCB68 frame19 no longer stretches an arm toward the doll helper.
- Other held-object clips no longer deform visible arm vertices through nodes4/11.
- Eye/eyelid/mouth/upper-head animation tracks can now affect their actual
  visible high-poly regions.
- Emotion follows node33/head motion during Child animation.
- Reflection remains synchronized.
- HeadDeco stress/performance remains stable.
- Remaining animation discrepancies should now be audited against this Rig Bible
  instead of changing global mappings ad hoc.
