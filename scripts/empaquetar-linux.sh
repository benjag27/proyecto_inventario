#!/usr/bin/env bash
#
# empaquetar-linux.sh — genera los instaladores de Linux del Inventario.
#
# Encadena el protocolo de despliegue del proyecto:
#   1) mvn clean package   (compila y corre los tests)
#   2) mvn javafx:jlink    (runtime mínimo autocontenido)
#   3) jpackage            (app-image portable y/o instalador .deb)
#
# Uso:
#   scripts/empaquetar-linux.sh [opciones]
#
# Ejemplos:
#   scripts/empaquetar-linux.sh
#   scripts/empaquetar-linux.sh --version 1.0.0 --type deb
#   scripts/empaquetar-linux.sh -t app-image --skip-tests
#
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
cd -- "$ROOT_DIR"

# --- Valores por defecto (alineados con el CHANGELOG del proyecto) ----------
APP_NAME="inventario-app"
APP_VERSION="1.0.0"
DEST="target/installer"
TYPE="both"
MAINTAINER=""
SKIP_TESTS=0
OFFLINE=0

MODULE="org.phora/org.phora.Main"
JLINK_IMAGE="target/inventario-app"
VENDOR="Phora"
DESCRIPTION="Inventario de escritorio"

# --- Utilidades -------------------------------------------------------------
die()  { printf 'ERROR: %s\n' "$*" >&2; exit 1; }
info() { printf '\n==> %s\n' "$*"; }
warn() { printf 'AVISO: %s\n' "$*" >&2; }

usage() {
  cat <<'EOF'
Genera los instaladores de Linux del Inventario.

Uso:
  scripts/empaquetar-linux.sh [opciones]

Opciones:
  -v, --version X.Y.Z    Versión numérica de la app (default: 1.0.0)
  -t, --type TIPO        app-image | deb | both (default: both)
  -n, --name NOMBRE      Nombre del paquete y del launcher (default: inventario-app)
  -d, --dest DIR         Carpeta de salida (default: target/installer)
  -m, --maintainer TXT   Maintainer del .deb, ej. "Nombre <mail@dominio>"
      --skip-tests       No ejecuta los tests de Maven
      --offline          Ejecuta Maven en modo offline (-o)
  -h, --help             Muestra esta ayuda

Requisitos:
  - JDK 17 o superior con jpackage (un JRE no alcanza)
  - Maven
  - Para .deb: dpkg-deb y fakeroot  (sudo apt install dpkg-dev fakeroot)

Salidas:
  app-image -> DIR/<nombre>/            (carpeta portable)
  deb       -> DIR/<nombre>_<version>-1_<arch>.deb

Notas:
  - El .msi de Windows no se puede generar desde Linux.
  - La base db/inventario_base.db debe quedar dentro del runtime;
    el script lo verifica y avisa si no la encuentra.
EOF
}

need() {
  command -v "$1" >/dev/null 2>&1 || die "falta '$1'. $2"
}

# --- Argumentos -------------------------------------------------------------
while [[ $# -gt 0 ]]; do
  case "$1" in
    -v|--version)    APP_VERSION="${2:?falta la versión}"; shift 2 ;;
    -t|--type)       TYPE="${2:?falta el tipo}"; shift 2 ;;
    -n|--name)       APP_NAME="${2:?falta el nombre}"; shift 2 ;;
    -d|--dest)       DEST="${2:?falta el directorio}"; shift 2 ;;
    -m|--maintainer) MAINTAINER="${2:?falta el maintainer}"; shift 2 ;;
    --skip-tests)    SKIP_TESTS=1; shift ;;
    --offline)       OFFLINE=1; shift ;;
    -h|--help)       usage; exit 0 ;;
    *)               die "opción desconocida: '$1' (usá --help)" ;;
  esac
done

# --- Validaciones -----------------------------------------------------------
[[ "$APP_VERSION" =~ ^[0-9]+(\.[0-9]+)*$ ]] \
  || die "versión inválida '$APP_VERSION' (usá algo como 1.0.0, sin -SNAPSHOT)"

