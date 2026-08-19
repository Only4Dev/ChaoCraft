ChaoCraft CP12H.2.1 — Shared GPU Warmup + Matrix Isolation

Log validation
--------------
CP12H.2 passed the important stability target:
- all shared Animal Parts warmed once,
- no native OOM / JVM crash,
- warmed attachments survived disconnect/rejoin inside the same Minecraft session,
- later stress still produced body-cache churn, but not the old fatal attachment/reflection cascade.

Warmup
------
One render-thread startup warmup now prepares every finite reusable visual resource:

1. Renderer-local references for all base models:
   - Child
   - every Adult family
   - every Chaos family
   - Neutral/Hero/Dark base Normal models
   - the three Emotion base models

   Their CPU mesh arrays were already globally preloaded by ChaoMeshRepository;
   this step resolves all renderer-local lookups before gameplay.

2. Every HeadDeco shared VBO type.

3. Every real Child AnimalPart animal+slot shared VBO.

4. Every real Adult AnimalPart animal+slot shared VBO.

Catalog combinations that do not exist are skipped automatically.

The warmup is finite. Slider/morph/body combinations are NOT generated.

If native allocation pressure occurs during warmup, it stops safely, retains
everything already warmed, and remaining resources use the existing lazy path.

Lifetime
--------
Shared VBOs remain resident across world disconnect/rejoin in the same Minecraft
session. They are recreated only after a real resource/hard-cache reload or a
new Minecraft process, because OpenGL VBOs cannot persist across process/context
destruction.

Logs are summarized instead of printing hundreds of warm lines:
  [Performance] Shared GPU warmup complete:
      N base model refs,
      N HeadDeco types,
      N AnimalPart keys,
      N KiB shared VBOs in N ms

Color / Reflection Matrix bug
-----------------------------
Color Matrix and Reflection Matrix now explicitly clear HeadDeco from the
Visual Lab draft before spawning their grids, matching Animal Matrix isolation.

The matrices still preserve family/type/evolution/face settings that are useful
for their respective visual audits; only unrelated HeadDeco is removed.

Expected PASS
-------------
- Startup reports one Shared GPU warmup summary.
- First use of ordinary Child Animal Parts / HeadDeco should not print new
  "Warmed shared ..." lines because they are already resident.
- Exit world -> re-enter: no second warmup and shared attachments remain resident.
- Color Matrix spawned while preview wears a hat: matrix has NO hats.
- Reflection Matrix spawned while preview wears a hat: matrix has NO hats.
- Animal Matrix remains hat-free.
- Existing animation, reflection, HeadDeco and AnimalPart visuals unchanged.
- No exhaustive slider/morph body preload is introduced.
