# Feature-004 — Estética coherente de `ProductFormView` (escenas de operaciones)

> Carpeta: `spec/features/004-estetica-formularios/` · Issue: [#36](https://github.com/benjag27/proyecto_inventario/issues/36) · Estado: `backlog`

## Objetivo

Pulir la vista de `ProductFormView` para que todas las **escenas derivadas de los botones del panel** (dar de alta, modificar, dar de baja, buscar por ID, buscar por nombre) tengan una **estética ajustada y coherente** con el módulo, en lugar de reusar el estilo del login de forma improvisada.

## Contexto y flujo actual

`ProductFormView.createScene()` reutiliza estilos de login (`login-card`, `login-title`, `login-subtitle` — `ProductFormView.java:46-69`) y:

- Escena dura `760×540` siempre, y `SceneManager.showProductForm` la reescala por modo (`sizeToScene` + `lockSize`) → cada operación puede abrir una ventana distinta.
- El contenido del formulario es un rectángulo de 480px centrado, sin estructura de cabecera como la del panel (`menu-header`/`menu-subheader`).
- Los **resultados de búsqueda** se vuelcan en un único `Label` multilínea (`lblMessage`, con "ID: X | Nombre | Stock" separados por `\n`) — aceptable como aviso, pobre como presentación de resultados (`ProductFormView.java:215-237`).
- Los campos y botones se apilan (`new VBox(12, ...)`) sin alineación tipográfica consistente entre modos.

## Comportamiento esperado

- **Encabezado consistente:** título + subtítulo + "← Volver" en el mismo esquema visual que `ProductPanelView` (sistema `menu-header`/`menu-subheader`/`logout-link`), no el del login.
- **Formularios alineados:** misma estructura, espaciado y ancho de campos/etiquetas entre los 5 modos; botones de acción con el mismo estilo y tamaño.
- **Resultados presentables:** las búsquedas muestran resultados en un componente acorde a la app (tabla/listado compacto o tarjeta), no un label crudo con textos separados por `\n`.
- **Tamaño estable:** los 5 modos comparten **el mismo tamaño de ventana** (sin re-escaleo brusco por modo — refuerza la Feature-003).
- Se respeta el tema oscuro existente (`app.css`); no se cambia ningún comportamiento funcional.

## Criterios de aceptación

- [ ] **CA1** — La cabecera de los formularios usa el esquema del módulo (título/subtítulo/volver coherentes con `ProductPanelView`).
- [ ] **CA2** — Los 5 modos usan la **misma estructura visual**: mismo ancho de tarjeta, mismo espaciado y campos alineados.
- [ ] **CA3** — Los resultados de búsqueda (por ID con datos, por nombre con lista) se muestran en un componente presentable, no en un `Label` multilínea crudo.
- [ ] **CA4** — La ventana mantiene un **tamaño constante** entre modos (sin re-escaleo brusco por operación).
- [ ] **CA5** — Se respeta el tema oscuro (`app.css`); no hay regresión visual en el panel de productos.
- [ ] **CA6** — El comportamiento de cada operación (alta/modificar/baja/búsquedas) **no cambia** (sin regresión funcional).

## Fuera de alcance

- No toca `domain`/`application`/`infrastructure`.
- No redefine los flujos de cada operación (eso es Feature-001 para la baja).

## Estado de implementación

- [ ] Pendiente de aprobación del `spec.md` → pasar a `in-progress`.