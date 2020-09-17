package com.daoyis.shennghuang.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.WindowManager;

import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.daoyis.shennghuang.BaseActivity;
import com.daoyis.shennghuang.BlueConnectListActivity;
import com.daoyis.shennghuang.Constans;
import com.daoyis.shennghuang.Views.X5WebView;
import com.daoyis.shennghuang.utils.MacUtils;
import com.daoyis.shennghuang.utils.PhotoUtils;
import com.daoyis.shennghuang.utils.PrintTextUtils;
import com.daoyis.shennghuang.utils.ScannerInterface;
import com.daoyis.shennghuang.utils.requestPermissionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.exception.ParameterErrorException;
import com.printer.sdk.exception.PrinterPortNullException;
import com.printer.sdk.exception.WriteException;
import com.printer.sdk.utils.XLog;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zpSDK.zpSDK.zpBluetoothPrinter;


/**
 * @author:"道翼(yanwen)"
 * @params:"代理升级"
 * @data:2019/10/15
 * @email:1966287146@qq.com
 */
public class WebViewActivity extends BaseActivity {

    private String TAG=WebViewActivity.class.getSimpleName();

    private ScanInterface scanDecode;//saomiao
    private Context mContext;
    // Member fields
    private BluetoothAdapter mBtAdapter;

    //组件声明
    private LinearLayout mLinearlayout;
    private X5WebView mWebView;//浏览器

    private static final int PERMISSION_REQUEST = 1;


    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;

    private String url;            //url地址
    private String pay_type = "1";
    //centertitle
    private String weburl;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_APN_SETTINGS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    ScannerInterface scanner;
    BroadcastReceiver scanReceiver;
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";//****重要

    zpBluetoothPrinter zpSDK;


    public WebViewActivity() {
    }

