CP11R.7 — Final Reflection Lighting

Aplicar:
- Extraer sobre C:\Dev\ChaoCraft
- Reemplazar archivos
- Gradle Build + Run en IntelliJ

Qué cambia:
- Reflection shader ahora usa la LIGHTMAP REAL de Minecraft (Sampler2).
- Gold/Silver/Jewels responden a día/noche, cuevas y block light.
- Se elimina el piso artificial de 38% de brillo que los hacía parecer iluminados de noche.
- Viewer _Emission se conserva:
  * Shiny: 0.5 -> superficie bastante autoiluminada.
  * Gold/Silver/Jewels/etc.: 0.1 -> sólo leve autoiluminación.
- Emission NO crea luz física alrededor del Chao; no es una antorcha.
- No cambia el reflection cubemap, composición Ref ni geometría.

PASS:
1. Día: reflections deben conservar el aspecto validado en CP11R.6.
2. Noche: Gold/Silver/Jewels deben oscurecerse claramente con el entorno.
3. Cerca de una antorcha: deben aclararse como otras entidades.
4. Shiny debe seguir visible/luminoso en oscuridad por _Emission=0.5.
5. No debe aparecer iluminación de bloques alrededor del Chao.
