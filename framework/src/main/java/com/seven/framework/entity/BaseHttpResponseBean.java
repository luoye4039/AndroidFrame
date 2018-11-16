package com.seven.framework.entity;

import java.net.Socket;

public class BaseHttpResponseBean<T> {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public T data;
    public int result;
    public int errorCode;
}
