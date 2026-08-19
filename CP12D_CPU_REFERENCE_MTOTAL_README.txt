ChaoCraft CP12D — Final CPU Reference MTOTAL Test

Purpose
-------
Last diagnostic before touching the final GPU skin architecture.

Base
----
This patch is applied on top of the CPU Reference Diagnostic that already
restored the approved pre-CP12D F8 shading and production render/cache path.

No experimental GPU skinning is enabled.

Critical correction
-------------------
The previous CPU reference accounted for Child RotateX(+90) but forgot that
ChaoRenderCache.prepareSegment() first performs the approved Unity->Minecraft
handedness conversion:

    z  = -z
    nz = -nz

Therefore the actual raw Viewer -> Child VBO transform is:

    F       = FlipZ
    R       = RotateX(+90)
    M_total = R * F

CPU skin palette now uses exactly:

    D_render = M_total * D_viewer * inverse(M_total)

Offline Mirror v4 result against Blender direct al_ncn GOLDEN:
    CURRENT RMS: ~1.10 .. 1.53
    FULL FIX RMS: ~0.000007 .. 0.000011
    FULL FIX MAX: ~0.000060

All Arms/Belly/Head/Legs/Tail/Wings converged to numerical noise.

OOM protection
--------------
The previous CPU diagnostic rebuilt a transient VBO for every fractional
animationFrame and could make the OpenGL driver retain deleted buffers long
enough to hit GL_OUT_OF_MEMORY.

This test:
- evaluates exact integer source frames only,
- caches a maximum of 12 diagnostic frame VBO sets,
- Sprint needs only 6,
- after the first Sprint loop, it performs zero additional frame VBO builds,
- cache is fully closed when leaving/changing the diagnostic context.

This is NOT the final runtime animation architecture. It is a correctness test.

PASS
----
F8 -> Body:
- approved shading remains unchanged.

F8 -> Anim -> Sprint 037:
- Child pose should match Blender GOLDEN / SA Tools.
- Arms, legs, wings, head, belly and tail stay assembled and move correctly.
- First loop logs at most six:
    [CP12D CPU REF MTOTAL] Built diagnostic frame ...
- subsequent Sprint loops should not keep building VBOs.
- no GL_OUT_OF_MEMORY / continuous VBO pressure caused by the diagnostic.

Production world Chao remains on the approved static performance path.
