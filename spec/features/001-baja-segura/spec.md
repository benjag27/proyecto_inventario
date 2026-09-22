# Feature-001 — Dar de baja seguro (búsqueda y confirmación de ID)

> Carpeta: `spec/features/001-baja-segura/` · Issue: [#32](https://github.com/benjag27/proyecto_inventario/issues/32) · Estado: `backlog`

## Objetivo

Redefinir el flujo del botón **"Dar de baja"** del módulo Productos para que sea imposible eliminar un producto por error:

1. Sin selección previa → permitir **buscar** el producto a eliminar **por nombre o por ID**.
2. Con selección previa → **no permitir modificar el ID** mientras se elimina.
3. En todos los casos → exigir una **confirmación explícita** antes del borrado.

## Contexto y flujo actual

En `ProductPanelView` el botón "Dar de baja" solo funciona si hay una fila seleccionada; si no, muestra el aviso *"Seleccioná un producto de la lista para dar de baja"* y no ofrece alternativa.

`ProductFormView` (modo `BAJA`) deja **editable el campo ID** a mano, incluso cuando llega un producto pre-cargado por selección (`prefill`). Un usuario apurado puede tipear un ID distinto y borrar el producto incorrecto.

## Comportamiento esperado

- **Sin selección:** al pulsar "Dar de baja" se abre una búsqueda del producto a eliminar:
  - **Por nombre:** coincidencia parcial, reutilizando `FindByName`.
  - **Por ID:** número exacto, reutilizando `FindProduct`.
  - Al elegir un resultado se pasa a la confirmación (ID fijo).
  - Si no hay resultados, mensaje claro y se mantiene en la búsqueda.
- **Con selección:** se abre directamente la confirmación con el **ID bloqueado (solo lectura)** y se muestran nombre y stock del producto a eliminar.
- **Confirmación explícita:** mensaje "¿Eliminar el producto *X* (ID *n*)?" con `Confirmar` / `Cancelar`. Solo `Confirmar` invoca a `DeleteProduct`.
- **Post-eliminación:** vuelve al panel con el listado refrescado.

## Criterios de aceptación

- [ ] **CA1** — Sin selección, "Dar de baja" permite buscar por **nombre** y por **ID**.
- [ ] **CA2** — La búsqueda por nombre filtra coincidencias parciales y permite **elegir** un producto para eliminar.
- [ ] **CA3** — Con producto elegido o seleccionado, el campo **ID es de solo lectura**.
- [ ] **CA4** — Siempre existe una **confirmación explícita** antes de borrar; cancelar no elimina nada.
- [ ] **CA5** — Tras eliminar, el listado del panel se refresca y el producto desaparece.
- [ ] **CA6** — Búsqueda sin resultados muestra un **mensaje claro**.
- [ ] **CA7** — Un ID inexistente **no borra nada** (mensaje de error).

## Fuera de alcance

- No cambia la implementación de `DeleteProduct` ni las capas `domain`/`application`/`infrastructure`.
- No introduce permisos por rol ni lógica nueva de negocio.

## Estado de implementación

- [ ] Pendiente de aprobación del `spec.md` → pasar a `in-progress`.