package com.github.tvbox.osc.bean;

/**
 * 切换按钮配置
 */
public enum API {
    MAIN(0, "主地址", "tvbox"),
    BACKUP(1, "备用地址", "tvbox_backup"),
    IPTV_LOCAL(2, "本地IPTV", "iptv_local");

    private final int type;
    private final String label;
    private final String tag;

    API(int type, String label, String tag) {
        this.type = type;
        this.label = label;
        this.tag = tag;
    }

    //
    public static API get(int type) {
        API[] values = values();
        for (API api : values) {
            if (type == api.type) {
                return api;
            }
        }
        return MAIN;
    }

    public int getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getTag() {
        return tag;
    }
}
