# MANUAL DE FLUJO DE TRABAJO Y GESTIÓN DE RAMAS EN GIT

Este documento establece las normas obligatorias para la organización del código, uso de ramas y fusiones en el repositorio del proyecto. Todo miembro del equipo debe seguir estrictamente este protocolo para garantizar la estabilidad del software.

---

## 1. ESTRUCTURA Y PROPÓSITO DE LAS RAMAS

El repositorio se divide en tres niveles jerárquicos de aislamiento. Queda prohibido alterar el propósito de cualquiera de estas ramas.

###  Rama `main` (Distribución / Releases)
* **Propósito:** rama de **distribución**. Aloja exclusivamente las versiones 100% estables y probadas, únicamente para generar los instaladores oficiales (`.msi`, `.deb`, etc.) mediante las *Releases* de GitHub.
* **Normativa:** NO es una rama de trabajo. Todo el desarrollo ocurre en `dev` y sus ramas `feature/*`.
* **Restricción crítica:** **PROHIBIDO** realizar `git push` directo a `main`. El código solo ingresa aquí mediante fusión autorizada por el administrador del proyecto tras el cierre de un ciclo de desarrollo.

###  Rama `dev` (Producción activa / Integración)
* **Propósito:** rama de **producción activa**. Es la base de trabajo del equipo (donde se desarrolla junto al/os compañero/s). Concentra las funciones y pantallas recientemente finalizadas para comprobar su interacción mutua.
* **Normativa:** es la rama base del día a día: se parte de ella para crear `feature/*` y refleja el estado actual del desarrollo. El estado del producto (incluidos los fixes, como el de bootstrap de login) vive aquí.
* **Sincronización:** el equipo debe `git pull origin dev` antes de cada tarea. La integración **no** se hace con `git push origin dev`: todo cambio entra por **Pull Request revisado** (ver secciones 2 y 3).

###  Ramas `feature/` (Desarrollo Especializado)
* **Propósito:** Aislar la codificación de una única tarea o Issue específica (ej: `feature/busqueda-nombre`, `feature/alerta-stock`).
* **Normativa:** Evitan que un código en estado de borrador o que no compila afecte la línea principal de integración. Se destruyen inmediatamente después de cumplir su objetivo.

---

## 2. PROTOCOLO OPERATIVO DIARIO (FLUJO DE COMANDOS)

Cada vez que se asigne o inicie el desarrollo de una nueva funcionalidad, ejecute los siguientes comandos en su terminal en el orden exacto indicado:

### Paso 1: Actualizar el entorno local
Antes de segregar el código, sincronice su entorno local con la versión más reciente del equipo en `dev`:
```bash
git checkout dev
git pull origin dev
```
### Paso 2: Crear la rama de trabajo aislada

Cree y desplácese a una nueva rama `feature`. Utilice un nombre descriptivo en minúsculas separado por guiones:


```
git checkout -b feature/nombre-de-la-tarea
```
### Paso 3: Confirmar y respaldar avances

Realice confirmaciones ordenadas de su código. Suba la rama a GitHub para asegurar el respaldo de su trabajo (ningún otro integrante modificará esta rama):


```
git add .
git commit -m "Descripción técnica y concisa del cambio realizado"
git push origin feature/nombre-de-la-tarea
```

## 3. PROTOCOLO DE REVISIÓN Y FUSIÓN (PULL REQUEST)

Una vez que la funcionalidad esté completada, verifique que **compile** y que los **tests pasen** localmente. La integración se hace por **Pull Request revisado**: **nunca** se hace `git push` directo a `dev` ni a `main`; cada cambio vive en su rama hasta que el revisor lo apruebe.

### Paso 1: Abrir el Pull Request

Con la rama `feature/...` ya subida a GitHub (ver sección 2, Paso 3), abra un Pull Request cuya **rama base sea `dev`** y cuya rama de comparación sea su rama de trabajo:

```
gh pr create --base dev --head feature/nombre-de-la-tarea --title "feat: descripción corta del cambio" --body "Qué hace, cómo se verificó (tests) y a qué issue cierra"
```

### Paso 2: Revisión por parte del revisor

El **revisor** (administrador del proyecto) revisa el Pull Request. Si hay observaciones, se corrigen agregando **nuevos commits en la misma rama** y el PR se actualiza solo. **Ningún integrante mergea su propio Pull Request.**

### Paso 3: Fusión autorizada

Aprobado el PR, **el revisor lo mergea a `dev`** (desde la interfaz de GitHub o con la CLI). Así `dev` solo recibe código ya revisado:

```
gh pr merge --merge
```

_Nota: Si GitHub detecta conflictos, detenga la fusión y resuélvalos en la rama antes de continuar._

### Paso 4: Sincronizar y depurar

Actualice su copia local y elimine la rama de trabajo (local y remota) para mantener limpio el historial visual del repositorio:

```
git checkout dev
git pull origin dev
git branch -d feature/nombre-de-la-tarea
git push origin --delete feature/nombre-de-la-tarea
```

### Paso 5: Promoción a `main` (release)

El paso de `dev` a `main` lo decide **exclusivamente el revisor**, mediante un Pull Request de `dev` hacia `main`, cuando el ciclo de desarrollo esté cerrado y estable. `main` solo aloja versiones listas para generar los instaladores oficiales.