case "$TYPE" in
  app-image|deb|both) ;;
  *) die "tipo inválido '$TYPE' (elegí app-image, deb o both)" ;;
esac

[[ "$APP_NAME" =~ ^[a-z0-9][a-z0-9-]*$ ]] \
  || die "nombre inválido '$APP_NAME' (minúsculas, números y guiones)"

need java     "instalá un JDK 17 o superior"
need mvn      "instalá Maven"
need jpackage "jpackage viene con el JDK, no con el JRE"

JAVA_MAJOR="$(java -version 2>&1 | sed -n 's/.*version "\([0-9]*\).*/\1/p' | head -1)"
[[ -n "$JAVA_MAJOR" && "$JAVA_MAJOR" -ge 17 ]] \
  || die "se requiere Java 17 o superior (detectado: ${JAVA_MAJOR:-desconocido})"

if [[ "$TYPE" == "deb" || "$TYPE" == "both" ]]; then
  need dpkg-deb "instalá dpkg-dev: sudo apt install dpkg-dev"
  need fakeroot "instalá fakeroot: sudo apt install fakeroot"
fi

# --- Compilación y runtime --------------------------------------------------
MVN=(mvn)
if [[ "$OFFLINE" -eq 1 ]]; then
  MVN+=("-o")
fi
if [[ "$SKIP_TESTS" -eq 1 ]]; then
  MVN+=("-DskipTests")
fi

info "Compilando y ejecutando tests (${MVN[*]} clean package)"
"${MVN[@]}" clean package

info "Generando runtime con jlink (${MVN[*]} javafx:jlink)"
"${MVN[@]}" javafx:jlink

[[ -x "$JLINK_IMAGE/bin/inventario" ]] \
  || die "no se generó el runtime en $JLINK_IMAGE/bin/inventario"

if grep -aq "db/inventario_base.db" "$JLINK_IMAGE/lib/modules"; then
  echo "    OK: db/inventario_base.db va incluida en el runtime"
else
  warn "no encontré db/inventario_base.db dentro de $JLINK_IMAGE/lib/modules"
fi

# --- jpackage ---------------------------------------------------------------
mkdir -p "$DEST"
COMMON=(
  --runtime-image "$JLINK_IMAGE"
  --module "$MODULE"
  --name "$APP_NAME"
  --vendor "$VENDOR"
  --description "$DESCRIPTION"
  --app-version "$APP_VERSION"
  --dest "$DEST"
)

if [[ "$TYPE" == "app-image" || "$TYPE" == "both" ]]; then
  info "Generando app-image portable en $DEST/$APP_NAME"
  rm -rf -- "$DEST/$APP_NAME"
  jpackage --type app-image "${COMMON[@]}"
fi

if [[ "$TYPE" == "deb" || "$TYPE" == "both" ]]; then
  info "Generando instalador .deb en $DEST"
  rm -f -- "$DEST/${APP_NAME}_"*.deb
  DEB_ARGS=(--type deb "${COMMON[@]}" --linux-shortcut --linux-menu-group "Office")
  if [[ -n "$MAINTAINER" ]]; then
    DEB_ARGS+=(--linux-deb-maintainer "$MAINTAINER")
  fi
  jpackage "${DEB_ARGS[@]}"
fi

# --- Resumen ----------------------------------------------------------------
info "Listo. Artefactos en $DEST"
ls -lh "$DEST"

if compgen -G "$DEST/*.deb" >/dev/null; then
  DEB="$(ls "$DEST"/*.deb | head -1)"
  printf '\nInstalar:     sudo apt install ./%s\n' "$DEB"
  printf 'Desinstalar:  sudo apt remove %s\n' "$APP_NAME"
  printf 'Metadata:     dpkg-deb -f "%s" Package Version Architecture\n' "$DEB"
  printf 'Probar sin instalar: %s/%s/bin/%s\n' "$DEST" "$APP_NAME" "$APP_NAME"
fi
