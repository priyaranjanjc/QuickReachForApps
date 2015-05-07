package com.ms.jcproject.jcpr.Adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ms.jcproject.jcpr.R;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private final ArrayList<String> mApps;
    private Context mContext;
    private LayoutInflater inflater;
    // private List<String> mPackages;
    private PackageManager pm;

    public GridViewAdapter(Context context, ArrayList<String> apps) {
        mContext = context;
        mApps = apps;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pm = context.getPackageManager();

    }

    @Override
    public int getCount() {

        return mApps.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_view_layout, null,
                    false);
        }

        ImageView ib = (ImageView) convertView.findViewById(R.id.icon);
        ib.setTag(mApps.get(position));

        if (mApps.get(position).equalsIgnoreCase("bluetooth")) {
            ib.setImageResource(R.drawable.ic_action_bluetooth);
        } else if (mApps.get(position).equalsIgnoreCase("wifi")) {
            ib.setImageResource(R.drawable.ic_action_network_wifi);
        } else if (mApps.get(position).equalsIgnoreCase("gps")) {
            ib.setImageResource(R.drawable.ic_action_location_found);
        } else if (mApps.get(position).equalsIgnoreCase("mobileData")) {
            ib.setImageResource(R.drawable.mobile_data);
        } else {
            try {
                ib.setImageDrawable(pm.getApplicationIcon(mApps.get(position)));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

}
