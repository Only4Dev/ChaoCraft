CP12B.1 — Animation Axis + Persistent Speed Hotfix

REQUIRES
- CP12B

APPLY
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Run: .\gradlew build
- Run Client

FIX 1 — SA2 -> ChaoCraft ANIMATION SPACE
The Child Chao mesh is baked through +90 degrees X before GPU upload.
CP12B applied raw SA2 node deltas directly to those already-converted vertices.

CP12B.1 conjugates each SA2 delta into the Child render coordinate space:

    renderDelta = C * sa2Delta * inverse(C)

where C is the same +90 degrees X transform used by the Child mesh renderer.

This keeps:
- original 40-node hierarchy
- original bind pivots
- original SA2 keyframes
- draw-time matrices
- stable VBOs

No geometry is rebuilt per animation frame.

FIX 2 — PER-CLIP SPEED TUNING
F8 -> Anim now has a Speed slider:
- range: 0.05 .. 2.00
- default: 0.50
- 2 decimal precision
- saved immediately per clip

Persistent debug file:
    config/chaocraft-animation-lab.properties

Example:
    037.animation_0028D358=0.50

The imported raw animation JSON is never modified.
These are lab tuning overrides which can later feed the final MotionTable runtime.

PLAYBACK
Minecraft base preview cadence:
    1.5 SA2 frames / Minecraft tick

Effective cadence:
    1.5 * perClipSpeed

Therefore Speed 0.50 = 0.75 source frames/tick = 15 source frames/sec
at Minecraft's normal 20 TPS.

TEST
1. F8 -> Anim
2. Neutral / Normal Child
3. Select 037 animation_0028D358
4. Speed = 0.50
5. Restart, then Play
6. Compare movement axes/pivots with SA Tools.
7. Pause/scrub individual frames if any limb still disagrees.

PASS
- Sprint visually follows the same axis/orientation as SA Tools.
- speed 0.50 feels equivalent to SA Tools speed 0.50.
- changing speed persists after closing/reopening the game.
- animation playback shows VBO 0/s after warmup.
