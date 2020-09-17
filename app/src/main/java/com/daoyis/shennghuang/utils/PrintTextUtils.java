package com.daoyis.shennghuang.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.daoyis.shennghuang.Applications;
import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.exception.ParameterErrorException;
import com.printer.sdk.exception.PrinterPortNullException;
import com.printer.sdk.exception.WriteException;

/**
 * @author:"道翼(yanwen)"
 * @params:"打印工具类"
 * @data:20-8-19 上午11:57
 * @email:1966287146@qq.com
 */
public class PrintTextUtils {

    public PrintTextUtils(){

    }
    private static PrintTextUtils my;
    private static class signles{
        public PrintTextUtils single(){
            my=new PrintTextUtils();
            return my;
        }
    }

    public static PrintTextUtils getInstance(){
        return new signles().single();
    }

    //二维码测试
    public String test(){
        Toast.makeText(Applications.getInstance(),"454545454",Toast.LENGTH_LONG).show();
        Log.e("daoyi","测试");
        return "11";
    }


    //二维码测试
    public void test(String value){
        PrinterInstance.mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        Barcode barcode2;
        if(value==null||value.equals("")){
              barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6,
                    "123456");
        }else{
             barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6,
                    value);
        }
        PrinterInstance.mPrinter.printBarCode(barcode2);
    }
    //文字打印测试
    public void Print_text(Context context,String value) {
        if (PrinterInstance.mPrinter == null) {
            Toast.makeText(context,
                    "打印机未链接", Toast.LENGTH_SHORT)
                    .show();
        }else{
            PrinterInstance.mPrinter.printText(value + "\r\n");
        }
    }
    //设置打印机部分参数
    public void Print_Setting(){
        PrinterInstance.mPrinter.initPrinter();
        PrinterInstance.mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LNCH,PrinterConstants.Command.ALIGN_LEFT);
        PrinterInstance.mPrinter.setFont(0,5,5,0,0);
        PrinterInstance.mPrinter.setLeftMargin(20);

    }
    //文字打印
    public void ptint_Text(String number) throws PrinterPortNullException, ParameterErrorException, WriteException {
        PrinterInstance.mPrinter.drawText(0,0,number,
                PrinterConstants.LableFontSize.Size_24, PrinterConstants.PRotate.Rotate_0,1,0,0
        );
    }

    //条形码打印
    public void Print_code(String json) throws PrinterPortNullException, ParameterErrorException, WriteException {
        PrinterInstance.mPrinter.drawBarCode(1,30,json ,PrinterConstants.PBarcodeType.CODE128
                ,1,50, PrinterConstants.PRotate.Rotate_0
        );
    }
    //直线绘制
    public void Print_line(){
        PrinterInstance.mPrinter.drawLine(1,0,90,384,90,true);
    }
    //文本绘制
    public void Print_text(String name,String json,String other) throws WriteException, ParameterErrorException, PrinterPortNullException {
        PrinterInstance.mPrinter.drawText(0,95,name,
                PrinterConstants.LableFontSize.Size_24, PrinterConstants.PRotate.Rotate_0,0,0,0
        );
        PrinterInstance.mPrinter.drawLine(1,0,120,384,120,true);

        PrinterInstance.mPrinter.drawText(0,140,other,
                PrinterConstants.LableFontSize.Size_24, PrinterConstants.PRotate.Rotate_0,1,0,0
        );
        PrinterInstance.mPrinter.drawBarCode(1,170,json ,PrinterConstants.PBarcodeType.CODE128
                ,1,50, PrinterConstants.PRotate.Rotate_0
        );
    }
}
