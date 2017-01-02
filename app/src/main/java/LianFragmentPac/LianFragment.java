package LianFragmentPac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.a123.tongxunlu.MainActivity;
import com.example.a123.tongxunlu.MyDataBaseHelper;
import com.example.a123.tongxunlu.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.firstDayOfWeek;
import static android.R.attr.format;
import static android.R.attr.name;
import static android.R.attr.start;
import static android.R.attr.wallpaperCloseEnterAnimation;

/**
 * Created by 123 on 2016/12/22.
 */

public class LianFragment extends Fragment {
    private EditText searchView;
    private List<LianXiRen> newList= new ArrayList<>();
    private ChuanshuListener chuanshuListener;
    private FloatingActionButton mAddBtn; //需要修改
    private MyDataBaseHelper myDataBaseHelper;
    private View view;
    private ListView mListView;
    private LianAdapter mLianAdapter;
    private ArrayList<LianXiRen> mDatas;
    private StringBuffer stringBuffer=null;
    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.tab02,container,false);
        initView();
        setData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        eventView();
        if(chuanshuListener.chuanshu()!=null&&chuanshuListener.flag()==-1){

            addData(chuanshuListener.chuanshu());
        }else if(chuanshuListener.chuanshu()!=null&&chuanshuListener.flag()!=-1){
            updateData(chuanshuListener.chuanshu(),chuanshuListener.flag());
        }

        super.onActivityCreated(savedInstanceState);
    }

    private void updateData(String str,int str1) {

        LianXiRen lianXiRen= (LianXiRen) mLianAdapter.getItem(str1);
        String firstName=lianXiRen.getFirstName();
        stringBuffer.replace(str1-1,str1+1,firstName);
        mDatas.remove(str1);
        addData(str);
    }

    public interface ChuanshuListener{
           public String chuanshu();
           public int flag();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            chuanshuListener=(ChuanshuListener)context;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  void addData(String str) {

       LianXiRen mLianXiRen=transform(str);
        int position = stringBuffer.indexOf(mLianXiRen.getFirstName());
        stringBuffer.insert(position+1,mLianXiRen.getFirstName());
        mDatas.add(position+1,mLianXiRen);
        mLianAdapter.notifyDataSetChanged();
    }

    public LianXiRen transform(String str){
        String firstName = null;
        LianXiRen lianxiren = new LianXiRen();
        char str1=str.charAt(0);
        boolean flag=str1 >= 0x4E00 &&  str1 <= 0x9FA5;
        if(flag){
            String[] pinyinArray = null;
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(str1, format);
                firstName=""+pinyinArray[0].charAt(0);
                lianxiren.setFirstName(firstName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            firstName=str1+"";
        }
        lianxiren.setName(str);
        lianxiren.setFirstName(firstName);
       return lianxiren;
    }

    private void initView() {
        mAddBtn = (FloatingActionButton)view.findViewById(R.id.mAddBtn);//需要修改

        mListView=(ListView)view.findViewById(R.id.lianListView);

         stringBuffer = new StringBuffer();

        mDatas = new ArrayList<>();

        searchView=(EditText)view.findViewById(R.id.searchView);
        mListView.setTextFilterEnabled(true);

        myDataBaseHelper = new MyDataBaseHelper(getContext(),"TongXunLu1");



        mLianAdapter = new LianAdapter(getContext(),R.layout.tab02_item,mDatas);

        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String a="";
       for(int i='A';i<='Z';i++){
           a=""+(char)i;
           LianXiRen str = new LianXiRen("",a);
          mDatas.add(str);
           stringBuffer.append(a);
       }
    }

    private List<LianXiRen> getNewData(String input_info){
        List<LianXiRen> mNewDatas= new ArrayList<>();
        for(LianXiRen lianXiRen:mDatas){
            String str=lianXiRen.getName();
            if(str.contains(input_info)&&!lianXiRen.getFirstName().equals("")){
                LianXiRen mLianXiRen=transform(str);
                mNewDatas.add(mLianXiRen);
            }
        }
        return mNewDatas;
    }

    private void eventView(){
        mListView.setAdapter(mLianAdapter);


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!searchView.getText().toString().equals("")){
                    String str=searchView.getText().toString();
                    newList=getNewData(str);
                    mLianAdapter= new LianAdapter(getContext(),R.layout.tab02,newList);
                    mListView.setAdapter(mLianAdapter);
                }else {
                    newList.clear();
                    mLianAdapter= new LianAdapter(getContext(),R.layout.tab02,mDatas);
                    mListView.setAdapter(mLianAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {         //当点击某个联系人的点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(),LookActivity.class);
                if(newList.size()>0){
                    intent.putExtra("姓名",newList.get(i).getName());
                }else {
                    intent.putExtra("姓名",mDatas.get(i).getName());
                }
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                final LianXiRen mLianXiRen;
                if(newList.size()>0){
                    mLianXiRen=newList.get(position);
                }else{
                    mLianXiRen=mDatas.get(position);
                }

                final Dialog dialog = new Dialog(getContext(),R.style.DialogTheme);
                View view1=LayoutInflater.from(getContext()).inflate(R.layout.dialog,null);
                dialog.setContentView(view1);
                TextView dialog_name=(TextView)view1.findViewById(R.id.dialog_name);
                dialog_name.setText(mLianXiRen.getName());
                TextView dialog_look=(TextView)view1.findViewById(R.id.dialog_look);
                TextView dialog_update=(TextView)view1.findViewById(R.id.dialog_update);
                TextView dialog_delete=(TextView)view1.findViewById(R.id.dialog_delete);
                dialog_look.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                              Intent intent = new Intent(getContext(),LookActivity.class);
                              intent.putExtra("姓名",mLianXiRen.getName());
                              dialog.dismiss();
                              startActivity(intent);
                    }
                });

                dialog_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),UpdateActivity.class);
                        intent.putExtra("姓名",mLianXiRen.getName());
                        intent.putExtra("位置",position);
                        dialog.dismiss();
                        startActivity(intent);
                    }
                });

                dialog_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                        builder.setTitle("删除联系人");
                       builder.setMessage("你确定要删除"+mLianXiRen.getName()+"?");
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataBaseDelete(mLianXiRen.getName());
                                mDatas.remove(mLianXiRen);
                                mLianAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.dismiss();
                        builder.create().show();
                    }
                });

                Window window=dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams params=window.getAttributes();
                params.width=1000;
                params.height=500;
                window.setAttributes(params);
                dialog.show();


                return true;
            }
        });

        mAddBtn.setOnClickListener(new View.OnClickListener() {  //需要修改
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddActivity.class);
                startActivity(intent);
            }
        });


    }


    private void DatBaseLook() {
         SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        Cursor cursor= db.query("LianXiRen",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                addData(name);
                Log.d("test",name);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void DataBaseDelete(String str){
        SQLiteDatabase db=myDataBaseHelper.getWritableDatabase();
        db.delete("LianXiRen","name=?",new String[]{str});
    }


    private void setData() {

       DatBaseLook();
    }

}
