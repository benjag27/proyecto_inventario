# Plan — Feature-003: Transiciones suaves

> Issue: #35 · Depende de: `spec/features/003-transiciones-suaves/spec.md`

## Enfoque

Feature **100% de presentación**, centralizada en `SceneManager`. Todas las vistas (login, menú, productos, formularios, auditoría) pasan por `show(Scene, String)` (`SceneManager.java:64`), así que una única animación de entrada resuelve la transición de **Productos ↔ operaciones** y deja el resto consistente por igual.

No se tocan las vistas individuales.

## Capas y archivos afectados

| Archivo | Cambio |
|---|---|
| `presentation/SceneManager.java` | En `show(...)`: aplicar `FadeTransition` (opcional `TranslateTransition` leve) sobre el `root` de la escena antes/después de `stage.setScene`. Ajustar `showProductForm` para que el acomodo de tamaño sea menos brusco. |
| `resources/styles/app.css` | Solo si se necesita una clase para el nodo raíz animado (opcional). |

## Estrategia de implementación

1. **Fade-in en `show(...)`:** al recibir el `Scene`, tomar `scene.getRoot()` y crear un `FadeTransition` (0→1) de ~250 ms que arranca al `setScene`. El fade cura el "corte seco" para toda navegación.
2. **Redimensionado suave:** en `showProductForm`, evitar el `sizeToScene()` brusco; usar un **tamaño estable por modo** (o un ancho fijo configurado) y animar `stage.setWidth/Height` con `Timeline` durante ~200 ms, o simplemente fijar un tamaño único de form (ver Feature-004) para que no haya re-escaleo.
3. **Regreso al panel:** `ProductPanelView` entra con el mismo fade; al ser más grande, no se percibe cortó.
4. **Verificación:** probar ida/vuelta por cada botón de operaciones.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Parpadeo (flickering) al cambiar de escena | Animar la escena **nueva** con fade desde opacidad 0; JavaFX lo hace con fluidez; verificar en ambos SO. |
| Ventana "flotante" si se anima el resize | Usar `Timeline` sobre `stage.widthProperty/heightProperty` o escala estable; nunca dejar el stage en estado intermedio. |
| Transición lenta en equipos viejos | Mantener duración breve (≤400 ms) y sin efectos costosos (solo opacidad/translación). |
| Afectar login/menú por error | La animación se aplica a escenas nuevas; si algo falla, se encapsula en `try/catch` y se muestra la escena igualmente. |

## Verificación

- `mvn clean verify` (compila + tests).
- Prueba manual por cada botón de operaciones: panel → form → volver, sin parpadeos ni saltos.
- Revisar que login, menú principal y auditoría sigan funcionando.

## Cierre

- Actualizar `CHANGELOG.md` (entradas `[ADD]/[CHANGE]`).
- Marcar `done` en `roadmap.md` y cerrar el issue #35.