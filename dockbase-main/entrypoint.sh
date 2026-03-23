#!/usr/bin/env bash
set -uo pipefail

# Run the user-provided init script (remote URL),
# then hand off control to supervisord.
#
# The init script is responsible for:
#   - Installing packages
#   - Writing /etc/supervisor/conf.d/*.conf
#
# Example:
#   docker run -e INIT_SCRIPT=https://example.com/setup.sh ghcr.io/vevc/dockbase:latest

if [[ "${INIT_SCRIPT:-}" == http://* ]] || [[ "${INIT_SCRIPT:-}" == https://* ]]; then
    echo "[dockbase] Fetching and running init script: ${INIT_SCRIPT}"
    if ! curl -fsSL "${INIT_SCRIPT}" | bash; then
        echo "[dockbase] ERROR: INIT_SCRIPT failed: ${INIT_SCRIPT}"
    fi
fi

echo "[dockbase] Starting supervisord..."
exec /usr/bin/supervisord -c /etc/supervisor/supervisord.conf
