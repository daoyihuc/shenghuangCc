package com.daoyis.shennghuang.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.Ringtone;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author 颜文（daoyi）
 * @version 1.0
 * @date 2019/8/27 15:29
 */
public class MacUtils {

    /**
     * @Params LEFT_RIGHT:从左至右
     * @Params TOP_BOTTOM:从上至下
     * @Params BOTTOM_TOP:从下至上
     * @Params RIGHT_LEFT:从右至左
     * */

    private static final int LEFT_RIGHT = 0;
    private static final int TOP_BOTTOM = 1;
    private static final int BOTTOM_TOP = 2;
    private static final int RIGHT_LEFT = 3;


    private static Context mContext;

    private static Activity mActivity;

    public static void init(Context context){
        if (context != null){
            mContext = context;
        }else {
            Log.e("doayi","未初始化lib");
            return;
        }
    }


    public static String getMac(Context context) {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = (String) getMacAddress(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }
    public static String getMac() {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = (String) getMacAddress(mContext);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    //Mac地址获取
    public static String getMacAddress(Context context) {
        String macAddress = null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0 && res1.length() != 0) {
                    res1.deleteCharAt(res1.length() - 1);
                } else if (res1.length() == 0) {
                    res1.deleteCharAt(res1.length());
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    //设置全屏
    public static void initWindow(Activity activity) {
        //隐藏标题
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = activity.getWindow();
        // 添加 statusView 到布局中
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
        while (decorView.getChildCount() >= 2) {
            decorView.removeViewAt(1);
        }
//        //设置当前窗口,全屏显示
        window.setFlags(flag, flag);
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    //设置全屏（图片沉底）
    public static void initWindow(Activity activity, boolean isImageBg, boolean isblack) {

        if(isImageBg) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if(isblack){
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }else{
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
                // 透明状态栏
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // 生成一个状态栏大小的矩形
                View statusView = createStatusView(activity, 0x00000000, false, null);
                // 添加 statusView 到布局中
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
                while (decorView.getChildCount() >= 2) {
                    decorView.removeViewAt(1);
                }
                statusView.setBackgroundColor(0x00000000);
                decorView.addView(statusView);
                // 设置根布局的参数
                setRootView(activity);
            }
        } else{
            initWindow(activity);
        }
    }

    //系统SDK版本为21时，透明状态栏,导航栏：
    public static void initWindow(Activity activity, int color, boolean isDrawable, GradientDrawable drawable, boolean isback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if(isback){
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
            }
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color, isDrawable, drawable);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
            while (decorView.getChildCount() >= 2) {
                decorView.removeViewAt(1);
            }
            statusView.setBackgroundColor(color);
            decorView.addView(statusView);
            // 设置根布局的参数
            setRootView(activity);
        }
    }

    public static void initWindows(Activity activity, int color, boolean isDrawable, GradientDrawable drawable, boolean isback)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if(isback){
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS|
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
            }
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color, isDrawable, drawable);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            decorView.setFitsSystemWindows(true);
//            decorView.setClipToPadding(true);
            //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
            while (decorView.getChildCount() >= 2) {
                decorView.removeViewAt(1);
            }
            decorView.addView(statusView);
            // 设置根布局的参数
//            setRootView(activity);
        }
    }

    //系统SDK版本为21时，设置沉淀式状态栏
    public static void initWindow(Activity activity, int color, boolean isDrawable, GradientDrawable drawable, boolean isback, float Alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if(isback){
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
            }
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color, isDrawable, drawable);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
            while (decorView.getChildCount() >= 2) {
                decorView.removeViewAt(1);
            }
//            statusView.setBackgroundColor(color);
//            statusView.setAlpha(Alpha);
            decorView.addView(statusView);
            // 设置根布局的参数