    public static void start(Activity activity, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MacUtils.initWindow(this, 0xff061fff, false, null, false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initDatass();
        initUI();
        setContentView(mLinearlayout);
        initScanner();

    }
    private void initScanner(){
        scanner = new ScannerInterface(this);
        //scanner.open();//扫描引擎上电；注意：请勿频繁调用，频繁开启串口会导致程序卡死。
        //scanner.close();//扫描引擎下电；注意：请勿频繁调用，频繁关闭串口会导致程序卡死。
        //scanner.resultScan();//恢复iScan默认设置。注意：频繁重置串口会导致程序卡死，退出程序调用一次即可。
        /**    ****重要
         * 设置扫描结果的输出模式，参数为0和1：
         * 0为模拟输出，同时广播扫描数据（在光标停留的地方输出扫描结果同时广播扫描数据）;
         * 1为广播输出（只广播扫描数据）；
         * 2为模拟按键输出；
         * */
        scanner.setOutputMode(1);
        scanner.enableFailurePlayBeep(true);//扫描失败蜂鸣反馈  ***测试扫描失败反馈接口，解码失败会出现错误提示
    }



    private void initDatass() {
        if(getIntent()!=null){
            if(getIntent().hasExtra("url")){
                url=getIntent().getStringExtra("url");
            }else{
                url= Constans.url;
            }

        }else{
            url= Constans.url;
        }


        Log.e("daoyi",url);
        requestPermissionUtils requestPermissionUtils=new requestPermissionUtils(this);
        requestPermissionUtils.request(PERMISSION_REQUEST,permissions);
        scanDecode=new ScanDecode(this);
        if(("true".equals(SystemProperties.get("persist.sys.keyreport", "false")))){
            scanDecode.initService("true");
        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void getBarcode(String s) {
                Toast.makeText(WebViewActivity.this,"扫描结果"+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {

            }
        });
    }



    //初始化UI
    private void initUI() {


        zpSDK=new zpBluetoothPrinter(this);

        LinearLayout.LayoutParams Line_prama = new LinearLayout.LayoutParams(-1, -1);
        mLinearlayout = new LinearLayout(this);
        mLinearlayout.setOrientation(LinearLayout.VERTICAL);
        mLinearlayout.setLayoutParams(Line_prama);
        mLinearlayout.setFitsSystemWindows(true);
        mLinearlayout.setClipToPadding(true);

        //webView
        mWebView = (X5WebView) new X5WebView(this,null);
        mWebView.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams mWebView_param = new LinearLayout.LayoutParams(-1, -1);
        mWebView.setLayoutParams(mWebView_param);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        // 其他细节操作
        settings.setCacheMode(settings.LOAD_NO_CACHE); // 关闭webview中缓存
        settings.setAllowFileAccess(true); // 设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");// 设置编码格式
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        mWebView.setWebChromeClient(new wvcc());
        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(new MJSINTER(), "android");
        mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
                String urls = request.getUrl().toString();
                try {
                    if (urls.startsWith("http:") || urls.startsWith("https:")) {
                        view.loadUrl(urls);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e){
                    return false;
                }
            }

            //页面加载结束时

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
            //界面开始加载时

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                dailog.show();

            }
            //正在加载时

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
//                dailog.show();

            }
        });

        mLinearlayout.addView(mWebView);

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void getBarcode(String s) {
                Toast.makeText(WebViewActivity.this,"扫描结果"+s,Toast.LENGTH_LONG).show();
                JsonObject jsonObject = toJson(s);
                Log.e("daoyi",jsonObject.toString());
                if (Build.VERSION.SDK_INT < 18) {
                    mWebView.loadUrl("javascript:trres(" + jsonObject + ")");
                } else {
                    mWebView.evaluateJavascript("javascript:trres(" + jsonObject + ")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                }

            }

            @Override
            public void getBarcodeByte(byte[] bytes) {

            }
        });

    }

    class  wvcc extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);

        }
        @Override
        public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                   JsResult arg3) {
            return super.onJsConfirm(arg0, arg1, arg2, arg3);
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
            Log.i("test", "openFileChooser 2");
            WebViewActivity.this.uploadFile = uploadFile;
            openFileChooseProcess();
            takePhoto();
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            Log.i("test", "openFileChooser 1");
            WebViewActivity.this.uploadFile = uploadFile;
            openFileChooseProcess();
            takePhoto();
        }



        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Log.i("test", "openFileChooser 3");
            WebViewActivity.this.uploadFile = uploadFile;
            openFileChooseProcess();
            takePhoto();
        }

        // For Android  >= 5.0
        public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
            WebViewActivity.this.uploadFiles = filePathCallback;
            openFileChooseProcess();
            takePhoto();
            return true;
        }



    }
    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "test"), 0);
    }

    /**
     * 拍照
     */

    private Uri imageUri;
    private final static int PHOTO_REQUEST = 100;
    private void takePhoto() {
        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(WebViewActivity.this, getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
        }
        PhotoUtils.takePicture(WebViewActivity.this, imageUri, PHOTO_REQUEST);
    }




    private class MJSINTER {


        @JavascriptInterface
        public boolean is_connect() {
//            SystemUtils.copy(WebViewActivity.this, weburl);
//            MacUtils.ToastShow(WebViewActivity.this, "成功复制到粘贴板", -2, 0);
            if(PrinterInstance.mPrinter != null){
                return true;
            }else{
                Log.e("daoyi","打印机未连接");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WebViewActivity.this,"打印机未连接",Toast.LENGTH_LONG).show();
//                        MacUtils.ToastShow(WebViewActivity.this,"打印机未连接");
                    }
                });

                return false;
            }

        }
        @JavascriptInterface
        public void connect_blue(){

            if (!mBtAdapter.isEnabled()) {
                // 打开蓝牙功能
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, 0x11);
            } else {
                Intent intent = new Intent(WebViewActivity.this, BlueConnectListActivity.class);
                startActivityForResult(intent, 0x12);
            }

        }

        /**
        * @param numbering :订单号
         * @param name 名称
         * @param count 数量
         * @param other 其他
        * */


        @JavascriptInterface
        public void print_Bcode(String numbering,String name,String count,String other){

//            print_Bcodes(numbering,name,count,other);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(!zpSDK.connect(devicesAddress))
//                    {
//                        Toast.makeText(WebViewActivity.this,"connect fail------", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//
//            });
//            zpSDK.pageSetup(700, 300);
//
//            zpSDK.drawText(10,0,numbering,
//                    24, 0,1,false,false);
//            zpSDK.drawBarCode(10,30,numbering,128
//                    ,false,1, 50);
//            zpSDK.drawLine(2,0,90,384,90,true);
//
//            zpSDK.drawText(10,95,name,
//                    24, 0,0,false,false
//            );
//            zpSDK.drawLine(2,0,120,384,120,true);
//
//            zpSDK.drawText(10,140,count,
//                    24, 0,1,false,false
//            );
//            zpSDK.drawBarCode(10,170,other ,128
//                    ,false,1, 50
//            );
//            zpSDK.print(0, 0);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            zpSDK.disconnect();

            if(PrinterInstance.mPrinter != null){
                try {
                    PrintTextUtils.getInstance().Print_Setting();
                    PrinterInstance.mPrinter.pageSetup(PrinterConstants.LablePaperType.Size_58mm, 700, 300);
                    PrintTextUtils.getInstance().ptint_Text(numbering);
                    PrintTextUtils.getInstance().Print_code(numbering);
                    PrintTextUtils.getInstance().Print_line();
                    PrintTextUtils.getInstance().Print_text(name,other,count);
                    PrinterInstance.mPrinter.print(PrinterConstants.PRotate.Rotate_0, 1);
                } catch (WriteException e) {
                    e.printStackTrace();
                } catch (ParameterErrorException e) {
                    e.printStackTrace();
                } catch (PrinterPortNullException e) {
                    e.printStackTrace();
                }
//                return true;
            }else {
                Log.e("daoyi", "打印机未连接");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WebViewActivity.this, "打印机未连接", Toast.LENGTH_LONG).show();
//                        MacUtils.ToastShow(WebViewActivity.this,"打印机未连接");
                    }
                });

