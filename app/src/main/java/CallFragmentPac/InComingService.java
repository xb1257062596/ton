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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by 123 on 2016/12/30.
 */

public class InComingService extends Service {
    private MyDataBaseHelper myDataBaseHelper;
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private String phoneNumber;

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

    @Override
    public void onCreate() {
        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener=new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private void addData(Information information) {
        myDataBaseHelper=new MyDataBaseHelper(getApplicationContext(),"TongXunLu1");
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

        @Override
        public void onCallStateChanged(int state,  String incomingNumber) {
            phoneNumber=incomingNumber;
            if(TelephonyManager.CALL_STATE_RINGING==state){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String address;
                        HttpURLConnection connection= null;
                        address = "http://op.juhe.cn/onebox/phone/query?tel="+phoneNumber+"&key=3f55145f2e85e1e7815f1ea33e774fd2";
                        try{
                            Log.d("test",phoneNumber+"1");
                            URL url= new URL(address);
                            connection= (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setReadTimeout(8000);
                            connection.setReadTimeout(8000);
                            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuffer buffer= new StringBuffer();
                            String line;
                            while((line=reader.readLine())!=null){
                                buffer.append(line);
                            }
                            String response=buffer.toString();
                            Log.d("test",response);
                            JSONObject jsonObject= new JSONObject(response);
                            JSONObject result=jsonObject.getJSONObject("result");
                            Log.d("test","是否成功1");
                            String address1=result.getString("province")+result.getString("city");
                            int iszhapian=result.getInt("iszhapian");


                            Calendar c = Calendar.getInstance();
                            int date = c.get(Calendar.DATE);
                            int hour = c.get(Calendar.HOUR_OF_DAY);
                            int minute = c.get(Calendar.MINUTE);
                            Bundle bundle=new Bundle();
                            bundle.putString("地址",address1);
                            if(iszhapian==1){
                                bundle.putString("诈骗","骚扰");
                            }else{
                                bundle.putString("诈骗","不是骚扰");
                            }
                            bundle.putString("电话",phoneNumber);
                            bundle.putString("时间",date+":"+hour+":"+minute);
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


                        }catch (Exception e){
                            Log.d("test",e.getMessage());
                        }
                    }
                }).start();
                super.onCallStateChanged(state, incomingNumber);
            }
        }
    }
}
