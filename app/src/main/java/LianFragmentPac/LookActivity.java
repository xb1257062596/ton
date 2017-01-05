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
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.a123.tongxunlu.MainActivity;
import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.MyThread;
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

public class LookActivity extends AppCompatActivity {
    private TextView phone_Text;
    private TextView address_Text;
    private MyDataBaseHelper myDataBaseHelper;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    String str;
    String str2;
    Bundle bundle= new Bundle();

    /**
     * 为了接收线程返回来的数据
     */
    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Bundle bundle1= (Bundle) msg.obj;
                    bundle.putString("地址",bundle1.getString("地址"));
                    bundle.putString("诈骗",bundle1.getString("诈骗"));
                    address_Text.setText(bundle1.getString("地址"));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.look);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        str = intent.getStringExtra("姓名");

        myDataBaseHelper = new MyDataBaseHelper(LookActivity.this, "TongXunLu1");

        phone_Text = (TextView) this.findViewById(R.id.phone_Text);

        address_Text=(TextView)this.findViewById(R.id.address_Text);

        collapsingToolbarLayout=(CollapsingToolbarLayout)this.findViewById(R.id.collapsingToolbarLayout);

        str2 = DataBaseLook(str);

       new MyThread(str2,handler).start();
        collapsingToolbarLayout.setTitle(str);
        phone_Text.setText(str2);


        Calendar c = Calendar.getInstance();
        int month=c.get(Calendar.MONTH)+1;
        String month1=month+"";
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
        bundle.putString("电话",str2);
        bundle.putString("时间",month1+"."+date1+" "+hour+":"+minute);
        bundle.putString("姓名",str);
        bundle.putString("类型","呼出");



        phone_Text.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


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

                if (ActivityCompat.checkSelfPermission(LookActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("test","通话不被允许");
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

    /**
     * @param item
     * @return  菜单的返回键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param str
     * @return  遍历数据库 通过姓名查找电话号码
     */
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

    /**
     * @param information 网数库中添加数据
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
}
