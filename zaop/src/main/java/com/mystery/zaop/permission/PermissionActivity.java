package com.mystery.zaop.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.mystery.zaop.R;
import com.mystery.zaop.annation.PermissionConst;
import com.mystery.zaop.utils.StatusBarUtils;


/**
 * 专门用来申请权限的
 */
public class PermissionActivity extends Activity {

    //定义权限处理的标记--接收用户传递进来的
    private final static String PARAM_PERMISSION = "param_permission"; //权限名
    private final static String PARAM_PERMISSION_CODE = "param_permission_code";  //权限码
    public final static int PARAM_PERMISSION_CODE_DEFAULT = PermissionConst.PARAM_PERMSSION_CODE_DEFAULT;

    //真正接收存储的变量
    private String[] permissions;
    private int requestCode;
    private static IPermission iPermissionListener;

    private final static String PARAM_TITLE = "param_permission_title";  //权限说明标题
    private final static String PARAM_DESC = "param_permission_desc";  //权限说明描述

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        StatusBarUtils.setStatusBarColor(this, Color.parseColor("#FFFFFF"));
        StatusBarUtils.StatusBarLightMode(this);

        permissions = getIntent().getStringArrayExtra(PARAM_PERMISSION);
        requestCode = getIntent().getIntExtra(PARAM_PERMISSION_CODE, PARAM_PERMISSION_CODE_DEFAULT);

        if (permissions == null || iPermissionListener == null) { //requestCode < 0
            this.finish();
            return;
        }

        //检测是否授权
        boolean permissionResult = PermissionUtils.hasPermissionRequest(this, permissions);
        if (permissionResult) {
            //告诉外界，已经授权
            iPermissionListener.granted();
            this.finish();
            return;
        }

//        // 权限被拒绝过但没有选择“不再询问“返回为true；有需求第一次被拒绝之后，第二次申请的时候将提示用户由用户决定是否申请
//        if (PermissionUtils.hasAlwaysDeniedPermission(this, permissions)) {
//            iPermissionListener.rationale();
//            this.finish();
//            return;
//        }


        LinearLayout llTitle = findViewById(R.id.ll_title);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvDesc = findViewById(R.id.tv_desc);
        tvTitle.postDelayed(() -> {
            if (!isFinishing() && getIntent() != null) {
                llTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(getIntent().getStringExtra(PARAM_TITLE));
                tvDesc.setText(getIntent().getStringExtra(PARAM_DESC));
            }
        }, 200);

        //申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    /**
     * 向系统申请权限得到的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //返回的结果需要验证一下，是否完全通过
        if (PermissionUtils.requestPermissionSuccess(grantResults)) {
            iPermissionListener.granted();

            this.finish();
            return;
        }

        //1、申请权限的回调，走到这里必然已经申请过了，
        //2、如果用户点击了，拒绝（勾选了“不在提醒”）等操作
        if (!PermissionUtils.hasAlwaysDeniedPermission(this, permissions)) {
            //用户拒绝（并勾选不在提醒）操作；告诉外界，用户拒绝并勾选“不在提醒”
            iPermissionListener.denied();
            this.finish();
            return;
        }

        //告诉外界，权限被取消
        iPermissionListener.cancel();
        this.finish();
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
                                               int requestCode,
                                               String title,
                                               String desc,
                                               IPermission iPermission) {
        PermissionActivity.iPermissionListener = iPermission;
        Intent intent = new Intent(context, PermissionActivity.class);
        //效果
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_PERMISSION_CODE, requestCode);
        bundle.putStringArray(PARAM_PERMISSION, permissions);

        //标题、描述
        bundle.putString(PARAM_TITLE, title);
        bundle.putString(PARAM_DESC, desc);

        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iPermissionListener = null;
        permissions = null;
        requestCode = 0;
        System.gc();
    }

}