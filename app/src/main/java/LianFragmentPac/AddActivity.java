package LianFragmentPac;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class AddActivity extends Activity {

    private MyDataBaseHelper myDataBaseHelper;
    private Button add_cancle;
    private Button add_ok;
    private EditText mAdd_name;
    private EditText mAdd_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add);

        initView();

        eventView();

    }

    private void initView() {
        add_cancle =(Button)this.findViewById(R.id.add_cancel);

        add_ok=(Button)this.findViewById(R.id.add_ok);

        mAdd_name=(EditText)this.findViewById(R.id.mAdd_name);

        mAdd_phone=(EditText)this.findViewById(R.id.mAdd_phone);
    }

    private void eventView(){
        myDataBaseHelper = new MyDataBaseHelper(this,"TongXunLu1");

        final SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();

        add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1=mAdd_name.getText().toString();

                String str2=mAdd_phone.getText().toString();

                ContentValues contentValues =new ContentValues();
                contentValues.put("name",str1);
                contentValues.put("phone",str2);
                db.insert("LianXiRen",null,contentValues);
                Intent  intent= new Intent(AddActivity.this, MainActivity.class);
                intent.putExtra("姓名",str1);
                startActivity(intent);
            }
        });

        add_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }


}
