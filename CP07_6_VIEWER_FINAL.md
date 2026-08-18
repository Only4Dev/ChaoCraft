# CP07.6 — Server distances + Visual Lab preview finalization

## Server configuration

On first launch ChaoCraft creates:

`config/chaocraft-server.properties`

```properties
simulation_distance_blocks=80
view_distance_blocks=80
```

- `simulation_distance_blocks`: additional server-authoritative radius around any non-spectator player in which ChaoCraft-owned AI/lifecycle/stat simulation is allowed to run. `0` disables the extra ChaoCraft cap and follows Minecraft's normal loaded/ticking chunks.
- `view_distance_blocks`: server entity tracking distance for Chao. `80` preserves the currently validated vanilla-like 5-chunk baseline. This is read while the Chao entity type is registered, so changing it requires restarting the game/server.
- Distances are clamped defensively to a maximum of 1024 blocks.

The client does not decide gameplay simulation. A dedicated server owns the simulation gate and the tracking distance.

## Visual Lab

F8 still opens the development-only Visual Lab.

The right side now renders an isolated client-only copy of the current target through the production `ChaoRenderer`. The renderer composes Minecraft's GUI model-view transform before issuing the persistent VBO draw, fixing the previously empty preview panel.

The preview rotates automatically through 360 degrees. Rotation is a draw-time presentation transform and does not invalidate/rebuild Chao geometry.

Expected idle behavior after warm-up: `VBO 0/s`.

## Architecture boundary

Server/common:
- persistent Chao state
- future AI/lifecycle/stats/emotions/relationships/breeding
- simulation distance gate
- entity tracking/view distance

Client only:
- morph/material renderer
- VBO/cache
- Visual Lab and preview

The Visual Lab remains a QA tool and can be disabled/removed from release builds later without moving gameplay authority to the client.
