ChaoCraft CP12E — Native SA2 GPU Skinning, Dedicated Vertex Attributes

Goal
----
Replace the temporary CPU Reference with stable draw-time GPU skinning while
preserving the approved CP11R.18 / pre-CP12D production renderer.

This checkpoint enables native GPU skinning in F8 Child preview only.
Production/world animation remains intentionally deferred until F8 validation.

Verified math
-------------
The Child vertex pipeline is:

    FlipZ (prepared morph geometry)
    then RotateX(+90) (renderer local transform)

Therefore:

    Mtotal = RotateX(+90) * FlipZ
    Dgpu   = Mtotal * Dviewer * inverse(Mtotal)

Offline Mirror v4 against Blender direct al_ncn:
    total RMS ~0.000007 .. 0.000011
    max error ~0.000060

CPU Reference in Minecraft visually confirmed Sprint assembly and shading.

Dedicated VBO transport
-----------------------
CP12D's UV1/Overlay hijack has been removed.

Skinned preview VBO format:
    Position
    Color
    UV0
    UV1 = real Minecraft Overlay
    UV2 = real Minecraft Light
    UV3 = packed SA2 influence 0
    UV4 = packed SA2 influence 1
    Normal
    Padding

Each packed influence remains:
    bits 0..5  = SA2 bone 0..39
    bits 6..15 = 10-bit weight

The visual state still owns the VBO. Animation clip/time/frame are draw-time
uniform state only and never enter VisualKey.

Preview shader
--------------
chao_skinning extends Minecraft's ordinary entity vertex lighting logic only
with SA2 position/normal skinning. The fragment stage follows the ordinary
entity preview path so F8 retains the approved shading instead of CP12D's
flat custom material preview.

Animation playback
------------------
The CPU diagnostic's integer-frame floor/cached-frame limitation is gone.
ChaoAnimationPose is sampled at the actual fractional animationFrame again,
so interpolation can be smooth without rebuilding geometry.

Safety / scope
--------------
- F8 Child body: GPU skinned.
- Reflection passes are left on the already-approved reflection shader in this
  checkpoint; they remain static while validating the ordinary Child skin path.
- Animal Parts / Head Deco / emotion ball remain on their approved paths.
- World production Chao remain static and keep the approved performance cache.
- No frame VBO cache and no per-frame VBO rebuild.

PASS
----
1. Client starts and logs:
       Loaded Chao native SA2 skinning shader.
2. F8 Body shading still matches the approved preview.
3. F8 Anim -> 037 Sprint remains assembled/correct like CPU MTOTAL.
4. Playback is smooth again (fractional-frame interpolation).
5. Scrubbing/playback does not generate repeated VBO build pressure.
6. World rendering/performance remains unchanged.
