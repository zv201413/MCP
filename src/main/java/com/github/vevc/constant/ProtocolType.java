package com.github.vevc.constant;

/**
 * Protocol type enumeration for multi-protocol support
 * @author vevc
 */
public enum ProtocolType {
    HYSTERIA2("hysteria2"),
    VMESS_WS("vmess-ws"),
    VLESS_WS("vless-ws"),
    NAIVE("naive"),
    ANYTLS("anytls"),
    ARGO("argo"),
    TUIC("tuic");

    private final String value;

    ProtocolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProtocolType fromString(String text) {
        for (ProtocolType type : ProtocolType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
