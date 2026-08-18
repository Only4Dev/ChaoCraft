# CP07.5 — Vanilla entity tracking parity

- Removes the prototype 10-block Chao tracking cap.
- Chao now use a 5-chunk tracking range, matching the vanilla EntityType builder baseline for normal living entities.
- Client rendering/culling remains vanilla-driven; no custom unlimited renderer is enabled by default.
- `trackedUpdateRate(3)` is unchanged.

## QA

Place Chao and vanilla mobs at comparable distances. Walk backward/forward and verify the Chao no longer disappear around the old ~10-block boundary.
