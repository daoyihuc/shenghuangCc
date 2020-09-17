package com.daoyis.shennghuang;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * @author:"道翼(yanwen)"
 * @params:"蓝牙"
 * @data:20-8-18 下午4:04
 * @email:1966287146@qq.com
 */
public class Applications extends Application {

    private static Applications sInstance;

    public static Applications getInstance() {
        return sInstance;
    }

    public Applications() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
