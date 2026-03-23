#!/usr/bin/env bash
# Example init script.
# This script runs inside the container at startup.
# It should:
#   1. Install required packages (Node.js 22 and Python3 are pre-installed)
#   2. Write /etc/supervisor/conf.d/*.conf for each program
# supervisord is started automatically after this script exits.

set -euo pipefail

# ── Install packages ──────────────────────────────────────────────────────────
apt-get update -qq
apt-get install -y --no-install-recommends nginx
rm -rf /var/lib/apt/lists/*

# ── Write supervisor program configs ─────────────────────────────────────────
cat > /etc/supervisor/conf.d/nginx.conf <<'EOF'
[program:nginx]
command=nginx -g "daemon off;"
autostart=true
autorestart=true
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0
EOF

cat > /etc/supervisor/conf.d/app.conf <<'EOF'
[program:app]
command=python3 -m http.server 8080
directory=/app
autostart=true
autorestart=true
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0
EOF

# supervisord is started by entrypoint.sh after this script exits.
