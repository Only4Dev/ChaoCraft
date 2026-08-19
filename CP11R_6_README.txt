CP11R.6 — Reflection Composition + Cubemap Seam Fix

Aplicar:
- Extraer sobre C:\Dev\ChaoCraft
- Reemplazar archivos
- Gradle Build + Run en IntelliJ

Cambios:
- Reflection se dibuja SIEMPRE después de todos los materiales/masks base.
- Gold/Silver/Jewels (Ref=1) cubren completamente el material reflectivo.
- Shiny/TTMetal (Ref=.4) conservan parte del color base, como Viewer.
- Sampling de las seis caras ahora mezcla cerca de seams para aproximar samplerCube seamless.
- Se elimina el spam repetido de "Loaded Chao Viewer-style reflection shader".
- No cambia geometría, animal parts, colores base ni cache/performance architecture.

PASS:
- Gold: body/belly/horns reflectivos deben quedar dorados, sin barriga azul encima.
- Wings y ojos mantienen sus materiales normales.
- Shiny conserva azul/verde de base, pero con reflection suave.
- Al rotar cámara no deben aparecer grandes cortes/polígonos entre caras del cubemap.
