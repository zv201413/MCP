# dockbase

🌐 [简体中文](README.md) | English

A ready-to-use base image with Node.js 22 and Python3 pre-installed. Provide an init script via `INIT_SCRIPT`, and supervisord takes over after it runs.

## How it works

```
entrypoint.sh
  ├── Download and run $INIT_SCRIPT   (install packages, write supervisor configs)
  └── exec supervisord                (manages all programs defined in conf.d/)
```

The init script is entirely user-defined. Its only responsibility:

1. Install whatever packages are needed
2. Write one or more `*.conf` files to `/etc/supervisor/conf.d/`

## Quick Start

### docker run

Start a ttyd web terminal (default port 7681, username `user`, password `pass`):

```bash
docker run \
  -p 7681:7681 \
  -e INIT_SCRIPT=https://raw.githubusercontent.com/vevc/dockbase/refs/heads/main/examples/init-ttyd.sh \
  -e TTYD_PORT=7681 \
  -e TTYD_ARGS="-W -c user:pass" \
  ghcr.io/vevc/dockbase:latest
```

### docker-compose

Start nginx reverse proxy + Python app:

```yaml
services:
  app:
    image: ghcr.io/vevc/dockbase:latest
    environment:
      INIT_SCRIPT: https://raw.githubusercontent.com/vevc/dockbase/refs/heads/main/examples/init-web.sh
    ports:
      - "80:80"
      - "8080:8080"
```

## Example Init Script

```bash
#!/usr/bin/env bash
set -euo pipefail

# 1. Install packages
apt-get update -qq && apt-get install -y nginx

# 2. Write supervisor program configs
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

# supervisord starts automatically after this script exits
```

See the [examples/](examples/) directory for more examples.

## Notes

- `INIT_SCRIPT` only accepts remote URLs starting with `http://` or `https://`, any other value is ignored
- If the init script fails to download or exits with an error, the container will print an error log but **will not exit** — supervisord starts normally, check details via `docker logs` / `kubectl logs`
- `/etc/supervisor/conf.d` is set to world-writable at build time, so non-root users can write configs without issues
