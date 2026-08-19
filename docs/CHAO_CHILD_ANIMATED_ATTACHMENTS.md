# ChaoCraft — Child Animated Attachments

CP12H.1 connects the existing rigid Viewer Animal Parts and shared Head Deco
geometry to the SA2 0–39 Rig Bible.

## Child slot mapping

| Viewer slot | +X SA2 node | -X SA2 node | Behavior |
|---|---:|---:|---|
| Arms | 3 | 10 | rigid bilateral |
| Legs | 6 | 13 | rigid bilateral |
| Tail | 8 | — | rigid |
| Wings | 37 | 39 | rigid bilateral |
| Face | 27 | — | follows mouth/head chain |
| Horns | 23 | 25 | rigid bilateral upper-head |
| Ears | 23 | 25 | rigid bilateral upper-head |
| Forehead | 29 | — | forehead attachment |
| Head Deco | 16 | — | follows head |

Bilateral Animal Part meshes are split once at VBO construction by source-space
triangle centroid X. Each resulting half remains a stable immutable VBO.

## Attachment-space contract

`rigNode 0..39`
: reserved for the older non-Child rigid animation diagnostic.

`rigNode 64..103`
: Child SA2 attachments. Decode with `node = rigNode - 64` and transform through
the same verified `Mtotal = RotateX(+90) * FlipZ` basis as Child body skinning.

This makes Emotion (33), Animal Parts and Head Deco share one attachment path
without introducing animation frame/time into VBO identity.

## Performance

There is no per-frame vertex upload or Animal Part rebuild.

- Animal Parts remain part of the existing visual-state VBO cache.
- Head Deco remains one shared VBO set per type from CP12F.
- Only matrices change per draw.
