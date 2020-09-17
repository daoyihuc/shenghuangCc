package com.daoyis.shennghuang;



import com.google.gson.JsonObject;

/**
 * @author:"道翼(yanwen)"
 * @params:"代理升级"
 * @data:20-8-20 上午9:13
 * @email:1966287146@qq.com
 */
public class testMain {

    public static void main(String [] args)  {


        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("qwe","12");
        jsonObject.addProperty("ss","11");

        System.out.println(jsonObject.toString());
    }
}
