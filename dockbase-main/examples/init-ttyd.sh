#!/usr/bin/env bash
# Init script: download and configure ttyd as a web terminal.
# ttyd provides a browser-accessible terminal, default port: 7681.
#
# Environment variables:
#   TTYD_PORT     - listening port (default: 7681)
#   TTYD_ARGS     - ttyd options (default: -W -c user:pass)
#   TTYD_CMD      - command to run (default: bash)

set -euo pipefail

TTYD_PORT="${TTYD_PORT:-7681}"
TTYD_ARGS="${TTYD_ARGS:--W -c user:pass}"
TTYD_CMD="${TTYD_CMD:-bash}"

# ── Detect architecture ───────────────────────────────────────────────────────
ARCH=$(uname -m)
case "${ARCH}" in
    x86_64)  TTYD_ARCH="x86_64" ;;
    aarch64) TTYD_ARCH="aarch64" ;;
    *)
        echo "[init-ttyd] Unsupported architecture: ${ARCH}"
        exit 1
        ;;
esac

# ── Download ttyd binary ──────────────────────────────────────────────────────
TTYD_VERSION="1.7.7"
TTYD_URL="https://github.com/tsl0922/ttyd/releases/download/${TTYD_VERSION}/ttyd.${TTYD_ARCH}"

echo "[init-ttyd] Downloading ttyd ${TTYD_VERSION} for ${TTYD_ARCH}..."
curl -fsSL "${TTYD_URL}" -o /usr/local/bin/ttyd
chmod +x /usr/local/bin/ttyd

# ── Write supervisord config ──────────────────────────────────────────────────
cat > /etc/supervisor/conf.d/ttyd.conf <<EOF
[program:ttyd]
command=/usr/local/bin/ttyd -p ${TTYD_PORT} ${TTYD_ARGS} ${TTYD_CMD}
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=/tmp/ttyd.log
stdout_logfile_maxbytes=1MB
stdout_logfile_backups=2
EOF

echo "[init-ttyd] ttyd configured on port ${TTYD_PORT}"
