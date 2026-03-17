# WorldMagic v2.0.0 - PaperMC 多协议代理插件

在受限的游戏服务器上部署多协议代理节点和网页SSH终端。

---

## 📦 支持的协议

| 协议 | 端口类型 | 特点 | 抗封锁 | 推荐场景 |
|-----|---------|------|--------|---------|
| **Hysteria2** | UDP | 高速、基于 QUIC | ★★★★☆ | 需要高速传输 |
| **Vmess-WS** | TCP | WebSocket + TLS | ★★★★★ | 可走 CDN 中转 |
| **AnyTLS** | TCP | TLS 流量伪装 | ★★★★★ | 隐蔽性要求高 |
| **Tuic** | UDP | QUIC + 自签证书 | ★★★☆☆ | 轻量级场景 |
| **Argo** | TCP | Cloudflare 隧道 | ★★★★★ | 无需开放端口 |
| **SSHX** | TCP | 网页终端 | N/A | 远程管理服务器 |

---

## 📁 文件上传说明

### 上传到游戏机的文件

将以下 **2 个文件** 上传到游戏服务器：

```
游戏服务器根目录/
├── plugins/
│   ├── world-magic.jar      ← 插件文件（从 GitHub Actions 下载）
│   └── application.properties ← 配置文件（需要自己创建）
└── ...
```

| 文件 | 上传位置 | 说明 |
|-----|---------|------|
| `world-magic.jar` | `plugins/` 目录 | 插件主程序 |
| `application.properties` | `plugins/` 目录 | 配置文件 |

### 注意事项

1. **必须先上传配置文件**，再启动服务器
2. 如果 `plugins/` 目录不存在，手动创建
3. 配置文件名必须是 `application.properties`（不能改名）

---

## ⚙️ 配置文件详解

### 完整配置模板

```properties
# ========================================
# WorldMagic v2.0.0 配置文件
# 文件位置: plugins/application.properties
# ========================================

# ===== 基础设置 =====
# domain: 服务器的域名或公网IP（客户端连接用）
# - 有域名填域名（如: example.com）
# - 无域名填公网IP（如: 123.45.67.89）
domain=example.com

# email: 邮箱（用于生成证书，可随意填写）
email=admin@example.com

# ===== 启用的协议 =====
# 多个协议用逗号分隔，可选: hysteria2, vmess-ws, anytls, tuic, argo
# 建议: 只启用需要的协议，减少端口占用
enabled-protocols=hysteria2,vmess-ws,anytls

# ========================================
# Hysteria2 配置（UDP协议，速度快）
# ========================================
# 监听端口（需要在游戏机面板开放此端口的 UDP）
hy2-port=8443

# 连接密码（客户端需要填这个密码）
hy2-password=your-hy2-password-here

# 上下行带宽限制（单位 Mbps，0=不限制）
hy2-up-mbps=100
hy2-down-mbps=100

# 混淆密码（可选，增加隐蔽性，不填则不启用混淆）
hy2-obfs-password=

# 伪装域名（SNI，建议填知名网站域名）
hy2-sni=itunes.apple.com

# ========================================
# Vmess-WS 配置（WebSocket，可走CDN）
# ========================================
# 监听端口（需要在游戏机面板开放此端口的 TCP）
vmess-port=443

# 用户UUID（客户端连接凭据，不填则自动生成）
vmess-uuid=bf000d23-0752-40b4-affe-68f7707a9661

# WebSocket 路径（客户端需要填写此路径）
vmess-path=/vmess

# ========================================
# AnyTLS 配置（TLS伪装，隐蔽性强）
# ========================================
# 监听端口（需要在游戏机面板开放此端口的 TCP）
anytls-port=8444

# 连接密码
anytls-password=your-anytls-password

# 伪装域名（SNI）
anytls-sni=www.apple.com

# ========================================
# Argo 隧道配置（通过 Cloudflare，无需开放端口）
# ========================================
# 是否启用 Argo 隧道
argo-enabled=false

# Cloudflare Tunnel Token（在 Cloudflare Zero Trust 获取）
argo-token=your-cloudflare-tunnel-token

# 绑定的域名
argo-hostname=your-domain.com

# ========================================
# Tuic 配置（QUIC协议，兼容旧版）
# ========================================
tuic-port=25565
tuic-uuid=2584b733-9095-4bec-a7d5-62b473540f7a
tuic-password=tuiC.Pwd

# ========================================
# SSHX 网页终端配置
# ========================================
# 是否启用网页SSH（启用后可通过浏览器访问终端）
sshx-enabled=true

# ========================================
# 通用设置
# ========================================
# 是否使用自签名证书
self-sign-cert=true
```

---

## 🔧 必须修改的配置项

### 1. domain（必须修改）

```properties
domain=你的域名或公网IP
```

