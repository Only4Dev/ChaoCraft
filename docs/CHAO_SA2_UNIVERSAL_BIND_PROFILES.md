# ChaoCraft — Universal SA2 Chao Rig / Bind Profiles

CP12I.1 establishes the data architecture for animating every SA2 Chao model
without replacing the already-approved Child runtime.

## Proven universal contract

The 114 audited `AL_RootObject/*.sa2mdl` Chao models contain exactly 40 nodes
and share the same parent/child topology, mesh-bearing node pattern, NoDisplay
pattern and NoAnimate pattern. The same SA2 `.saanim` clips target those 40
channels.

Therefore ChaoCraft uses one semantic/index Rig Bible (`0..39`) plus one bind
profile per source model.

## Per-model profile data

Each profile stores, for every node:

- bind position
- bind BAMS rotation converted once to radians
- bind scale
- NoPosition / NoRotate / NoScale metadata
- RotateZYX metadata
- precomputed bind-world matrix
- precomputed inverse-bind-world matrix

The matrices are process-lifetime immutable CPU data; they are not rebuilt per
frame, entity, world, slider state or VBO state.

## CP12I.1 safety boundary

The existing Child animation path still calls the original
`ChaoSa2RigDefinition` implementation. A new profile-aware `ChaoAnimationPose`
overload exists but nothing in the renderer selects it yet.

At startup the 114 profiles are parsed once and `al_ncn` is validated against
the exact DAE-derived Child golden with a 2e-6 component tolerance. A failed
atlas load cannot break Child rendering; it logs the failure and leaves the
legacy path active.

CP12I.2 can therefore opt exactly one Adult model into the profile-aware sampler
while Child remains the control group.
