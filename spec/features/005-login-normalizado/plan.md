# Plan — Feature-005: Login tolerante a espacios

> Issue: #42 · Depende de: `spec/features/005-login-normalizado/spec.md`

## Enfoque

Cambio **acotado a la capa `domain`**: el problema vive en `LoginService`. No se toca `application`, `infrastructure` ni `presentation`, ni el esquema de datos. La contraseña se deja intacta a propósito.

## Capas y archivos afectados

| Archivo | Cambio |
|---|---|
| `domain/service/LoginService.java` | Nuevo helper `normalizeUsername` (`trim` + guarda de `null`); se aplica en `authenticate`, `mustChangePassword` y `changePassword`. |
| `test/java/org/phora/domain/service/LoginServiceTest.java` | Nuevo test con repositorio **en memoria** (no depende de la base real). |
| `CHANGELOG.md` | Entrada `[FIX]`. |

## Estrategia de implementación

1. `private static String normalizeUsername(String username)` → `username == null ? null : username.trim()`.
2. `authenticate`: normalizar; si usuario o contraseña son `null` → `false`.
3. `mustChangePassword` y `changePassword`: normalizar igual (evita saltarse el cambio obligatorio de contraseña).
4. Test con un `UserRepository` en memoria que expone `admin`, verificando: trim al inicio/fin, tabulaciones/espacios combinados, contraseña incorrecta, entradas `null` y normalización en `mustChangePassword`/`changePassword`.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Recortar la contraseña y romper claves con espacios intencionales | No se normaliza la contraseña. |
| Cambiar la semántica de búsqueda del usuario (mayúsculas, acentos) | Fuera de alcance: solo se descartan espacios al inicio/fin. |
| Regresión en el flujo de cambio obligatorio | Cubierto por los casos de `mustChangePassword`/`changePassword` en el test. |

## Verificación

- `mvn -Dtest=LoginServiceTest test` → 6/6 en verde.
- `mvn clean verify` (compila + suite completa).
- Prueba manual de login con `"admin "` + `admin123`.

## Cierre

- Registrar `[FIX]` en `CHANGELOG.md`.
- Marcar `done` en `roadmap.md` y cerrar el issue #42.
