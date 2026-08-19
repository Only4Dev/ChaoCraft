CP11R.8 — Draw-Time Lighting + OOM Recovery

Extraer directamente sobre C:\Dev\ChaoCraft, reemplazar, Gradle Build + Run.
No BAT, scripts ni git apply.

- Luz deja de generar VBO variants cuando usamos los shaders ChaoCraft.
- Día/noche/antorchas cambian uniforms/lightmap al dibujar.
- Nuevo chao_material shader para base/materials/animal parts.
- Reflection usa LightUv filtrado; elimina texelFetch/flat lighting.
- BufferBuilder ya no recibe el byte estimate como initialCapacity.
- Si falla una reserva nativa: se cierran batches parciales, se limpia cache idle
  y se pausan builds 5 s; el Chao puede quedar invisible temporalmente en vez de crash.
- Iris con shaderpack conserva fallback light-keyed por compatibilidad.

TEST:
1) Reflection Matrix de noche.
2) Colocar/quitar muchas antorchas.
3) shared entries no debe crecer sólo por cambiar luz.
4) builds/deferred no deben dispararse por cada antorcha.
5) iluminación más suave.
6) cambiar día/noche varias veces.
7) sin crash.
8) enviar latest.log.
