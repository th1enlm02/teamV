package com.example.teamv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.teamv.R;

public class ColorPickerAdapter extends BaseAdapter {
    Context context;
    int[] colors;

    public ColorPickerAdapter(Context context, int[] colors) {
        this.context = context;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_color_item, parent, false);

            holder = new ViewHolder();
            holder.ivColorItem = convertView.findViewById(R.id.color_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the color for ImageView
        holder.ivColorItem.setBackgroundResource(colors[position]);

        return convertView;
    }

    // ViewHolder pattern for better ListView performance
    static class ViewHolder {
        ImageView ivColorItem;
    }
}
