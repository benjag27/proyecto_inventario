# Plan — Feature-004: Estética coherente de `ProductFormView`

> Issue: #36 · Depende de: `spec/features/004-estetica-formularios/spec.md`

## Enfoque

Feature **100% de presentación** en `ProductFormView` (+ `app.css`). Se reemplaza el uso de estilos de login por el esquema del módulo Productos, se unifica la estructura de los 5 modos, se presenta mejor los resultados de búsqueda y se fija un tamaño de ventana único (colabora con la Feature-003).

## Capas y archivos afectados

| Archivo | Cambio |
|---|---|
| `presentation/ProductFormView.java` | Cabecera con `menu-header`/`menu-subheader`/`logout-link`; estructura de formulario común; resultados de búsqueda en componente presentable; ancho de tarjeta y tamaños fijos por modo. |
| `presentation/SceneManager.java` | `showProductForm`: fijar tamaño **constante** del form (sin `sizeToScene` por modo ni re-escaleo). |
| `resources/styles/app.css` | Nueva(s) clase(s) si hace falta (p. ej. `form-card` y tabla/tarjeta de resultados). |

## Estrategia de implementación

1. **Cabecera:** en `createScene()` reemplazar `login-title/login-subtitle` por el mismo esquema que `ProductPanelView` (`menu-header`/`menu-subheader`), manteniendo la alineación con "← Volver".
2. **Estructura común:** extraer un layout de formulario único (tarjeta de ancho fijo, p. ej. 560px) con los campos apilados uniformemente; cada modo solo aporta sus campos/acciones al contenedor común (helper `buildFields()` ya separa por modo → normalizar el `VBox` de salida).
3. **Resultados presentables:**
   - Buscar por nombre → `TableView<Product>` compacta (solo lectura, sin columnas de más) dentro del propio form, o un `VBox` de tarjetas; reutilizar `productList`/observable.
   - Buscar por ID → mantener el aviso pero en un recuadro de resultado estilizado (no un label plano).
4. **Tamaño fijo:** `SceneManager.showProductForm` usa un ancho/alto únicos para toda operación (ya no `sizeToScene`); quitar el re-escaleo por modo.
5. **Estilo:** agregar en `app.css` las clases (ej. `form-card`, `result-table`) para respetar el tema oscuro.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Alinear 5 modos sin romper el prefill | Mantener `formModificar/formBaja` con prefill intacto; el cambio es solo layout. |
| Cambiar estética rompe tamaño de contenido | Probar con los textos más largos (nombre de producto) para que nada se corte. |
| Re-escaleo previo por modo ocultaba layout roto | Con tamaño fijo, verificar cada modo a 760×540 (o el tamaño elegido). |
| Duplicar estilos de login | No reutilizar `login-*`; crear/alinear clases de módulo. |

## Verificación

- `mvn clean verify` (compila + tests).
- Prueba visual de los 5 modos: igual estructura, tema oscuro, tamaño estable y sin regresión en el panel.
- Regresión funcional: alta/modificar/baja/búsquedas siguen operando igual.

## Cierre

- Actualizar `CHANGELOG.md` (entradas `[ADD]/[CHANGE]`).
- Marcar `done` en `roadmap.md` y cerrar el issue #36.