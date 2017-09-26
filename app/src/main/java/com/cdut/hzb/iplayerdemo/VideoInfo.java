package com.cdut.hzb.iplayerdemo;

/**
 * Created by hans on 2017/9/26 0026.
 */

class VideoInfo {
    private String displayName;
    private String filePath;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "displayName='" + displayName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
