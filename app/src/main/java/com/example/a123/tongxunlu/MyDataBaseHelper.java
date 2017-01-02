package com.example.a123.tongxunlu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by 123 on 2016/12/25.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String CREATE_TABLE="create table LianXiRen(_id integer primary key autoincrement," +
            "name text ," +
            "phone text)";

    public static final String CREATE_TABLE1="create table TongHua(_id integer primary key autoincrement," +
            "name text," +
            "phone text," +
            "address text," +
            "time text," +
            "type text," +
            "harass text)";

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    public MyDataBaseHelper(Context context,String name){
        super(context,name,null,6);
        mContext=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE1);
        Toast.makeText(mContext,"数据库创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
           db.execSQL("drop table if exists LianXiRen");
        db.execSQL("drop table if exists TongHua");
        onCreate(db);
    }
}