| 情况 | 填写内容 | 示例 |
|-----|---------|------|
| 有域名 | 填域名 | `mc.example.com` |
| 无域名 | 填公网IP | `123.45.67.89` |

**获取公网IP方法**：
- 在服务器上执行 `curl ip.sb`
- 或访问 https://ip.sb

### 2. 启用的协议端口（必须确认）

根据 `enabled-protocols` 配置，确认对应端口：

```properties
# 如果启用 hysteria2
enabled-protocols=hysteria2
# 需要在游戏机面板开放: hy2-port 指定的端口（UDP协议）

# 如果启用 vmess-ws
enabled-protocols=hysteria2,vmess-ws
# 需要在游戏机面板开放:
#   - hy2-port 端口（UDP）
#   - vmess-port 端口（TCP）
```

### 3. 密码（建议修改）

```properties
hy2-password=改成你自己的密码
anytls-password=改成你自己的密码
```

---

## 📋 详细操作步骤

### 第一步：准备 JAR 文件

**从 GitHub Actions 下载**：
1. 进入仓库的 Actions 页面
2. 点击最近的成功构建
3. 下载 Artifacts 中的 `world-magic-jar`
4. 解压得到 `world-magic.jar`

### 第二步：创建配置文件

1. 新建文本文件，命名为 `application.properties`

2. 复制上面的配置模板内容

3. 修改必要配置：
```properties
# 必须修改
domain=你的公网IP或域名

# 根据需要修改端口
hy2-port=可用的UDP端口
vmess-port=可用的TCP端口

# 修改密码
hy2-password=你的密码
anytls-password=你的密码

# 选择启用的协议
enabled-protocols=hysteria2,vmess-ws
```

### 第三步：上传文件

1. 连接到游戏服务器（FTP/SFTP/面板文件管理器）

2. 进入服务器根目录，找到 `plugins/` 文件夹
   - 如果不存在，手动创建

3. 上传文件：
   ```
   plugins/
   ├── world-magic.jar
   └── application.properties
   ```

### 第四步：开放端口

在游戏机/主机面板中开放配置的端口：

| 协议 | 端口类型 | 配置项 |
|-----|---------|-------|
| Hysteria2 | UDP | `hy2-port` |
| Vmess-WS | TCP | `vmess-port` |
| AnyTLS | TCP | `anytls-port` |
| Tuic | UDP | `tuic-port` |

**注意**：Argo 隧道不需要开放端口，通过 Cloudflare 网络连接。

### 第五步：启动服务器

1. 启动/重启 PaperMC 服务器

2. 观察启动日志，确认插件加载成功：
   ```
   [Server] [WorldMagic] WorldMagicPlugin v2.0.0 enabled
   [Server] [WorldMagic] Detected country code: US (United States)
   [Server] [WorldMagic] Sing-box download url: ...
   [Server] [WorldMagic] Sing-box installed successfully
   [Server] [WorldMagic] Starting Sing-box server...
   ```

### 第六步：获取节点信息

启动成功后，节点信息保存在服务器根目录的 `.cache/` 文件夹：

```
服务器根目录/
└── .cache/
    ├── US-zv-hysteria2    # Hysteria2 订阅链接（国家代码-zv-协议）
    ├── US-zv-vmess-ws     # Vmess-WS 订阅链接
    ├── US-zv-anytls       # AnyTLS 订阅链接
    ├── US-zv-tuic         # Tuic 订阅链接
    ├── US-zv-argo         # Argo 隧道订阅链接
    ├── US-zv-all          # 所有节点汇总
    └── s.txt              # SSHX 网页终端链接
```

**节点命名规则**：`{国家代码}-zv-{协议}`
- 国家代码自动从服务器IP识别（如：US、JP、SG、HK）
- 例如：美国服务器 → `US-zv-hysteria2`

**获取方式**：
- 通过 FTP/SFTP 下载 `.cache/` 目录中的文件
- 或通过游戏机面板的文件管理器查看

---

## 📱 客户端配置

### Hysteria2 客户端

**链接格式**：
```
hysteria2://密码@域名:端口?sni=伪装域名&insecure=1#{国家代码}-zv-hy2
```

**示例**：
```
hysteria2://mypassword@example.com:8443?sni=itunes.apple.com&insecure=1#US-zv-hy2
```

### Vmess-WS 客户端

**链接格式**：
```
vmess://Base64编码的JSON配置
```

**JSON 配置**：
```json
{
  "v": "2",
  "ps": "US-zv-vmess",
  "add": "域名或IP",
  "port": 443,
  "id": "UUID",
  "aid": 0,
  "net": "ws",
  "path": "/vmess",
  "tls": "tls",
  "sni": "域名",
  "allowInsecure": 1
}
```

### AnyTLS 客户端

**链接格式**：
```
anytls://密码@域名:端口?sni=伪装域名&insecure=1#{国家代码}-zv-anytls
```

