package LianFragmentPac;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.a123.tongxunlu.MainActivity;
import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.R;

/**
 * Created by 123 on 2016/12/26.
 */

public class UpdateActivity extends Activity {

    private MyDataBaseHelper myDataBaseHelper;
    private Button update_cancel;
    private Button update_ok;
    private EditText update_name;
    private EditText update_phone;
    String name;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update);

        Intent intent= getIntent();
        name=intent.getStringExtra("姓名");
        position=intent.getIntExtra("位置",-1);
        String str2=null;
        initView();
        update_name.setText(name);

        str2=DataBaseLook(name);
        update_phone.setText(str2);

        eventView();
    }

    private void eventView() {

        update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1= new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        update_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1=update_name.getText().toString();
                String str2=update_phone.getText().toString();
                DataBaseUpdate(str1,str2);
                Intent intent= new Intent(UpdateActivity.this,MainActivity.class);
                intent.putExtra("姓名",str1);
                intent.putExtra("位置",position);
                startActivity(intent);
            }
        });

    }

    /**
     * 初始化空间
     */
    private void initView() {
        update_cancel=(Button)this.findViewById(R.id.update_cancel);
        update_ok=(Button)this.findViewById(R.id.update_ok);
        update_name=(EditText)this.findViewById(R.id.update_name);
        update_phone=(EditText)this.findViewById(R.id.update_phone);
        myDataBaseHelper=new MyDataBaseHelper(UpdateActivity.this,"TongXunLu1");

    }

    /**
     * @param str
     * @return    通过姓名查找电话号码
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
     * @param str1
     * @param str2
     * 更新数据库
     */
    private void DataBaseUpdate(String str1,String str2){
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",str1);
        contentValues.put("phone",str2);
        db.update("LianXiRen",contentValues,"name=?",new String[]{name});
    }
}