//                return false;

            }


        }

        @JavascriptInterface //js接口声明
        public void takePhoto() {
            scanner.scan_start();
//            scanDecode.starScan();
//            Intent intent = new Intent(WebViewActivity.this, MipcaActivityCapture.class);
//            startActivityForResult(intent, 0x13);

        }
    }
    public JsonObject toJson(String a){

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("order_number",a);

        return jsonObject;
    }
    public JsonObject toJson(String name,String a){

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(name,a);

        return jsonObject;
    }

    public void print_Bcodes(String numbering,String name,String count,String other){


        if(!zpSDK.connect(devicesAddress))
        {
            Toast.makeText(this,"connect fail------", Toast.LENGTH_LONG).show();
            return;
        }
//        PrintTextUtils.getInstance().ptint_Text(numbering);
//        PrintTextUtils.getInstance().Print_code(numbering);
//        PrintTextUtils.getInstance().Print_line();
//        PrintTextUtils.getInstance().Print_text(name,other,count);
//        PrinterInstance.mPrinter.print(PrinterConstants.PRotate.Rotate_0, 1);


//        zpSDK.Write(new byte[]{0x1b,0x74,(byte) 0xfc});//japan
        zpSDK.pageSetup(700, 300);
//        zpSDK.drawText(100, 48/*48+100+56+56+80+80*/, "���͘���", 2, 0, 0, false, false);
        zpSDK.drawText(10,0,numbering,
                24, 0,1,false,false);
//        zpSDK.drawBarCode(8, 540, "12345678901234567", 128, true, 3, 60);
        zpSDK.drawBarCode(10,30,numbering,128
                ,false,1, 50);
        zpSDK.drawLine(2,0,90,384,90,true);

        zpSDK.drawText(10,95,name,
                24, 0,0,false,false
        );
        zpSDK.drawLine(2,0,120,384,120,true);

        zpSDK.drawText(10,140,count,
                24, 0,1,false,false
        );
        zpSDK.drawBarCode(10,170,other ,128
                ,false,1, 50
        );

        //zpSDK.drawBarCode(124,48+100+56+56+80+80+80 , "12345678901234567", 128, false, 3, 60);
        zpSDK.print(0, 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        zpSDK.disconnect();
    }

    public String JsontoString(JSONObject jsonObject){
        Gson gson = new Gson();
        String s = gson.toJson(jsonObject);
        return s;
    }

    String devicesAddress;
    String devicesName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0x13){
            String results = data.getStringExtra("result");
            if (Build.VERSION.SDK_INT < 18) {
                mWebView.loadUrl("javascript:javacalljs(" + results + ")");
            } else {
                mWebView.evaluateJavascript("javascript:javacalljs(" + results + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        }else if(resultCode==0x12){

            devicesAddress = data.getExtras().getString("mac");
            devicesName = data.getExtras().getString("name");
            connect2BlueToothdevice();
//            zpSDK.connect(devicesAddress);
        }
        if(requestCode==0x11){
            Intent intent = new Intent(this, BlueConnectListActivity.class);
            startActivityForResult(intent, 0x12);
        }
    }
    private static BluetoothDevice mDevice;
    public static PrinterInstance myPrinter;

    private void connect2BlueToothdevice() {

        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devicesAddress);
        devicesName = mDevice.getName();
        myPrinter = PrinterInstance.getPrinterInstance(mDevice, mHandler);
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {// 未绑定
            // IntentFilter boundFilter = new IntentFilter();
            // boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            // mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            PairOrConnect(true);
        } else {
            PairOrConnect(false);
        }
    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter();
            boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            this.registerReceiver(boundDeviceReceiver, boundFilter);
            boolean success = false;
            try {
                // // 自动设置pin值
                // Method autoBondMethod =
                // BluetoothDevice.class.getMethod("setPin", new Class[] {
                // byte[].class });
                // boolean result = (Boolean) autoBondMethod.invoke(mDevice, new
                // Object[] { "1234".getBytes() });
                // Log.i(TAG, "setPin is success? : " + result);

                // 开始配对 这段代码打开输入配对密码的对话框
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                success = (Boolean) createBondMethod.invoke(mDevice);
                // // 取消用户输入
                // Method cancelInputMethod =
                // BluetoothDevice.class.getMethod("cancelPairingUserInput");
                // boolean cancleResult = (Boolean)
                // cancelInputMethod.invoke(mDevice);
                // Log.i(TAG, "cancle is success? : " + cancleResult);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.e("daoyi", "createBond is success? : " + success);
        } else {
            new connectThread().start();

        }
    }

    public static boolean isConnected = false;// 蓝牙连接状态

    private class connectThread extends Thread {
        @Override
        public void run() {
            if (myPrinter != null) {
                isConnected = myPrinter.openConnection();
            }
        }
    }


    // 用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    Uni_app_lanya("已连接");
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Uni_app_lanya("失败");
                    XLog.i(TAG, "ZL at SettingActivity Handler() 连接失败!");
                    break;
                case PrinterConstants.Connect.CLOSED:
                    Uni_app_lanya("关闭");
                    isConnected = false;
                    XLog.i(TAG, "ZL at SettingActivity Handler() 连接关闭!");
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Uni_app_lanya("无设备");
                    break;
                // case 10:
                // if (setPrinterTSPL(myPrinter)) {
                // Toast.makeText(mContext, "蓝牙连接设置TSPL指令成功", 0).show();
                // }
                default:
                    break;
            }


        }

    };

    public void Uni_app_lanya(String status){
        JsonObject result = toJson("result", status);
        if (Build.VERSION.SDK_INT < 18) {
            mWebView.loadUrl("javascript:lanya(" + result + ")");
        } else {
            mWebView.evaluateJavascript("javascript:lanya(" + result + ")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {

                }
            });
        }
    }

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i(TAG, "bounding......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i(TAG, "bound success");
                        // if bound success, auto init BluetoothPrinter. open
                        // connect.
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        // 配对完成开始连接
                        if (myPrinter != null) {
                            new connectThread().start();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.i(TAG, "执行顺序----4");

                        mContext.unregisterReceiver(boundDeviceReceiver);
                        Log.i(TAG, "bound cancel");
                        break;
                    default:
                        break;
                }

            }
        }
    };

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("value");//***重要 Extral参数
            /** 如果条码长度>0，解码成功。如果条码长度等于0解码失败。*/
            if (intent.getAction().equals(RES_ACTION)){//获取扫描结果   ****重要 Action
                if(scanResult.length()>0){
//                    tvScanResult.append("Barcode:"+scanResult+"\n");
                    Toast.makeText(WebViewActivity.this,"扫描结果"+scanResult,Toast.LENGTH_LONG).show();
                    JsonObject jsonObject = toJson(scanResult);
                    Log.e("daoyi",jsonObject.toString());
                    if (Build.VERSION.SDK_INT < 18) {
                        mWebView.loadUrl("javascript:trres(" + jsonObject + ")");
                    } else {
                        mWebView.evaluateJavascript("javascript:trres(" + jsonObject + ")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    }

                }else{
                    /**扫描失败提示使用有两个条件：
                     1，需要先将扫描失败提示接口打开只能在广播模式下使用，其他模式无法调用。
                     2，通过判断条码长度来判定是否解码成功，当长度等于0时表示解码失败。
                     * */
                    Toast.makeText(getApplicationContext(), "解码失败!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //注册广播接受者,扫描结果的意图过滤器action一定要使用"android.intent.action.SCANRESULT"   ****重要
        scanReceiver = new ScannerResultReceiver();
        IntentFilter intentFilter = new IntentFilter(RES_ACTION);
        registerReceiver(scanReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(scanReceiver);
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
        } else {
            finish();
        }
    }





    @Override
    protected void onDestroy() {

        scanner.resultScan();//重置串口，恢复iScan默认设置。一般在退出整个程序时调用一次。

        //停止扫描
        //Stop scanning
        scanDecode.stopScan();
        //回复初始状态
        //Return to initial state
        scanDecode.onDestroy();

        super.onDestroy();
    }
}
