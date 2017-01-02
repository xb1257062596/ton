package LianFragmentPac;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.a123.tongxunlu.R;
import com.hp.hpl.sparta.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 2016/12/22.
 */

public class LianAdapter extends ArrayAdapter<LianXiRen> implements Filterable{

    public LianAdapter(Context context, int resource, List<LianXiRen> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       LianXiRen mLianXiRen=getItem(position);
        String str=mLianXiRen.getFirstName();
        String str1=mLianXiRen.getName();
        if(str.equals("")){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.tab02_title,null);
            if(position!=getCount()-1){
                LianXiRen mLianXiRen1=getItem(position+1);
                if (mLianXiRen1.getFirstName().equals("")){
                    AbsListView.LayoutParams param = new AbsListView.LayoutParams( 0,1);
                    convertView.setLayoutParams(param);
                }
            }if(position==getCount()-1){
                AbsListView.LayoutParams param = new AbsListView.LayoutParams( 0,1);
                convertView.setLayoutParams(param);
            }

        }else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tab02_item, null);
        }
        TextView mTextView2=(TextView)convertView.findViewById(R.id.mTextView);
        mTextView2.setText(str1);
        return convertView;
    }




}
