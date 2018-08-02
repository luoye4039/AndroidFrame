package com.seven.framework.entity;

import java.io.Serializable;

public class SelectDialogBean implements Serializable {
    public Object type;
    public String content;
    public int btBackGround;
    public int textColor;
    public int textSize;

    public SelectDialogBean() {
    }

    public SelectDialogBean(Object type, String content) {
        this.type = type;
        this.content = content;
    }
}
