# Tasks — Feature-002: Códigos de barras

> Issue: [#33](https://github.com/benjag27/proyecto_inventario/issues/33) · Checklist de implementación en orden.

## Análisis
 - [x] T1 — Revisar `BsConfig.initDB()` (sentencias `CREATE TABLE IF NOT EXISTS` actuales) y `ProductRepositoryImpl` (conexión sin transacciones hoy).
- [x] T2 — Revisar `Product` (builder/setters) y las firmas de `AddProduct.execute`/`UpdateProduct.execute` y sus llamadores en `ProductFormView`.

## Implementación — BD y Domain
- [x] T3 — `BsConfig`: crear tabla `product_barcodes` (`product_id` FK con `ON DELETE CASCADE`, `barcode TEXT NOT NULL UNIQUE`) y ejecutarla en `initDB()`.
- [x] T4 — `Product`: campo `List<String> barcodes` (inmutable), builder `.barcodes(...)` con default vacío, getter y setter.

## Implementación — Persistencia (ProductRepositoryImpl)
- [x] T5 — `add(Product)`: transacción; insert producto y recuperar id con `last_insert_rowid()` (el driver no implementa `getGeneratedKeys`); insert códigos; `commit`.
- [x] T6 — `update(Product)`: transacción; actualizar campos + **sincronizar códigos** (borrar removidos, insertar nuevos).
- [x] T7 — `delete(Product)`: verificar que `ON DELETE CASCADE` limpia los códigos (validado en `ProductBarcodeTest`).
- [x] T8 — `findById`/`findAll`/`findByName`: cargar códigos por producto e inyectarlos.
- [x] T9 — Traducir `SQLException` por `UNIQUE` a mensaje amigable ("El código ya está registrado en otro producto").

## Implementación — Application
- [x] T10 — `AddProduct.execute`: nueva firma con `List<String> barcodes`; validar duplicados en la misma lista; audit con códigos.
- [x] T11 — `UpdateProduct.execute`: nueva firma con `List<String> barcodes`; `product.setBarcodes(...)`; audit con códigos.
- [x] T12 — Actualizar los llamadores de ambas firmas en `ProductFormView`.

## Implementación — Presentation
- [x] T13 — `ProductFormView.formAlta()`: bloque de códigos opcional (campo + "Agregar código" + filas con botón "✖").
- [x] T14 — `ProductFormView.formModificar()`: precargar códigos del `prefill`; agregar nuevos; **eliminar uno individual** manteniendo el resto.
- [x] T15 — `ProductFormView` (buscar por ID / nombre): mostrar los códigos en el detalle.
- [x] T16 — `ProductPanelView.buildTable()`: columna "Códigos".

## Validación
- [x] T17 — Verificar CA1..CA8 del `spec.md`.
- [x] T18 — `mvn test` y `mvn clean install` en verde.
- [ ] T19 — Prueba manual: alta sin códigos, alta con varios, agregar/eliminar en modificar, duplicado rechazado, baja en cascada.

## Cierre
- [x] T20 — Registrar cambios en `CHANGELOG.md` (`[ADD]/[CHANGE]`) y actualizar estado en `roadmap.md`.
- [ ] T21 — Marcar `done` en `roadmap.md` y cerrar el issue #33.