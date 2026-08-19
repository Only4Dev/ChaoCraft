ChaoCraft CP12G.4 — SA Tools Preview Parity

Finding
-------
Do NOT swap bilateral rig mappings.

The SA Tools DAE bind positions prove:

  +X: node3 arm, node6 leg, node18/19 eye, node23 upper-head, node37 wing
  -X: node10 arm, node13 leg, node21/22 eye, node25 upper-head, node39 wing

That matches the Viewer source geometry convention already used by CP12G.3.
Swapping these nodes would make future hand attachments node4/11 and other
semantic helpers attach to the physically wrong side.

Why F8 looked mirrored
----------------------
The Minecraft GUI presents the converted front-facing Chao with opposite
horizontal screen parity to the SA Tools viewport. A phone/camera mirror made
the same animation frame visually coincide 1:1, confirming this is presentation
parity rather than animation time or bone identity.

Change
------
Only F8/Visual Lab presentation is mirrored on X at draw time.

Unchanged:
- child.cmesh / Rig Bible mappings
- SA2 pose evaluator
- Mtotal
- node4/11 held-object attachments
- node33 Emotion
- reflection skinning
- world/production renderer
- VBO/cache identity and performance

Expected PASS
-------------
Compare the SAME source frame directly:
- 037 frame1 should show the same stepping side as SA Tools frame1.
- 0028F618 frame5 should show the same chest-beating arm as SA Tools frame5.
- No need to hunt for frame4/frame9 equivalents anymore.
- Reflection, Emotion and the whole Chao mirror together, so no detached passes.
- Animation remains smooth and stretch-free.

This checkpoint intentionally changes only the diagnostic preview convention;
production/world orientation remains untouched until real gameplay animation
is enabled and audited in world coordinates.
