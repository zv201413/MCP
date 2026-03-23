# dockbase

🌐 简体中文 | [English](README.en.md)

一个开箱即用的 Docker 基座镜像，内置 Node.js 22 与 Python3 运行环境。通过 `INIT_SCRIPT` 环境变量指定一个远程初始化脚本，脚本执行完毕后由 supervisord 接管所有进程。

## 工作原理

```
entrypoint.sh
  ├── 下载并执行 $INIT_SCRIPT   （安装依赖、写入 supervisor 配置）
  └── exec supervisord          （接管 conf.d/ 中定义的所有程序）
```

初始化脚本完全由用户自定义，只需完成两件事：

1. 安装所需的软件包
2. 向 `/etc/supervisor/conf.d/` 写入一个或多个 `*.conf` 进程配置文件

脚本退出后，supervisord 自动启动并接管所有已配置的程序。

## 快速开始

### docker run

启动 ttyd 网页终端（默认端口 7681，用户名 `user`，密码 `pass`）：

```bash
docker run \
  -p 7681:7681 \
  -e INIT_SCRIPT=https://raw.githubusercontent.com/vevc/dockbase/refs/heads/main/examples/init-ttyd.sh \
  -e TTYD_PORT=7681 \
  -e TTYD_ARGS="-W -c user:pass" \
  ghcr.io/vevc/dockbase:latest
```

### docker-compose

启动 nginx 反向代理 + Python 应用：

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

## 初始化脚本示例

```bash
#!/usr/bin/env bash
set -euo pipefail

# 1. 安装所需软件包
apt-get update -qq && apt-get install -y nginx

# 2. 写入 supervisor 进程配置
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

# 脚本退出后 supervisord 自动启动
```

更多示例参见 [examples/](examples/) 目录。

## 注意事项

- `INIT_SCRIPT` 只接受 `http://` 或 `https://` 开头的远程 URL，传入其他值会被忽略
- 初始化脚本下载失败或执行报错时，容器会打印错误日志但**不会退出**，supervisord 仍会正常启动，可通过 `docker logs` / `kubectl logs` 查看错误详情
- `/etc/supervisor/conf.d` 已在构建时设置为任意用户可读写，非 root 用户的初始化脚本同样可以正常写入配置
