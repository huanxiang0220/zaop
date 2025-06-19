package com.mystery.zaop.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mystery.zaop.R;


/**
 * 专门用跳转设置界面申请权限的
 */
public class PermissionSettingActivity extends Activity {

    //定义权限处理的标记--接收用户传递进来的
    private final static String PARAM_PERMSSION = "param_permission"; //权限名
    //跳转设置界面请求码
    public static final int REQUEST_CODE_SETTING = 301;
    //真正接收存储的变量
    private String[] permissions;
    private static IPermissionGranted iGrantedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        permissions = getIntent().getStringArrayExtra(PARAM_PERMSSION);
        if (permissions == null || iGrantedListener == null) {
            this.finish();
            return;
        }

        //检测是否授权
        boolean permissionResult = PermissionUtils.hasPermissionRequest(this, permissions);
        if (permissionResult) {
            //告诉外界，已经授权
            iGrantedListener.granted();
            this.finish();
            return;
        }

        if (!PermissionUtils.hasAlwaysDeniedPermission(this, permissions)) {
            //用户拒绝（并勾选不在提醒）操作；告诉外界，用户拒绝并勾选“不在提醒”
            //跳转设置界面
            Intent intent = PermissionUtils.getStartAndroidSettingIntent(this);
            intent.setFlags(0);//addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);会破坏任务栈导致提前回调onActivityResult
            startActivityForResult(intent, REQUEST_CODE_SETTING);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING) {//从设置界面回来了
            //验证一下结果
            if (PermissionUtils.hasPermissionRequest(this, permissions)) {
                iGrantedListener.granted();
            }
        }
        //无论是否有权限都finish
        this.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isFinishing()){
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);//让其Activity不要有任何动画
    }

    /**
     * 此权限申请专用于Activity，对外暴露，static
     */
    public static void requestPermissionAction(Context context, String[] permissions,
                                               IPermissionGranted grantedListener) {
        PermissionSettingActivity.iGrantedListener = grantedListener;
        Intent intent = new Intent(context, PermissionSettingActivity.class);
        //效果
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putStringArray(PARAM_PERMSSION, permissions);

        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iGrantedListener = null;
        permissions = null;
        System.gc();
    }

}