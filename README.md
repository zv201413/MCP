# WorldMagic v2.0.1 - PaperMC 多协议代理插件

WorldMagic 是一款专为受限游戏服务器环境设计的 PaperMC 插件，能够隐蔽地部署多协议代理节点（sing-box）和网页 SSH 终端（SSHX）。

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

## 📁 快速部署说明

### 0. 从本项目下载如下 **2 个文件** 
<img width="1749" height="609" alt="image" src="https://github.com/user-attachments/assets/ff40d71f-aaf5-4505-aae6-0b3edbd78662" />

### 1. 上传文件到游戏服务器

将下载的 **2 个文件** 上传到游戏服务器的 `plugins/` 目录：

```
游戏服务器根目录/
└── plugins/
    ├── world-magic.jar      ← 插件主程序
    └── application.properties ← 配置文件（需要手动创建）
```

> **注意**：如果 `plugins/` 目录不存在，请手动创建。配置文件名必须严格为 `application.properties`。

### 2. 配置 `application.properties`

根据你的服务器环境修改配置文件。建议直接使用项目提供的模板进行修改：

```properties
# ===== 基础设置 =====
# domain: 必填。填写你的公网 IP 或解析到该 IP 的域名。用于生成客户端连接链接。
domain=你的服务器IP
# email: 选填。用于生成自签名证书的邮箱标识。
email=admin@example.com

# ===== 启用的协议 =====
# enabled-protocols: 填写你想要运行的协议。
# 多个协议用逗号分隔，可选: hysteria2, vmess-ws, anytls, tuic, argo
# 提示：由于游戏机通常只开放一个端口(25565)，建议只开启 1-2 个协议。
enabled-protocols=hysteria2,vmess-ws,anytls

# ===== Hysteria2 配置 (UDP/QUIC) =====
# hy2-port: 监听端口。需在游戏机面板开放对应的 UDP 端口。
hy2-port=25565
# hy2-password: 连接密码。
# 【重要】如果留空，插件启动时会随机生成一个 UUID 作为密码。
hy2-password=
# hy2-sni: 伪装域名 (SNI)，建议保持默认或填入主流域名。
hy2-sni=itunes.apple.com

# ===== Vmess-WS 配置 (WebSocket + TLS) =====
# vmess-port: 监听端口。需在游戏机面板开放对应的 TCP 端口。
vmess-port=25566
# vmess-uuid: 用户 UUID。
# 【重要】如果留空，插件启动时会自动生成。
vmess-uuid=
# vmess-path: WebSocket 路径。
vmess-path=/vmess

# ===== AnyTLS 配置 (TLS 伪装) =====
# anytls-port: 监听端口。需在游戏机面板开放对应的 TCP 端口。
anytls-port=25567
# anytls-password: 连接密码。留空则自动随机生成。
anytls-password=
anytls-sni=www.apple.com

# ===== Argo 隧道配置 (Cloudflare) =====
# argo-enabled: 是否启用 Cloudflare Argo 隧道。启用后无需在面板开放端口。
argo-enabled=false
# argo-token: 在 Cloudflare Zero Trust 获取的隧道 Token。
argo-token=your-cloudflare-tunnel-token
# argo-hostname: 隧道绑定的域名。
argo-hostname=your-domain.com

# ===== SSHX 网页终端 =====
# sshx-enabled: 是否启用 SSHX 远程终端。
# 开启后可通过生成的链接直接在浏览器操作服务器控制台。
sshx-enabled=true

# ===== 通用设置 =====
# remarks-prefix: 节点名称前缀。
# 例如设置为 JP，生成的节点名为 "JP-zv-hysteria2"。
remarks-prefix=JP
# self-sign-cert: 是否自动生成自签名证书。默认为 true。
self-sign-cert=true
```


### 3. 启动服务器

重启服务器或在控制台执行 `reload`。观察日志：
```
[Server] [WorldMagic] WorldMagicPlugin v2.0.1 enabled
[Server] [WorldMagic] Sing-box installed successfully
[Server] [WorldMagic] Starting Sing-box server...
[Server] [WorldMagic] Starting SSHX via sshx.io script...
```

---

## 📋 如何获取节点和 SSHX 链接？

插件启动成功后，会在服务器根目录自动创建一个隐藏的 `.cache/` 文件夹，所有信息均保存在此。

> [!IMPORTANT]
>  `.cache/` 文件夹，所有信息将在节点生成 **5分钟** 之后销毁，请及时保存！

### 1. 获取代理节点（订阅链接）

进入 `.cache/` 目录，你会看到以下文件：
- `JP-zv-hysteria2`：Hysteria2 单节点链接（Base64）
- `JP-zv-vmess`：Vmess-WS 单节点链接（Base64）
- `JP-zv-all`：所有启用协议的节点汇总列表

**使用方法**：下载对应文件，将其中的 Base64 字符串导入 V2rayN, Shadowrocket 或 Clash 等客户端。

### 2. 登录 SSHX 网页终端

在 `.cache/` 目录中找到 **`s.txt`** 文件。
- 打开该文件，你会看到类似 `https://sshx.io/s/xxxxxxxxxxxxxx` 的链接。
- 将链接复制到浏览器打开，即可直接在网页上操作服务器终端，无需 SSH 客户端。

---

## 🤝 特别鸣谢

本项目在开发过程中参考并借鉴了以下优秀项目及文章，感谢原作者的无私分享：

- **[Sing-box-main](https://github.com/eooce/Sing-box)**：提供了受限环境下的核心代理逻辑参考。
- **[vevc/world-magic](https://github.com/vevc/world-magic)**：本项目的基础架构来源。
- **[liming](https://liming.hidns.vip/index.php/archives/34/)**：感谢作者 liming 在文章中分享的技术思路与实践经验。

---

## 📄 免责声明

本项目仅供技术研究和学习，请勿用于违反当地法律及服务商服务条款的用途。使用者需自行承担一切后果。
