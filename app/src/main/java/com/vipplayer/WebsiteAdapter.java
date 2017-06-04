package com.vipplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jack on 2017/5/28 16:41.
 * Copyright 2017 Jack
 */

public class WebsiteAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mItems;

    public WebsiteAdapter(Context context, List<String> items) {
        mContext = context.getApplicationContext();
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String str = mItems.get(position);

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_website, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mNameTv = (TextView) view.findViewById(R.id.website_name_tv_item_website);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mNameTv.setText(str.split(MyConstants.SEPARATOR)[0]);
        return view;
    }

    private static class ViewHolder {
        TextView mNameTv;
    }
}
