# CP12A — Original SA2 Animation Library + F8 Animation Lab Foundation

## Goal
Start ChaoCraft's native animation system without GeckoLib and without changing the completed static visual renderer.

## Source data imported
- 279 original Chao motion JSON exports produced by SA Tools AnimJSONConverter.
- Original `chao.action` export ordering.
- Original `al_motion_table/info.ini` preserved as `motion_table.ini` for the next motion-state layer.

## Runtime architecture
`ChaoAnimationRepository` parses all clips once during CLIENT_RESOURCES reload.
The server never reads or knows about animation files.

Each clip preserves:
- export index
- original `animation_XXXXXXXX` name
- frame count
- ModelParts
- interpolation mode
- ShortRot
- sparse Position keys by SA2 node index
- sparse Rotation keys by SA2 node index
- raw Ninja binary-angle rotations + conversion helpers to radians

No animation allocations occur every render frame in this checkpoint.

## F8 Visual Lab
New `Anim` tab:
- previous/next clip selector
- Play/Pause
- Restart
- frame scrub slider
- clip frame/node/model-part diagnostics
- known animated node diagnostics

Default selection starts on export 037, the Chao Sprint clip shown during SA Tools archaeology.

## Important renderer finding
Current ChaoCraft `.cmesh` files preserve visual segments such as:
- `*_Arms`
- `*_Legs`
- `*_Head`
- `*_Tail`
- `*_Wings`

But original SA2 motions address independent hierarchy nodes. Confirmed original attachment indices include:
- 003 Left Arm
- 006 Left Leg
- 008 Tail
- 010 Right Arm
- 013 Right Leg
- 024 Left Ear
- 026 Right Ear
- 028 Face/Tongue
- 029 Forehead
- 030 Left Horn
- 031 Right Horn
- 037 Left Wing
- 039 Right Wing

Therefore CP12A intentionally DOES NOT apply the motion to geometry yet. Applying a left-arm transform to today's merged `*_Arms` segment would animate both arms and produce false results.

## Next checkpoint contract
Create a node-preserving Chao mesh/rig representation from the original `AL_RootObject` hierarchy while preserving the already validated:
- vertices/normals/UV
- morph deltas
- material/submesh mapping
- Reflection
- lighting
- Animal Parts
- Head Deco
- shared/warm GPU cache

The intended runtime becomes:

Appearance/morph -> persistent shared VBO geometry
SA2 clip + time -> node pose matrices
node ownership -> draw-time transform

Animation pose/time MUST NOT enter `VisualKey` and MUST NOT rebuild VBOs per frame.

## Test / PASS
1. `./gradlew build`
2. Start client.
3. Startup log should include:
   `Preloaded 279 original SA2 Chao animation clips ...`
4. Open F8 -> Anim.
5. Selector should browse all 279 clips.
6. Play/Pause and frame scrub should update the lab clock/diagnostics smoothly.
7. Chao geometry is intentionally static in CP12A.
8. Body/Face/Parts/Test visuals and CP11R.18 performance must remain unchanged.
