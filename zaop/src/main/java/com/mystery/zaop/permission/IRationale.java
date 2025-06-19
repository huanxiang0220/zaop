package com.mystery.zaop.permission;

/**
 * 用来备客户端调用，用来操作继续申请权限
 */
public interface IRationale {

    /**
     * 继续申请权限
     */
    void resume();
}