//            setRootView(activity);
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }


    /*
     * 道，半透明状态栏
     *
     * */
    public static void setStatus_one(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //状态栏清除
    public static void clearStatus(Activity activity){
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            decorView.setFitsSystemWindows(true);
//            decorView.setClipToPadding(true);
        //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
        while (decorView.getChildCount() >= 2) {
            decorView.removeViewAt(1);
        }
    }


    /*
    * 道，全透明状态栏
    *
    * */
    public static void setStatus_two(Activity activity){
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
//            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            View statusBarView = new View(activity);
//            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    getStatusBarHeight(activity));
//            statusBarView.setLayoutParams(lp);
////            statusBarView.setBackgroundColor(color);
//            //在一个界面在来回切换顶部状态栏的时候导致透明度的状态栏不能显示 需remove掉
//            while (decorView.getChildCount() >= 2) {
//                decorView.removeViewAt(1);
//            }
//            decorView.addView(statusBarView);
        }
    }


    /**
     * 生成一个状态栏大小的矩形
     *
     * @param activity
     * @param color
     * @param isDrawable
     * @return
     */
    @SuppressLint("NewApi")
    private static View createStatusView(Activity activity, int color, boolean isDrawable, GradientDrawable drawable) {
        // 绘制一个和状态栏一样高的矩形
        // 获得状态栏高度
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusView.setLayoutParams(params);
        if (isDrawable) {
            statusView.setBackground(drawable);
        } else {
            statusView.setBackgroundColor(color);
        }
//        statusView.setId(R.id.FAKE_STATUS_BAR_VIEW_ID);
        return statusView;
    }


    /**
     * 设置根布局参数
     */
    public  static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


    //设置颜色渐变

    /**
     * @param colors :渐变颜色值
     * @param type :渐变方向
     * @return GradientDrawable:返回一个drawable
     * */


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("WrongConstant")
    public static GradientDrawable GradientColor(int[] colors, int type) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        switch (type) {
            case LEFT_RIGHT:
                gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                gradientDrawable.setColors(colors);
                gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
                break;
            case TOP_BOTTOM:
                gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                gradientDrawable.setColors(colors);
                gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
                break;
            case BOTTOM_TOP:
                gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                gradientDrawable.setColors(colors);
                gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
                break;
            case RIGHT_LEFT:
                gradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
                gradientDrawable.setColors(colors);
                gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
                break;
        }
        return gradientDrawable;
    }


    /**
     * @param color:背景颜色
     * @param radius:圆角属性
     * @return GradientDrawable:返回一个drawable
     * */
    public static GradientDrawable gradientButton(int color, int radius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }

    //设置像素dp转换
    public static int dpto(Activity activity, int dp) {
        return (int) (activity.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    public static int dpto(int dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    public static float dptof(float dp) {
        return  (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    /**
     *
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(float pxValue) {

        final float scale = mContext.getResources().getDisplayMetrics().density;
        BigDecimal bigDecimal=new BigDecimal(pxValue);
        BigDecimal bigDecimal1=new BigDecimal(scale);
        BigDecimal bigDecimal2=new BigDecimal(0.5f);
        BigDecimal divide = bigDecimal.divide(bigDecimal1,6, BigDecimal.ROUND_HALF_UP);
        divide.add(bigDecimal2);

        return  (divide.floatValue());
    }

    /**
     * 读取视频时长
     * @param sum 视频路径
     *
     * @return
     */
    public static String readVideo(long sum) {
        String durationStr = "";
        double sum1 = (double) sum;
        System.out.println("视频时长总秒数：" + sum1);

        BigDecimal duration = BigDecimal.valueOf(sum);
        durationStr = durationFormatToString(duration);
        System.out.println("视频时长:" + durationStr);

        return durationStr;
    }

    /**
     * 将视频时长转换成"00:00:00.000"的字符串格式
     * @param duration 视频时长（单位：秒）
     * @return
     */
    public static String durationFormatToString(BigDecimal duration) {
        BigDecimal nine = BigDecimal.valueOf(9);
        BigDecimal sixty = BigDecimal.valueOf(60);

        BigDecimal second = duration.divideAndRemainder(sixty)[1];
        BigDecimal minute = duration.subtract(second).divide(sixty).divideAndRemainder(sixty)[1];
        BigDecimal hour = duration.subtract(second).divideToIntegralValue(BigDecimal.valueOf(3600));

        String str = "";
        if (hour.compareTo(nine) > 0) {
            str += hour.intValue() + ":";
        } else {
            str += "0" + hour.intValue() + ":";
        }
        if (minute.compareTo(nine) > 0) {
            str += minute.intValue() + ":";
        } else {
            str += "0" + minute.intValue() + ":";
        }
        if (second.compareTo(nine) > 0) {
            str += second.intValue() + ".000";
        } else {
            str += "0" + second.intValue() + ".000";
        }
        return str;
    }

    //手机获取IMEI码
    @SuppressLint("MissingPermission")
//    public String getIMEI(Context context) {
//        String imei;
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_STATE},100);
//            return "";
//        }else{
//            imei = telephonyManager.getDeviceId();
//        }
//
//        //在次做个验证，也不是什么时候都能获取到的啊
//        if (imei == null) {
//            imei = "";
//        }
//        return imei;
//
//    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    //将资源文件转换为url
    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    //讲资源文件转换为string
    public static String resourceIdToString(Context context, int resourceId) {
        return ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId;
    }


    public static String comdify(String value) {
        DecimalFormat df = null;
        if (value.indexOf(".") > 0) {
            int i = value.length() - value.indexOf(".") - 1;
            switch (i) {
                case 0:
                    df = new DecimalFormat("###,##0");
                    break;
                case 1:
                    df = new DecimalFormat("###,##0.0");
                    break;
                case 2:
                    df = new DecimalFormat("###,##0.00");
                    break;
                case 3:
                    df = new DecimalFormat("###,##0.000");
                    break;
                case 4:
                    df = new DecimalFormat("###,##0.0000");
                    break;
                default:
                    df = new DecimalFormat("###,##0.00000");
                    break;
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(value);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }
    //字符切割
    public static String rectString(String rect){
        String t_a=rect.substring(0,4);
        String y_b=rect.substring(5,7);

        String t=y_b+"/"+t_a;
        return t;
    }
    //字符还原
    public static String BackString(String rect){
        String t_a=rect.substring(0,2);
        String y_b=rect.substring(3,7);

        String t=y_b+"-"+t_a;
        return t;
    }

    //字符切割、
    public static String rectOther(String rect, int src, int dest){
        String t_a=rect.substring(src,dest);
        return t_a;
    }

    //判断年份是否超出
    public static int timeYear_eXceed(String time){
        int flag=0;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy");
        try {
            Date date=simpleDateFormat.parse(time);//时间转换
            String StringDate=simpleDateFormat.format(System.currentTimeMillis());
            Date localDate=simpleDateFormat.parse(StringDate);

            if(date.getTime()>localDate.getTime()){
                //传入时间大于本地时间
                flag=1;
            }else{
                //传入时间小于本地时间
                flag=0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    //判断月份是否超出
    public static int timeeXceed(String time){
        int flag=0;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");
        try {
            Date date=simpleDateFormat.parse(time);//时间转换
            String StringDate=simpleDateFormat.format(System.currentTimeMillis());
            Date localDate=simpleDateFormat.parse(StringDate);

            if(date.getTime()>localDate.getTime()){
                //传入时间大于本地时间
                flag=1;
            }else{
                //传入时间小于本地时间
                flag=0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    //月份超出提示
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void ToastShow(Context context, String data){
        ToastShow(context, data, -2, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void ToastShow(Context context, String data, int width, int height){
        Toast toast= Toast.makeText(context,data, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        View view=toast.getView();
        view.setAlpha(0.7f);

        GradientDrawable gradientDrawable=new GradientDrawable();
        gradientDrawable.setColor(0xff000000);
        gradientDrawable.setCornerRadius(MacUtils.dpto(4));
        view.setBackground(gradientDrawable);
        TextView tv=view.findViewById(android.R.id.message);
        tv.setLayoutParams(new LinearLayout.LayoutParams(width,-2));
        tv.setTextColor(0xffffffff);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(13);
        tv.setMaxLines(1);
        tv.setPadding(20,14,20,14);
        toast.show();
    }
    //时间格式转化
    public static String formatDate(String res, String srcFormat, String destFormat){
        String flag="";

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(srcFormat);
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat(destFormat);
        Date date = null;
        try {
            date=simpleDateFormat.parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        flag=simpleDateFormat1.format(date);


        return flag;
    }

    //月份增加
    public static String changeMonth(String time, String type, int count){

        String flag="";

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(type);
        Date date = null;
        try {
            date=simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,count);
        Date dstTime = calendar.getTime();
        flag=simpleDateFormat.format(dstTime);

        return flag;
    }

    //将字符转换为date
    public static Date String_Date(String res, String type){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(type);
        Date date = null;
        try {
            date=simpleDateFormat.parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //将date转换为字符
    public static String Date_String(Date date, String type){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(type);
        Date dates = null;
        String flag="";
        flag=simpleDateFormat.format(date);
        return flag;
    }

    //home返回
    public static void OnHomeBack(Context context){
        Intent intent=new Intent(Intent.ACTION_MAIN);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//如果是服务里调用，必须加入new task标识

        intent.addCategory(Intent.CATEGORY_HOME);


//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        context.startActivity(intent);


    }






    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);

    }




    private static Bitmap compressMatrix(String path) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.2f, 0.2f);
        Bitmap bm = BitmapFactory.decodeFile(path);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        return bitmap;
    }

    //  去除小数点后。00
    public static String remove_OO(String as){
        if(as==null||as.equals("")){
            return "";
        }
        BigDecimal value = null;
        try {
            value = new BigDecimal(as);
        } catch (Exception e) {}
        BigDecimal noZeros = value.stripTrailingZeros();
        String result = noZeros.toPlainString();
        return result;
    }


    public static int remove_OO_toInt(String as){
        if(as==null||as.equals("")){
            return 0;
        }
        BigDecimal value = new BigDecimal(as);
        BigDecimal noZeros = value.stripTrailingZeros();
        int result = noZeros.intValue();
        return result;
    }




        /**

         * 获取视频文件截图

         *

         * @param path 视频文件的路径

         * @return Bitmap 返回获取的Bitmap

         */

        public  static Bitmap getVideoThumb(String path) {

            MediaMetadataRetriever media = new MediaMetadataRetriever();

            media.setDataSource(path);

            return  media.getFrameAtTime();

        }

    //Bitmap对象保存味图片文件
    public static void saveBitmapFile(Bitmap bitmap,String s) {
            File file=new File(s);//将要保存图片的路径
            try{
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }

    }
//格式化字符
    //convert转换
    public static String  Convert_IS(String type,String value){
            String resulte="";
            DecimalFormat decimalFormat = new DecimalFormat(type);
            resulte=decimalFormat.format(value);
            return resulte;
    }

    //获取网络视频第一帧
    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }
    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static Ringtone mRingtone;


}
