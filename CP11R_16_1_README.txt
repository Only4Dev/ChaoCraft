CP11R.16.1 — Compile Hotfix

Apply directly over C:\Dev\ChaoCraft, replace files, then:
    .\gradlew build

Fixes only:
- Missing java.util.HashMap import in ChaoRenderer.
- HeadDeco MaterialSpec fallback updated from old 5-arg constructor
  to current 7-arg constructor: reflective=false, emission=0.

No behavior, rendering, cache, anchor, or performance logic changed.
