# CP11 — Reflection parity + renderer hardening + performance logger

## Renderer hardening
- GPU resources are shared by complete visual fingerprint instead of duplicated per entity UUID when appearance/light match.
- One entity no longer keeps two full lighting variants. Obsolete unique bindings are released before replacement allocation.
- Shared VBO cache: 64 MiB hard estimated budget, pre-eviction reserves 16 MiB before cache misses.
- Expensive VBO cache misses are paced to at most one every ~50 ms; cache hits are unlimited. A newly visible Chao can briefly render fallback while its build waits instead of creating an allocation burst.
- Large compatible material groups are split into ~2 MiB BufferBuilder chunks. A normal render build should no longer request the 6.5 MiB contiguous builder seen in the Animal Matrix crash.
- Matrices are spawned server-side at 2 Chao/tick rather than 34/75 entities in one tick.

## Reflection
- Keeps the Viewer ReflectionT parameters already ported (`_Ref` / `_Emission` semantics).
- Cubemap strips are now sampled using an actual reflection vector approximation (R = V - 2*dot(V,N)*N) rather than using the surface normal directly as a painted texture coordinate.
- This is intentionally renderer-side only; reflection choice remains authoritative/persistent Chao appearance state.

## Performance logger
F8 -> Test:
- `Start Perf Log` / `Stop Perf Log`
- `Perf Snapshot`

Sessions are written to:
`run/logs/chaocraft-profiler/session-YYYYMMDD-HHMMSS/`

Upload the whole session folder after a stress test:
- `metrics.csv`
- `events.log`
- `summary.txt`

Metrics include FPS, client Chao count, VBO builds/deferred/evictions per second, cache bindings/shared entries/MB, cache hits/misses, average Chao renderer CPU time, bytes uploaded, reflection renders, JVM heap, JVM direct/native buffer pool, and pending matrix spawns.

## Recommended stress test
1. F8 -> Test -> Start Perf Log.
2. Spawn Animal Matrix (34), walk around it, place/remove torches for 2-3 minutes.
3. Spawn Adult Extremes (75), walk/rotate camera for 2-3 minutes.
4. F3+T once.
5. Clear Matrix.
6. Stop Perf Log.
7. Send the generated profiler session folder.
