package com.seven.framework.manager.uploadordown;

public class DownFileBean {
    public String url;
    public long totalLength;
    public long downLength;
    public int downPercent;
    public String downFilePath;
    public boolean isCompleted;

    public DownFileBean() {
    }

    public DownFileBean(String url, long totalLength, long downLength, int downPercent, String downFilePath) {
        this.url = url;
        this.totalLength = totalLength;
        this.downLength = downLength;
        this.downPercent = downPercent;
        this.downFilePath = downFilePath;
    }
}
