# CP07.7 — Isolated Visual Lab + Spawn Workflow

## Visual Lab

- F8 opens one temporary client-only Chao using the production `ChaoRenderer`.
- The lab no longer selects or mutates Chao already living in the world.
- The preview is created when the screen opens and its renderer caches are released when the screen closes.
- Sliders edit only the virtual draft and remain debounced before rebuilding geometry.
- `Auto Rotate` can be toggled ON/OFF. With it OFF, drag the preview horizontally to rotate it manually.
- `Summon Chao` sends the current draft once to the authoritative server, which validates/rate-limits the request and spawns a normal synced Chao in front of the player.
- Matrix tools remain server-authoritative and unchanged.

## Multiplayer/performance contract going forward

- Gameplay simulation/lifecycle/AI/stats/relationships/genetics are server-authoritative.
- Rendering, morph preparation and VBOs are client-only.
- `simulation_distance_blocks` is the server-side gate for future Chao-owned simulation work.
- `view_distance_blocks` is the server tracking/view radius and is startup-only.
- Continuous server values must not force continuous mesh uploads. Client visual state remains quantized and VBO rebuilds are event-driven.
- Expensive future simulation systems should use staggered cadences/budgets instead of making every Chao run every expensive subsystem on the same tick.
- Lifecycle implementation must preserve the project rule that biological progression is driven by active server simulation and does not advance from offline wall-clock time.

The exact cadence/budget implementation is intentionally deferred until lifecycle/AI exists so SA2 behavior is not distorted by a speculative scheduler.
