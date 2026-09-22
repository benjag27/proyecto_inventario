# MANUAL DE FLUJO DE TRABAJO Y GESTIÓN DE RAMAS EN GIT

Este documento establece las normas obligatorias para la organización del código, uso de ramas y fusiones en el repositorio del proyecto. Todo miembro del equipo debe seguir estrictamente este protocolo para garantizar la estabilidad del software.

---

## 1. ESTRUCTURA Y PROPÓSITO DE LAS RAMAS

El repositorio se divide en tres niveles jerárquicos de aislamiento. Queda prohibido alterar el propósito de cualquiera de estas ramas.

###  Rama `main` (Producción Estable)
* **Propósito:** Alojar exclusivamente las versiones de software 100% estables, probadas y listas para el usuario final.
* **Normativa:** De esta rama se generan los instaladores oficiales (`.msi`, `.deb`, etc.) mediante las *Releases* de GitHub.
* **Restricción crítica:** **PROHIBIDO** realizar `git push` directo a `main`. El código solo ingresa aquí mediante fusión autorizada por el administrador del proyecto tras el cierre de un ciclo de desarrollo.

###  Rama `dev` (Integración y Ensamblaje)
* **Propósito:** Concentrar las funciones y pantallas recientemente finalizadas para comprobar su interacción mutua.
* **Normativa:** Es la rama base del día a día. Refleja el estado intermedio actual del desarrollo del proyecto.

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

## 3. PROTOCOLO DE FUSIÓN Y CIERRE DE TAREA

Una vez que la funcionalidad esté completada, verifique que compile localmente de forma exitosa y proceda a integrar el código a la línea principal siguiendo estos pasos:
###  Paso 1: Regresar a la rama de integración

Cambie el espacio de trabajo local a la rama de desarrollo principal:



```
git checkout dev
```

### Paso 2: Absorber actualizaciones remotas de terceros

Evite conflictos de código trayendo lo que otros desarrolladores hayan subido al servidor remoto mientras usted trabajaba en su celda aislada:



```
git pull origin dev
```

### Paso 3: Ejecutar la fusión

Incorpore los cambios de su tarea terminada dentro de la rama `dev`:


```
git merge feature/nombre-de-la-tarea
```

_Nota: Si Git detecta conflictos de código, detenga el proceso y resuélvalos manualmente en el editor junto al administrador antes de continuar._

### Paso 4: Publicar la integración en el servidor

Envíe el resultado final unificado a GitHub para que esté disponible para el resto del equipo:


```
git push origin dev
```

### Paso 5: Depurar el árbol de ramas

Elimine la rama `feature` local y remotamente para mantener limpio el historial visual del repositorio:


```
git branch -d feature/nombre-de-la-tarea
git push origin --delete feature/nombre-de-la-tarea
```