# ChaoCraft — CP12I.2E Adult Animated Attachments

CP12I.2E extends the already-approved Child attachment path to Adult and Chaos
without introducing a second rig or per-frame geometry work.

## Universal attachment contract

Emotion, Animal Parts and HeadDeco use the same SA2 nodes for every Chao body:

- Emotion: node 33
- Arms: nodes 3 / 10
- Legs: nodes 6 / 13
- Tail: node 8
- Wings: nodes 37 / 39
- Face: node 27
- Horns / Ears: nodes 23 / 25
- Forehead: node 29
- HeadDeco: node 16

Attachment VBOs encode these node ids using the reserved attachment range
`64..103`. At draw time the node is decoded and transformed through the active
Chao body's render-space basis. The encoding is therefore universal; it is no
longer Child-only.

## Performance contract

- No animation frame/time enters VisualKey or shared attachment cache identity.
- No Animal Part, Emotion or HeadDeco geometry is rebuilt per animation frame.
- Adult Animal Parts remain shared by `(adult asset set, animal, slot)`.
- HeadDeco remains one shared immutable VBO set per type.
- Bilateral parts are split once when their shared VBO is constructed so each
  side can follow its correct SA2 node.
- Only the draw-time matrices change while an animation is playing.

## Scope

This checkpoint changes attachment animation only. It does not change Chao
simulation, behavior, world AI, material formulas, facial blend/morph control,
or animation clip data.
