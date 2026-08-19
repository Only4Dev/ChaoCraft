CP12B — First Original SA2 Motion Playback

REQUIRES
- CP11R.18
- CP12A + CP12A.1 (279 lowercase animation JSON resources already installed)

APPLY
- Extract directly over C:\Dev\ChaoCraft
- Replace files
- Run: .\gradlew build
- Run Client

WHAT THIS CHECKPOINT DOES
- Adds the original 40-node AL_RootObject bind hierarchy extracted from
  al_ncn.sa2mdl (Neutral / Normal Child baseline).
- Samples SA2 Position + Rotation keyframes at draw time.
- Uses animatedWorld * inverse(bindWorld) rigid-node transforms.
- Sparse keyframes are interpolated; Ninja binary angles use shortest-path
  angular interpolation.
- F8 Animation Lab now applies the selected clip/frame to the preview.
- Arms, Legs and Wings are split LEFT/RIGHT only when the isolated F8 preview
  VBO is built:
    node 3  Left Arm
    node 10 Right Arm
    node 6  Left Leg
    node 13 Right Leg
    node 37 Left Wing
    node 39 Right Wing
  Tail -> node 8
  Head -> node 16
  Belly/body -> node 1
- The split uses disconnected triangle centroid X and changes no geometry.

PERFORMANCE CONTRACT
- Animation frame/time is NOT part of VisualKey.
- Animation does NOT rebuild/upload VBOs per frame.
- Only draw-time matrices change while playing/scrubbing.
- Production/world cache remains CP11R.18 merged/unrigged in this checkpoint.
  This intentionally avoids increasing world draw calls while the SA2 rig math
  is being validated in F8.
- Final gameplay animation will use a GPU node-palette path after validation,
  not per-node production draw-call multiplication.

CURRENT INTENTIONAL LIMITS
- al_ncn is the first bind-pose reference. This exactly targets the current
  Neutral/Normal Child baseline used for first validation.
- Other Chao share the same 40-node hierarchy/motions, but their bind pivot
  positions vary by evolution model. Those bind pivots will be interpolated
  with appearance morphs in the next rig-generalization checkpoint.
- Animal Parts, Head Deco and emotion extras are not node-bound yet in CP12B;
  validate the naked/base Chao motion first.
- MotionTable loop/PlaySpeed/transition semantics are not connected yet.
  F8 still uses its lab playback clock.

FIRST VALIDATION
1. Open F8 -> Anim.
2. Use a Neutral / Normal Child baseline (the normal default preview).
3. Select export 037 / animation_0028D358 (Chao Sprint).
4. Press Play.
5. Expected:
   - body bobs/moves;
   - left/right arms animate independently;
   - left/right legs animate independently;
   - tail and wings follow their SA2 nodes;
   - head follows the inherited hierarchy;
   - animation loops without VBO builds every frame.
6. Pause and scrub Frame:
   - pose should change immediately without VBO/cache churn.
7. Compare Sprint against SA Tools.

IMPORTANT
This checkpoint is a rig/math validation checkpoint. If the motion is mirrored,
rotated on the wrong axis, or a pivot is wrong, send screenshots/video/frame
comparison. That gives us a precise coordinate/order correction before the
same runtime is generalized to all 279 clips and all appearance bind profiles.
