# ChaoCraft — SA2 Chao Rig Bible 0–39

Source of truth for the original `al_ncn` Chao skeleton used by SA Tools and
Chao Garden animation data.

This table combines:

1. the exact 40-node hierarchy/object ids exported by SA Tools;
2. direct visual inspection of every node in SA Tools;
3. the DAE skin controllers, which prove which nodes actually deform visible
   original Chao geometry.

**Rule:** every node remains in the runtime pose. Only nodes proven to deform
visible geometry receive Child body weights. Pivots and attachments are never
deleted or repurposed as deform bones.

| Node | Parent | SA Tools object | Role | Visible deform | Current semantic |
|---:|---:|---|---|:---:|---|
| 00 | — | `object_00016DB4` | ROOT | no | Root / floor origin |
| 01 | 0 | `object_00016D7C` | DEFORM | yes | Core / belly center |
| 02 | 1 | `object_00016804` | PIVOT | no | Left arm pivot/helper |
| 03 | 2 | `object_000167CC` | DEFORM | yes | Left visible arm/shoulder |
| 04 | 3 | `object_000163A4` | ATTACHMENT | no | Left hand held-object attachment |
| 05 | 1 | `object_0001636C` | PIVOT | no | Left leg pivot/helper |
| 06 | 5 | `object_00016334` | DEFORM | yes | Left visible leg |
| 07 | 1 | `object_00015F54` | PIVOT | no | Tail pivot/helper |
| 08 | 7 | `object_00015F1C` | DEFORM | yes | Visible tail |
| 09 | 1 | `object_00015B84` | PIVOT | no | Right arm pivot/helper |
| 10 | 9 | `object_00015B4C` | DEFORM | yes | Right visible arm/shoulder |
| 11 | 10 | `object_00015724` | ATTACHMENT | no | Right hand held-object attachment |
| 12 | 1 | `object_000156EC` | PIVOT | no | Right leg pivot/helper |
| 13 | 12 | `object_000156B4` | DEFORM | yes | Right visible leg |
| 14 | 1 | `object_000152E4` | PIVOT | no | Head chain pivot A |
| 15 | 14 | `object_000152AC` | PIVOT | no | Head chain pivot B |
| 16 | 15 | `object_00015274` | DEFORM | yes | Visible head |
| 17 | 16 | `object_00014554` | PIVOT | no | Left eye pivot/helper |
| 18 | 17 | `object_0001451C` | DEFORM | yes | Visible left eye |
| 19 | 17 | `object_00013FAC` | DEFORM | yes | Visible left eyelid |
| 20 | 16 | `object_00013B74` | PIVOT | no | Right eye pivot/helper |
| 21 | 20 | `object_00013B3C` | DEFORM | yes | Visible right eye |
| 22 | 20 | `object_00013604` | DEFORM | yes | Visible right eyelid |
| 23 | 16 | `object_000131CC` | DEFORM | yes | Upper-head left visible part / horn-ear base |
| 24 | 16 | `object_00012F14` | ATTACHMENT | no | Upper-head left helper / tip attachment |
| 25 | 16 | `object_00012EDC` | DEFORM | yes | Upper-head right visible part / horn-ear base |
| 26 | 16 | `object_00012C14` | ATTACHMENT | no | Upper-head right helper / tip attachment |
| 27 | 16 | `object_00012BDC` | DEFORM | yes | Visible mouth region |
| 28 | 27 | `object_000128BC` | ATTACHMENT | no | Mouth item attachment (food/pacifier candidate) |
| 29 | 16 | `object_00012884` | ATTACHMENT | no | Forehead attachment/helper |
| 30 | 16 | `object_0001284C` | ATTACHMENT | no | Upper-head left rear attachment/helper |
| 31 | 16 | `object_00012814` | ATTACHMENT | no | Upper-head right rear attachment/helper |
| 32 | 16 | `object_000127DC` | PIVOT | no | Emotion parent/pivot |
| 33 | 32 | `object_000127A4` | ATTACHMENT | no | Emotion attachment/controller |
| 34 | 1 | `object_0001276C` | ATTACHMENT | no | Back-center attachment between wings |
| 35 | 1 | `object_00012734` | ATTACHMENT | no | Chest / medal attachment |
| 36 | 1 | `object_000126FC` | PIVOT | no | Left wing pivot/helper |
| 37 | 36 | `object_000126C4` | DEFORM | yes | Visible left wing |
| 38 | 1 | `object_000123FC` | PIVOT | no | Right wing pivot/helper |
| 39 | 38 | `object_000123C4` | DEFORM | yes | Visible right wing |

## Visible deform nodes proven by the SA Tools DAE

`1, 3, 6, 8, 10, 13, 16, 18, 19, 21, 22, 23, 25, 27, 37, 39`

These are the only node ids referenced by non-zero visible mesh skin weights
in `ChaoSaTool.dae`.

## Child high-poly mapping

The current Viewer Child mesh is rebuilt against that exact visible-node set:

| Child segment/material | SA2 node |
|---|---|
| Belly / torso | `1` |
| Arms +X side | `3` |
| Arms -X side | `10` |
| Legs +X side | `6` |
| Legs -X side | `13` |
| Tail | `8` |
| Head body | `16` |
| Head upper/horn +X | `23` |
| Head upper/horn -X | `25` |
| Eye +X | `18` |
| Eye -X | `21` |
| Eyelid +X | `19` |
| Eyelid -X | `22` |
| Mouth side + middle | `27` |
| Wing +X | `37` |
| Wing -X | `39` |

`+X/-X` is used deliberately in the binary mapping to avoid mixing Viewer,
SA2 and camera-facing left/right conventions.

## Attachments now preserved for gameplay

- `4` — held object attachment, one hand
- `11` — held object attachment, opposite hand
- `28` — mouth item / food / pacifier candidate
- `29` — forehead attachment
- `33` — Emotion controller
- `34` — back-center attachment
- `35` — chest / medal attachment
- `24/26/30/31` — upper-head attachment/helper nodes pending content audit

## Emotion

`node33 / object_000127A4` is child of `node32`, visibly terminates above the
head in SA Tools, and is retained as an animated attachment rather than a deform
bone.

For Child Animation Lab playback the existing Emotion mesh is now tagged with
node33 and receives its bind-to-pose transform at draw time. Animation frame
does not enter VBO identity and Emotion geometry is not rebuilt per frame.

## Important discovery from Playing with Doll

`node4 / object_000163A4` moves the thrown doll/held-object attachment. It does
not deform visible arm geometry in the original DAE. Assigning Viewer hand
vertices to node4 caused the extreme ribbon/stretch bug.

The same principle applies to every helper/attachment in this Bible.
