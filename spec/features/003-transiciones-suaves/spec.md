# Feature-003 — Transiciones suaves entre Productos y sus operaciones

> Carpeta: `spec/features/003-transiciones-suaves/` · Issue: [#35](https://github.com/benjag27/proyecto_inventario/issues/35) · Estado: `backlog`

## Objetivo

Que el paso de la pantalla de **Productos** hacia sus **operaciones** (alta, modificar, dar de baja, búsquedas) y el regreso al panel **no sea un corte seco**: hoy la escena se reemplaza de golpe y además la ventana se reescala bruscamente (`1300×760` → `760×540`).

## Contexto y flujo actual

La navegación pasa por `SceneManager.show(...)`, que hace `stage.setScene(scene)` **sin ninguna animación** (`SceneManager.java:64-68`):

- `ProductPanelView` (1300×760) → `ProductFormView` (760×540): cambio instantáneo, sin transición.
- Volver (`← Volver`) hace lo inverso, también de golpe.
- `SceneManager.showProductForm` además ejecuta `freeSize()` → `stage.sizeToScene()` → `lockSize(...)` (`SceneManager.java:47-54`), lo que **redimensiona la ventana al instante**, reforzando la sensación "violenta".

Todo el cambio se puede centralizar en `SceneManager` gracias a que todas las vistas pasan por `show(Scene, String)`.

## Comportamiento esperado

- Al entrar (o navegar) cada escena hace una entrada suave: **fade-in** (y/o deslizamiento leve) del contenido de la nueva escena.
- La sensación es de continuidad entre panel y formularios de operaciones (ida y vuelta), sin parpadeos ni saltos.
- La ventana acomoda su tamaño de forma **menos abrupta** (mantener ancho similar o animar el ajuste), sin perder el layout.
- Aplica de forma `backlog` en cadena y consistente a **productos ↔ operaciones**.

## Criterios de aceptación

- [ ] **CA1** — `SceneManager.show` aplica una transición suave de entrada (fade y/o deslizamiento) a la escena nueva.
- [ ] **CA2** — La transición dura entre ~200 y ~400 ms, sin bloquear la interacción ni pestañear.
- [ ] **CA3** — Navegar panel → operaciones y operaciones → panel se siente continuo en ambos sentidos.
- [ ] **CA4** — El re-escaleo de ventana (`1300×760` ↔ `760×540`) no produce salto brusco.
- [ ] **CA5** — Sin regresiones: login, menú y auditoría siguen navegando correctamente (la transición es global pero no cambia comportamiento).
- [ ] **CA6** — No hay flickering ni contenidos que "parpadeen" al redimensionar.

## Fuera de alcance

- No cambia el layout ni el tamaño final de las escenas (eso es Feature-004).
- No modifica lógica de negocio ni de aplicación.

## Estado de implementación

- [ ] Pendiente de aprobación del `spec.md` → pasar a `in-progress`.