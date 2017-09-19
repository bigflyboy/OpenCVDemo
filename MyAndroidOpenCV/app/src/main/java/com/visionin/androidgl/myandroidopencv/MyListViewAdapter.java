package com.visionin.androidgl.myandroidopencv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MyListViewAdapter extends BaseAdapter {
    private List<CommandData> commandDataList;
    private Context context;

    public MyListViewAdapter(Context appContext) {
        this.context = appContext;
        this.commandDataList = new ArrayList<>();
    }

    public List<CommandData> getModel() {
        return this.commandDataList;
    }

    @Override
    public int getCount() {
        return commandDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return commandDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return commandDataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowitem, parent, false);
        TextView txtItem = (TextView) rowView.findViewById(R.id.row_textView);
        rowView.setTag(commandDataList.get(position));
        txtItem.setText(commandDataList.get(position).getCommand());
        return rowView;
    }
}
