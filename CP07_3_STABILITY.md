# CP07.3 — Renderer Stability + Responsive Visual Lab

This checkpoint hardens the visual foundation before adding more Chao systems.

## Runtime boundaries

- **Server authority:** real `ChaoEntity` appearance/state changes are accepted and applied on the server, then synchronized through Minecraft's tracked entity data.
- **Client rendering:** `.cmesh` loading, morph preparation, VBOs, materials, preview rendering and QA telemetry live only under `src/client`.
- **Visual Lab preview:** the preview is a temporary client-only Chao that is never spawned into the world. It uses the exact production `ChaoRenderer` but has its own UUID/cache identity.
- **Future gameplay:** lifecycle, stats, emotions, AI, relationships, genetics, breeding and progression must run in common/server code. Clients consume synchronized presentation state; they do not decide gameplay results.

## Stability changes

- Body yaw/preview rotation is applied at draw time and no longer belongs to the VBO cache key.
- GPU cache is bounded to two light variants per Chao and stale variants are explicitly closed.
- Preview, disconnect and resource reload paths explicitly release renderer caches.
- CPU morph cache can release temporary preview arrays immediately.
- BufferBuilder capacity is estimated up front instead of repeatedly growing native buffers.
- A single render batch is capped at 16 MiB; malformed oversized assets are skipped.
- `.cmesh` runtime array allocation is capped at 128 MiB per model before allocation.
- Visual state constructors sanitize NaN/infinite/out-of-range values and enforce the 100-point evolution budget.
- Visual Lab slider changes are quantized; preview appearance updates are coalesced to game ticks.
- Real-entity Visual Lab packets are coalesced to 10 Hz and server-side dev endpoints have an additional rate limit.
- Visual Lab is responsive: controls and production-renderer preview share the available logical GUI space instead of depending on Minecraft GUI Scale.
- Visual Lab shows VBO builds/sec, cached entity/light variants, estimated cached VBO MiB and state packets/sec.

## Acceptance test

1. Run `./gradlew build` (or `.\\gradlew build` on Windows).
2. Open a test world and F8 at your normal Minecraft GUI Scale. Controls and preview should both be visible.
3. Leave Visual Lab open and untouched for at least 5 minutes. After initial warmup, `VBO` should settle at `0/s`.
4. Move the mouse around the preview continuously. Rotation alone should keep `VBO` at `0/s`.
5. Drag evolution/alignment/age sliders aggressively for 2–3 minutes. The game must remain stable; `net` should normally stay at or below ~10/s.
6. Close/reopen F8 repeatedly. Preview cache entries must not accumulate.
7. Spawn the 15-family matrix, walk/turn around it, then clear it. Rotation/camera movement must not cause continuous VBO rebuilds.
8. Reload resources (F3+T), reopen the lab and verify assets reload normally without stale VBOs.
9. For multiplayer validation, run a dedicated Fabric server with ChaoCraft and two clients. The server remains authoritative; edits from an allowed Visual Lab user should synchronize to the other client, while ordinary clients never own Chao gameplay state.

Do not start CP08 until this stress pass is stable.
