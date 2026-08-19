CP11R.5 — Viewer Reflection Shader + Build Queue

APLICAR:
- Extraer este ZIP directamente sobre C:\Dev\ChaoCraft
- Aceptar reemplazar archivos.
- Gradle Build en IntelliJ.
- Run Client.

NO hay BAT, scripts ni git apply.

CAMBIOS:
- Reflection cubemap se samplea en draw-time con normal + cámara actual.
- Usa cubemaps estáticos del Viewer; NO refleja el mundo Minecraft.
- _Ref sigue siendo 0.4 para Shiny/TTMetal y 1.0 para jewels/metals.
- _Emission sigue siendo 0.5 para Shiny y 0.1 para los demás.
- Emission es auto-iluminación visual, no block/dynamic light.
- Iris se detecta sin dependencia. Si un shaderpack está activo, se usa fallback RenderLayer.
- Build misses pasan por FIFO deduplicado: cada visual state entra una vez en cola.
- Se conserva el pacing de 50 ms para evitar bursts de VBO/native allocation.
- No se toca Animal Matrix/suffocation.

TEST:
1. Build debe compilar.
2. Reflection Matrix: Gold/Silver/Jewels deben reaccionar al mover cámara, no verse pegados.
3. Shiny debe verse más auto-iluminado/suave que jewel reflection.
4. Ojos/boca no deben recibir reflection.
5. Invocar matrices grandes y observar warm-up.
6. deferred/s debe caer drásticamente respecto a miles de intentos/s.
7. Save/Quit/Rejoin varias veces.
8. Enviar latest.log y una captura de Gold + Shiny.
