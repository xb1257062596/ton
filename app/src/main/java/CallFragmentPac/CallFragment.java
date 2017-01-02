package CallFragmentPac;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import LianFragmentPac.LookActivity;

/**
 * Created by 123 on 2016/12/22.
 */

public class CallFragment extends Fragment {
    MyBroadcastReceiver myBroadcastReceiver;
    private MyDataBaseHelper myDataBaseHelper;
    SQLiteDatabase db;
    private View view;
    private ListView mListView;
    private CallAdapter callAdapter;
    private List<Information> mDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.tab01,container,false);
          myBroadcastReceiver= new MyBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("电话打入或者打出");
        getActivity().registerReceiver(myBroadcastReceiver,filter);

        return  view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
       initView();

        eventView();

        super.onActivityCreated(savedInstanceState);
    }

    private void eventView() {

        mListView.setAdapter(callAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Information  information = mDatas.get(i);
                addData(information);
                mDatas.add(0,information);
                callAdapter.notifyDataSetChanged();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    Log.d("test","没有申请权限");
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + information.getPhone()));
                startActivity(intent2);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Information information= mDatas.get(i);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("删除通话记录");
                builder.setMessage("你确定要删除"+information.getName()+"?");
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                         dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatas.remove(information);
                        db.delete("TongHua","name=?",new String[]{information.getName()});
                        callAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

    }

    private void initView() {

        myDataBaseHelper= new MyDataBaseHelper(getContext(),"TongXunLu1");

        db=myDataBaseHelper.getWritableDatabase();

        mListView =(ListView)view.findViewById(R.id.callListView);

        mDatas = new ArrayList<>();


        chaDataBase();


        callAdapter = new CallAdapter(getContext(),R.layout.tab01_item,mDatas);


    }
    public  class  MyBroadcastReceiver extends BroadcastReceiver{;

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getBundleExtra("信息");
            Information information=new Information();
            information.setName(bundle.getString("姓名"));
            information.setType(bundle.getString("类型"));
            information.setTime(bundle.getString("时间"));
            information.setPhone(bundle.getString("电话"));
            information.setHarass(bundle.getString("诈骗"));
            information.setAddress(bundle.getString("地址"));
            mDatas.add(0,information);
            callAdapter.notifyDataSetChanged();
        }
    }

    public void chaDataBase(){
        Cursor cursor=db.query("TongHua",null,null,null,null,null,null,null);
        Information information=new Information();
        if(cursor.moveToFirst()){
            do{
                information.setName(cursor.getString(cursor.getColumnIndex("name")));
                information.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                information.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                information.setTime(cursor.getString(cursor.getColumnIndex("time")));
                information.setType(cursor.getString(cursor.getColumnIndex("type")));
                information.setHarass(cursor.getString(cursor.getColumnIndex("harass")));
                mDatas.add(information);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addData(Information information) {
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
