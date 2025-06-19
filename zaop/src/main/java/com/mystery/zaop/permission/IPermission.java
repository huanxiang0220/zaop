package com.mystery.zaop.permission;

public interface IPermission {

    /**
     * 已经授权
     */
    void granted();

    /**
     * 授权授权
     */
    void cancel();

    /**
     * 拒绝授权（并且用户勾选了不再提醒）
     */
    void denied();

//    /**
//     * 用户拒绝过权限，但未勾线“不再提醒”
//     */
//    void rationale();
}