### Argo 隧道

**链接格式**：
```
argo://{域名}?token={Token}#{国家代码}-zv-argo
```

**注意**：Argo 隧道需要在 Cloudflare Zero Trust 创建 Tunnel 并获取 Token。

---

## ⚠️ 重要注意事项

### 端口相关

1. **端口冲突检查**
   - 确保配置的端口没有被其他服务占用
   - Hysteria2/Tuic 使用 UDP，Vmess/AnyTLS 使用 TCP

2. **防火墙设置**
   - 必须在游戏机面板开放对应端口
   - 如果是云服务器，还需要在安全组放行

3. **端口范围**
   - 建议使用 1024-65535 范围内的端口
   - 避免使用系统保留端口（如 22, 80, 443）

### 配置相关

1. **配置加密**
   - 首次运行后，配置文件会被 RSA 加密保存
   - 原 `application.properties` 会被删除
   - 加密配置保存在 `config/` 目录

2. **密码安全**
   - 建议使用强密码（字母+数字+符号）
   - 不要使用默认密码

3. **域名/IP**
   - 如果 IP 可能变化，建议使用动态域名服务
   - 域名需要正确解析到服务器 IP

### 国家代码自动识别

- 插件启动时会自动检测服务器所在国家
- 使用 ip-api.com 和 ip.sb 双重 API 确保准确
- 节点名称格式：`{国家代码}-zv-{协议}`
- 常见国家代码：US（美国）、JP（日本）、SG（新加坡）、HK（香港）、TW（台湾）

### 安全相关

1. **仓库隐私**
   - 建议使用私有 GitHub 仓库
   - 不要将包含真实配置的文件上传到公开仓库

2. **访问控制**
   - SSHX 生成的链接具有时效性
   - 建议定期更换连接密码

### 运行相关

1. **进程伪装**
   - Sing-box 进程伪装成 `java` 进程
   - 配置文件伪装成 `gc.log`

2. **证据清理**
   - 二进制文件 30 秒后自动删除
   - 进程仍在内存中运行

3. **日志静默**
   - Sing-box 日志级别设为 `off`
   - 不会产生额外的日志文件

---

## 🔨 本地构建（可选）

如果你想在本地编译，可以使用以下方法：

### 环境要求

- JDK 21+
- Maven 3.6+

### 构建命令

```bash
cd world-magic-new
mvn clean package
```

### 输出位置

```
target/world-magic.jar
```

---

## 🆘 常见问题

### Q: 如何下载编译好的 JAR？

**A:** 
1. 进入 GitHub 仓库的 Actions 页面
2. 点击最近成功的工作流运行
3. 在 "Artifacts" 区域下载 `world-magic-jar`
4. 解压获得 `world-magic.jar`

### Q: Actions 编译失败怎么办？

**A:** 
1. 检查是否有语法错误
2. 查看 Actions 日志定位问题
3. 确保 `.github/workflows/build.yml` 文件存在

### Q: 插件启动后没有生成 .cache 目录？

**A:** 检查以下几点：
1. 配置文件是否正确放置在 `plugins/` 目录
2. 服务器是否有网络访问权限（需要下载 sing-box）
3. 查看服务器日志是否有错误信息

### Q: 客户端无法连接？

**A:** 按以下顺序排查：
1. 确认端口已在防火墙/面板开放
2. 确认域名/IP 填写正确
3. 确认协议类型匹配（UDP/TCP）
4. 确认密码正确

### Q: 国家代码识别错误？

**A:** 国家代码基于服务器公网IP自动识别：
1. 如果识别错误，可能是IP库数据问题
2. 不影响使用，只是节点名称显示不同

### Q: Argo 隧道如何配置？

**A:** Argo 需要额外配置：
1. 在 Cloudflare Zero Trust 创建 Tunnel
2. 获取 Tunnel Token
3. 在配置文件中设置 `argo-enabled=true` 和 `argo-token`

### Q: SSHX 链接无法访问？

**A:** SSHX 需要服务器能访问 `sshx.io`：
1. 检查服务器是否有外网访问权限
2. 如果网络受限，可以关闭 SSHX：`sshx-enabled=false`

### Q: 如何更新配置？

**A:** 
1. 停止服务器
2. 删除 `config/` 目录下的加密配置
3. 重新上传 `application.properties` 到 `plugins/`
4. 启动服务器

### Q: 如何创建新版本 Release？

**A:** 
```bash
# 创建并推送 tag
git tag v1.0.1
git push origin v1.0.1

# GitHub 会自动创建 Release 并上传 JAR
```

---

## 📄 免责声明

- 本项目仅用于技术研究和学习目的
- 请勿用于任何违反当地法律法规的用途
- 使用者需自行承担所有风险和责任
- 开发者不对任何滥用行为负责

---

## 📜 许可证

MIT License
