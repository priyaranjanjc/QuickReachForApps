package com.ms.jcproject.jcpr;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainFragment extends Fragment {

    private SharedPreferences pref;
    private PackageManager myPackageManager;
    private ListView mListView;
    private MyBaseAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_fragment_apps_view, null, false);

        myPackageManager = getActivity().getPackageManager();
        pref = getActivity().getSharedPreferences("favourites", getActivity().MODE_PRIVATE);
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> intentList = myPackageManager.queryIntentActivities(intent, 0);

        mListView = (ListView) v.findViewById(R.id.app_lv);
        mAdapter = new MyBaseAdapter(getActivity(), intentList);
        mListView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyBaseAdapter extends BaseAdapter {

        private Context myContext;
        private List<ResolveInfo> MyAppList;

        MyBaseAdapter(Context c, List<ResolveInfo> l) {
            myContext = c;
            MyAppList = l;
        }

        @Override
        public int getCount() {
            return MyAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return MyAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.package_list_view_rename, null, false);

            }
            imageView = (ImageView) convertView.findViewById(R.id.icon);
            TextView tv = (TextView) convertView.findViewById(R.id.app_name);
            Switch toggle = (Switch) convertView.findViewById(R.id.toggle);


            ResolveInfo resolveInfo = MyAppList.get(position);
            toggle.setTag(resolveInfo.activityInfo.packageName);

            imageView.setImageDrawable(resolveInfo.loadIcon(myPackageManager));
            tv.setText(resolveInfo.loadLabel(myPackageManager));

            Set<String> appList = pref.getStringSet("apps", null);
            toggle.setOnCheckedChangeListener(null);
            if (appList != null && appList.contains(resolveInfo.activityInfo.packageName)) {
                toggle.setChecked(true);
            } else
                toggle.setChecked(false);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Set<String> appList = pref.getStringSet("apps", null);
                    if (appList == null)
                        appList = new HashSet<String>();
                    if (isChecked) {
                        SharedPreferences.Editor edit = pref.edit();
                        edit.clear();
                        appList.add((String) buttonView.getTag());
                        edit.putStringSet("apps", appList);
                        edit.commit();
                    } else {
                        SharedPreferences.Editor edit = pref.edit();
                        appList.remove(buttonView.getTag());
                        edit.clear();
                        edit.putStringSet("apps", appList);
                        edit.commit();

                    }


                }
            });
            return convertView;

        }

    }

}
