ChaoCraft CP12H.1 — Child Animal Parts + Head Deco Animation

Scope
-----
Child only. Adult/Hero/Dark/Chaos animation is intentionally deferred until
this attachment path is visually validated.

Implemented
-----------
- Animal Parts follow the SA2 Rig Bible at draw time.
- Bilateral slots are split into independent rigid +X/-X VBO halves.
- Head Deco follows node16 (Head).
- Existing node33 Emotion is migrated to the same generic Child attachment path.
- All Child attachment deltas use the verified Mtotal coordinate basis.
- No animation frame enters VisualKey/VBO identity.
- No per-frame VBO upload/rebuild.

Mappings
--------
ARMS      +X 3  / -X 10
LEGS      +X 6  / -X 13
TAIL          8
WINGS     +X 37 / -X 39
FACE          27
HORNS     +X 23 / -X 25
EARS      +X 23 / -X 25
FOREHEAD      29
HEAD DECO     16
EMOTION       33

Preserved
---------
- CP12G.4 SA Tools preview parity.
- CP12F shared HeadDeco performance architecture.
- CP12G.1 skinned reflection.
- CP12G.3 body mapping / Rig Bible.
- production world renderer remains unanimated.
- held-object helpers 4/11 remain free for future objects.

PASS
----
Use a Child with mixed Animal Parts + Head Deco and play asymmetric clips:
- arms/legs parts remain attached to their matching body side,
- wings/tail move with their body nodes,
- ears/horns/forehead/head deco remain attached to the animated head,
- Emotion continues following node33,
- no detached static duplicate pieces,
- 037 and the audited 279 body animations remain unchanged,
- aggressive slider/playback does not create per-frame VBO pressure.
