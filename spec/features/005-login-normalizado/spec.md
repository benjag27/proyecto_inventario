# Feature-005 — Login tolerante a espacios (normalización del usuario)

> Carpeta: `spec/features/005-login-normalizado/` · Issue: [#42](https://github.com/benjag27/proyecto_inventario/issues/42) · Estado: `done`

## Objetivo

Que el inicio de sesión no falle por **espacios accidentales** al inicio o al final del nombre de usuario.

## Contexto

`LoginService.authenticate` consultaba `userRepository.findByUsername(username)` con el texto **tal cual** venía del campo. Como la búsqueda es por igualdad (`WHERE username = ?`), un espacio al final (`"admin "`) **nunca** coincide con `"admin"`, y el login se rechaza con el mensaje genérico *"Usuario o contraseña incorrectos"*, sin pista de la causa.

Efecto colateral de seguridad: `mustChangePassword` también recibía el usuario sin normalizar, por lo que un usuario con espacio podía **omitir** la pantalla de cambio obligatorio de contraseña.

## Comportamiento esperado

- El nombre de usuario se **normaliza** (`trim`) antes de consultar el repositorio, en todos los métodos que lo reciben (`authenticate`, `mustChangePassword`, `changePassword`).
- Un usuario (o contraseña) `null` no provoca excepción: se trata como no autenticado (`false`).
- La **contraseña NO se normaliza**: puede contener espacios intencionales.

## Criterios de aceptación

- [x] **CA1** — `authenticate("admin ", "admin123")` y `authenticate("  admin", "admin123")` autentican igual que `authenticate("admin", "admin123")`.
- [x] **CA2** — `authenticate` sigue rechazando una contraseña incorrecta (sin cambios de comportamiento).
- [x] **CA3** — `authenticate(null, ...)` y `authenticate(..., null)` devuelven `false` sin lanzar excepción.
- [x] **CA4** — `mustChangePassword("admin ")` y `changePassword("admin ", ...)` normalizan el usuario.
- [x] **CA5** — Se mantiene el hashing PBKDF2WithHmacSHA256 y la comparación en tiempo constante (sin cambios).

## Fuera de alcance

- No se cambia el algoritmo de hashing ni el formato `ITERACIONES:SAL:HASH`.
- No se modifica el esquema de base de datos.
- La contraseña no se recorta (a propósito).
- La búsqueda por nombre de producto (mayúsculas/acentos) queda fuera: aquí solo se tratan espacios al inicio/fin del usuario.

## Estado de implementación

- [x] `spec.md` aprobado (fix de robustez ya implementado en `dev`, commit `3c29ae2`).
- [x] Implementado + test `LoginServiceTest` (6 casos) en verde.
- [x] Registrado en `CHANGELOG.md`.
