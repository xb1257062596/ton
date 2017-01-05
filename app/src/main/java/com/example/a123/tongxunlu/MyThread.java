package com.example.a123.tongxunlu;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 123 on 2017/1/4.
 * 功能：通过电话号码 从网上得到其地址和是否骚扰的信息
 * 通过 Message 方法传递给 Handler
 */

public class MyThread extends Thread {
    private String phone;
    private Handler handler;

    public MyThread(String phone, Handler handler) {
        this.phone = phone;
        this.handler = handler;
    }

    @Override
    public void run() {
        String address;
        HttpURLConnection connection= null;
        address = "http://op.juhe.cn/onebox/phone/query?tel="+phone+"&key=3f55145f2e85e1e7815f1ea33e774fd2";
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setReadTimeout(8000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String response = buffer.toString();
            JSONObject jsonObject = new JSONObject(response);
            JSONObject result = jsonObject.getJSONObject("result");
            String address1=result.getString("province")+result.getString("city");
            int iszhapian=result.getInt("iszhapian");
            Log.d("test",""+iszhapian);
            String harass;
            if(iszhapian==1){
                harass="骚扰";
            }else{
                 harass="不是骚扰";
            }
            Bundle bundle= new Bundle();
            bundle.putString("骚扰",harass);
            bundle.putString("地址",address1);
            Message message=handler.obtainMessage();
            message.obj=bundle;
            message.what=1;
            message.sendToTarget();
        }catch (Exception e){
            Log.d("test",e.getMessage());
        }
        super.run();
    }
}
