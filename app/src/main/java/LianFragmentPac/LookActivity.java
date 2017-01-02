package LianFragmentPac;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a123.tongxunlu.MainActivity;
import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import CallFragmentPac.Information;

/**
 * Created by 123 on 2016/12/25.
 */

public class LookActivity extends Activity {
    private TextView name_Text;
    private TextView phone_Text;
    private Button backBtn;
    private MyDataBaseHelper myDataBaseHelper;
    String str;
    String str2;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look);
        Intent intent = getIntent();
        str = intent.getStringExtra("姓名");

        myDataBaseHelper = new MyDataBaseHelper(LookActivity.this, "TongXunLu1");

        name_Text = (TextView) this.findViewById(R.id.name_Text);

        phone_Text = (TextView) this.findViewById(R.id.phone_Text);

        backBtn = (Button) this.findViewById(R.id.backBtn);

        str2 = DataBaseLook(str);
        name_Text.setText(str);
        phone_Text.setText(str2);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(LookActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        phone_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle = new Bundle();
                chaShu();

                if (ActivityCompat.checkSelfPermission(LookActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LookActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str2));
                startActivity(intent2);
            }
        });
    }


    private String DataBaseLook(String str){
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        String str2=null;
        Cursor cursor=db.query("LianXiRen",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                if(name.equals(str)){
                    str2=cursor.getString(cursor.getColumnIndex("phone"));
                }
            }while (cursor.moveToNext());
        }
        return str2;
    }

    private void addData(Information information) {
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        Log.d("test","okok");
        ContentValues values=new ContentValues();
        values.put("name",information.getName());
        values.put("type",information.getType());
        values.put("phone",information.getPhone());
        values.put("address",information.getAddress());
        values.put("time",information.getTime());
        values.put("harass",information.getHarass());
        db.insert("TongHua",null,values);
    }

    private void chaShu(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address;
                HttpURLConnection connection= null;
                address = "http://op.juhe.cn/onebox/phone/query?tel="+str2+"&key=3f55145f2e85e1e7815f1ea33e774fd2";
                try{
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
                    JSONObject jsonObject= new JSONObject(response);
                    JSONObject result=jsonObject.getJSONObject("result");
                    String address1=result.getString("province")+result.getString("city");
                    int iszhapian=result.getInt("iszhapian");


                    Calendar c = Calendar.getInstance();
                    int date = c.get(Calendar.DATE);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    bundle.putString("地址",address1);
                    if(iszhapian==1){
                        bundle.putString("诈骗","骚扰");
                    }else{
                        bundle.putString("诈骗","不是骚扰");
                    }
                    bundle.putString("电话",str2);
                    Log.d("test","1"+bundle.getString("电话"));
                    bundle.putString("时间",date+":"+hour+":"+minute);
                    bundle.putString("姓名",str);
                    bundle.putString("类型","呼出");

                    Information information = new Information();
                    information.setAddress(bundle.getString("地址"));
                    information.setName(bundle.getString("姓名"));
                    information.setPhone(bundle.getString("电话"));
                    information.setHarass(bundle.getString("诈骗"));
                    information.setTime(bundle.getString("时间"));
                    information.setType(bundle.getString("类型"));
                    addData(information);

                    Intent intent1 = new Intent("电话打入或者打出");
                    intent1.putExtra("信息", bundle);
                    sendBroadcast(intent1);
                }catch (Exception e){
                    Log.d("test",e.getMessage());
                }
            }
        }).start();
    }
}
