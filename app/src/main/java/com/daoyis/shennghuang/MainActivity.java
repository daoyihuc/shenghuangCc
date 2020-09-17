package com.daoyis.shennghuang;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daoyis.shennghuang.Activity.WebViewActivity;
import com.daoyis.shennghuang.utils.PrintTextUtils;
import com.google.gson.Gson;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.exception.ParameterErrorException;
import com.printer.sdk.exception.PrinterPortNullException;
import com.printer.sdk.exception.WriteException;
import com.printer.sdk.utils.XLog;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import zpSDK.zpSDK.zpBluetoothPrinter;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String TAG=MainActivity.class.getSimpleName();
    private TextView log_text;
    private Button blue_button;
    private Button test_button;

    private ScanInterface scanDecode;//saomiao
    private Context mContext;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    //
    zpBluetoothPrinter zpSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_date();
        init_view();
    }

    @Override
    protected void init_date() {
        super.init_date();
        scanDecode=new ScanDecode(this);
//        scanDecode.initService("true");
        mContext = MainActivity.this;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        SystemProperties.get("persist.sys.keyreport", "true");
    }

    @Override
    protected void init_view() {
        super.init_view();
        blue_button=findViewById(R.id.lanya);
        test_button=findViewById(R.id.test);
        log_text=findViewById(R.id.label_text);
        blue_button.setOnClickListener(this);
        test_button.setOnClickListener(this);

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void getBarcode(String s) {
                Log.e("daoyi",s);
                Toast.makeText(MainActivity.this,"扫描结果"+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {

            }
        });
        zpSDK=new zpBluetoothPrinter(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.lanya:
                if (!mBtAdapter.isEnabled()) {
                    // 打开蓝牙功能
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, 0x11);
                } else {
                    Intent intent = new Intent(this, BlueConnectListActivity.class);
                    startActivityForResult(intent, 0x12);
                }
                break;
            case R.id.test:
//                WebViewActivity.start(this,"https://weijia.utools.club/index.html#/");
//                print_Bcode("123232131313313","34242424","434242","43424");
                if (PrinterInstance.mPrinter != null) {
                    Print1("111","11","11","1");


//
                } else {

                    Toast.makeText(this,
                            "打印机未链接", Toast.LENGTH_SHORT)
                            .show();
////                    takePhoto();
////                    print_Bcode("454545144144-55252","daoyi","Qyt:252");
//
                }
                break;

        }
    }
    public void print_Bcode(String numbering,String name,String count,String other){


        if(PrinterInstance.mPrinter != null){
            try {
                PrintTextUtils.getInstance().Print_Setting();
                PrinterInstance.mPrinter.pageSetup(PrinterConstants.LablePaperType.Size_58mm, 700, 300);
                PrintTextUtils.getInstance().ptint_Text(numbering);
                PrintTextUtils.getInstance().Print_code(numbering);
                PrintTextUtils.getInstance().Print_line();
                PrintTextUtils.getInstance().Print_text(name, other, count);
                PrinterInstance.mPrinter.print(PrinterConstants.PRotate.Rotate_0, 1);
            } catch (PrinterPortNullException e) {
                e.printStackTrace();
            } catch (ParameterErrorException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }




    }
    public void Print1(String numbering,String name,String count,String other)
    {

        if(!zpSDK.connect(devicesAddress))
        {
            Toast.makeText(this,"connect fail------", Toast.LENGTH_LONG).show();
            return;
        }

        zpSDK.Write(new byte[]{0x1b,0x74,(byte) 0xfc});//japan
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





        zpSDK.drawBarCode(124,48+100+56+56+80+80+80 , "12345678901234567", 128, false, 3, 60);
        zpSDK.print(0, 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        zpSDK.disconnect();
    }





    public JSONObject toJson(String numbering,String name,String other ){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("order_number",numbering);
            jsonObject.put("name",name);
            jsonObject.put("other",other);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public String JsontoString(JSONObject jsonObject){
        Gson gson = new Gson();
        String s = gson.toJson(jsonObject);
        return s;
    }



    String devicesAddress;
    String devicesName;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0x12){
            log_text.setText("链接中。。。");
             devicesAddress = data.getExtras().getString("mac");
             devicesName = data.getExtras().getString("name");
            connect2BlueToothdevice();
        }
        if(requestCode==0x11){
            Intent intent = new Intent(this, BlueConnectListActivity.class);
            startActivityForResult(intent, 0x12);
        }


        if (requestCode == 0x13) {

        }
    }

    private static BluetoothDevice mDevice;
    public static PrinterInstance myPrinter;

    private void connect2BlueToothdevice() {

        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devicesAddress);
        devicesName = mDevice.getName();
//        zpSDK.connect(devicesAddress);
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
                    log_text.setText("链接成功");
                    // TOTO 暂时将TSPL指令设置参数的设置放在这
                    // if (setPrinterTSPL(myPrinter)) {
                    // if (interfaceType == 0) {
                    // Toast.makeText(mContext,
                    // R.string.settingactivitty_toast_bluetooth_set_tspl_successful,
                    // 0)
                    // .show();
                    // } else if (interfaceType == 1) {
                    // Toast.makeText(mContext,
                    // R.string.settingactivity_toast_usb_set_tspl_succefful,
                    // 0).show();
                    // }
                    // }
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    log_text.setText("链接失败");

                    XLog.i(TAG, "ZL at SettingActivity Handler() 连接失败!");
                    break;
                case PrinterConstants.Connect.CLOSED:
                    log_text.setText("链接关闭");
                    isConnected = false;
                    XLog.i(TAG, "ZL at SettingActivity Handler() 连接关闭!");
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    log_text.setText("当前无链接设备");
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
    /**
     * 返回键监听
     * Return key listening
     */
    private long mkeyTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    boolean cn = "CN".equals(getApplicationContext().getResources().getConfiguration().locale.getCountry());
//                    if (cn) {
//                        ToastUtils.showShortToastSafe("再次点击返回退出");
//                    } else {
//                        ToastUtils.showShortToastSafe("Press the exit again");
//                    }
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //停止扫描
        //Stop scanning
        scanDecode.stopScan();
        //回复初始状态
        //Return to initial state
        scanDecode.onDestroy();
        super.onDestroy();
    }
}
