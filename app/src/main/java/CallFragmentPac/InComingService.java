package CallFragmentPac;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.MyThread;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by 123 on 2016/12/30.
 * 开启一个服务来监听是否有电话打入
 */

public class InComingService extends Service {
    private MyDataBaseHelper myDataBaseHelper;
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private String phoneNumber;


    /**
     * 处理者 用来处理线程传过来的数据
     * 添加到数据库中去
     * 发出广播有电话打入 将数据全部通过广播传过去
     */
    private Handler handler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    Calendar c = Calendar.getInstance();
                    int month=c.get(Calendar.MONTH)+1;
                    String month1=""+month;
                    if(month<10){
                        month1="0"+month;
                    }
                    int date = c.get(Calendar.DATE);
                    String date1=""+date;
                    if(date<10){
                        date1="0"+date;
                    }
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    Bundle bundle1= (Bundle) msg.obj;
                    Bundle bundle= new Bundle();
                    bundle.putString("地址",bundle1.getString("地址"));
                    bundle.putString("骚扰",bundle1.getString("骚扰"));
                    bundle.putString("电话",phoneNumber);
                    bundle.putString("时间",month1+"."+date1+" "+hour+":"+minute);
                    if(chaDataBase(phoneNumber)==null){
                        bundle.putString("姓名","陌生人");
                    }else{
                        bundle.putString("姓名",chaDataBase(phoneNumber));
                    }
                    bundle.putString("类型","呼入");

                    Information information=new Information();
                    information.setAddress(bundle.getString("地址"));
                    information.setName(bundle.getString("姓名"));
                    information.setPhone(bundle.getString("电话"));
                    information.setHarass(bundle.getString("诈骗"));
                    information.setTime(bundle.getString("时间"));
                    information.setType(bundle.getString("类型"));
                    addData(information);

                    Intent intent= new Intent("电话打入或者打出");
                    intent.putExtra("信息",bundle);
                    sendBroadcast(intent);

            }
            super.handleMessage(msg);
        }
    };

    /**
     * @param phone  电话号码
     * @return      联系人
     * 通过电话号码从数据库中查找联系人
     */
    private String  chaDataBase(String phone){
        String str=null;
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        Cursor cursor=db.query("LianXiRen",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String phone1=cursor.getString(cursor.getColumnIndex("phone"));
                if(phone1.equals(phone)){
                    str=cursor.getString(cursor.getColumnIndex("name"));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return str;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 用一个TelephonyManager对象来监听来电的事件
     */
    @Override
    public void onCreate() {
        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener=new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        myDataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"TongXunLu1");
        super.onCreate();
    }

    /**
     * @param information  代表要添加的数据
     *   往数据库里面添加数据
     */
    private void addData(Information information) {
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",information.getName());
        values.put("type",information.getType());
        values.put("phone",information.getPhone());
        values.put("address",information.getAddress());
        values.put("time",information.getTime());
        values.put("harass",information.getHarass());
        db.insert("TongHua",null,values);
    }

    class MyPhoneStateListener extends PhoneStateListener{

        /**
         * @param state     用来判断状态
         * @param incomingNumber  来电者的电话
         */
        @Override
        public void onCallStateChanged(int state,  String incomingNumber) {
            phoneNumber=incomingNumber;
            if(TelephonyManager.CALL_STATE_RINGING==state){
                new MyThread(incomingNumber,handler).start();
                super.onCallStateChanged(state, incomingNumber);
            }
        }
    }
}
