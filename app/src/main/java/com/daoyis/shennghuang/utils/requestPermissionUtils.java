package com.daoyis.shennghuang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 道义（daoyi）
 * @version 1.0
 * @date 2020-05-12
 * @params
 **/
public class requestPermissionUtils {

    private Context context;
    private  String[] permissions=null;
    private  List<String> mPermissionList = new ArrayList<>();
    private int code;
    private static final int PERMISSION_REQUEST = 1;

    public requestPermissionUtils(Context c){
        this.context=c;
    }

    public void request(int code,String...strings){
        this.code=code;
        this.permissions=strings;
        requestPermission();
    }

    public boolean requests(int code,String...strings){
        this.code=code;
        this.permissions=strings;
        return checkPermission();
    }


    /**
     * 请求权限
     */
    private void requestPermission() {
        mPermissionList.clear();
        /**
         * 判断哪些权限未授予
         */
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        /**
         * 判断是否为空
         */
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            //delayEntryPage();

        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions((Activity) context, permissions, code);
        }
    }

    //检查权限
    public boolean checkPermission() {

        mPermissionList.clear();
        /**
         * 判断哪些权限未授予
         */
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        /**
         * 判断是否为空
         */
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            return true;
        } else {//请求权限方法
            return false;
        }
    }

}

