package CallFragmentPac;

import android.content.Context;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.a123.tongxunlu.R;

import java.util.List;

/**
 * Created by 123 on 2016/12/22.
 */

public class CallAdapter extends ArrayAdapter<Information> {
    public CallAdapter(Context context, int resource, List<Information> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;

        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.tab01_item,null);
            viewHolder = new ViewHolder();
            viewHolder.nameText=(TextView)convertView.findViewById(R.id.nameText);
            viewHolder.phoneText=(TextView)convertView.findViewById(R.id.phoneText);
            viewHolder.addressText=(TextView)convertView.findViewById(R.id.addressText);
            viewHolder.timeText=(TextView)convertView.findViewById(R.id.timeText);
            viewHolder.typeText=(TextView)convertView.findViewById(R.id.typeText);
            viewHolder.harassText=(TextView)convertView.findViewById(R.id.harassText);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Information information =getItem(position);
        viewHolder.nameText.setText(information.getName());
        viewHolder.phoneText.setText(information.getPhone());
        viewHolder.addressText.setText(information.getAddress());
        viewHolder.timeText.setText(information.getTime());
        viewHolder.typeText.setText(information.getType());
        viewHolder.harassText.setText(information.getHarass());
        try{
            if(!information.getHarass().equals("骚扰")){
                viewHolder.harassText.setVisibility(View.GONE);
            }else {
                viewHolder.harassText.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            viewHolder.harassText.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView nameText;
        TextView phoneText;
        TextView addressText;
        TextView timeText;
        TextView typeText;
        TextView harassText;
    }
}
