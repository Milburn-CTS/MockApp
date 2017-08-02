package com.continentaltechsolutions.dell.mockapp.Business;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.continentaltechsolutions.dell.mockapp.R;

import java.util.List;

/**
 * Created by DELL on 02-Aug-17.
 */

public class sosmsgAdapter extends ArrayAdapter<sosmsg> {
    private Context mContext;

    public sosmsgAdapter(Context context,int resource, List<sosmsg> data ) {
        super(context, resource, data);
        mContext=context;
    }
    public View getView(int position, View convertView, ViewGroup parent ) {
        ViewHolder holder = null;
        sosmsg rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            convertView=mInflater.inflate(R.layout.sosview_row,null);
            holder=new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.textView1);
            holder.txtNumber = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtName.setText("Name: " + rowItem.getName());
        holder.txtNumber.setText("Number:"+ rowItem.getNum());

        return convertView;
    }

    private class ViewHolder {
        TextView txtName;
        TextView txtNumber;
    }
}
