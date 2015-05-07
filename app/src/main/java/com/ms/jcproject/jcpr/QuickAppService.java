package com.ms.jcproject.jcpr;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ms.jcproject.jcpr.Adapters.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressLint("NewApi")
public class QuickAppService extends Service {

    private WindowManager.LayoutParams params;
    private Context mContext;
    private DisplayManager displayManager;
    private Display display;
    private PopupWindow pw;
    private Button games;
    private Button favourites;
    private Button social;
    private ActivityManager activityManager;
    private List<RecentTaskInfo> x;
    private List<ApplicationInfo> recentApps = new ArrayList<ApplicationInfo>();
    private GridViewAdapter mAdapter;
    private GridView mGridView;
    private LayoutInflater inflater;
    private PackageManager pm;
    private List<ApplicationInfo> appInfo;
    private List<ApplicationInfo> socialApps;
    private LinearLayout mCategories;
    private ArrayList<String> mPackages;
    private ImageView mView;
    private Handler mHandler;
    private Runnable mSetIconTransparent = new Runnable() {
        @Override
        public void run() {
            mView.setAlpha(0.6f);
        }
    };
    private View.OnClickListener mCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getSharedPreferences("favourites", MODE_PRIVATE);
            Set<String> appList = pref.getStringSet("apps", null);
            mPackages.clear();
            if (appList != null) {
                for (String pack : appList)
                    mPackages.add(pack);
            }
        }
    };
    private long lastPressTime;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {

        mContext = getBaseContext();

        displayManager = (DisplayManager) mContext
                .getSystemService(Context.DISPLAY_SERVICE);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        mHandler = new Handler();

        params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;

        mView = new ImageView(this);
        mView.setImageResource(R.drawable.chat_head);
        mView.setScaleType(ImageView.ScaleType.FIT_XY);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.width = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        params.height = (int) getResources().getDimension(android.R.dimen.app_icon_size);


        final WindowManager wmgr = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        wmgr.addView(mView, params);

        mView.setOnTouchListener(new View.OnTouchListener() {
            long start;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeCallbacksAndMessages(null);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long pressTime = System.currentTimeMillis();


                        // If double click...
                        if (pressTime - lastPressTime <= 300) {
                            QuickAppService.this.stopSelf();
                        }
                        lastPressTime = pressTime;
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        start = System.currentTimeMillis();
                        break;
                    //return true;
                    case MotionEvent.ACTION_UP:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        wmgr.updateViewLayout(mView, params);

                        //return true;
                        break;
                }
                return false;
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setAlpha(1f);
                constructPopupWindow();
            }
        });

        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                QuickAppService.this.stopSelf();
                return false;
            }
        });

        mHandler.postDelayed(mSetIconTransparent, 8000);

        super.onCreate();
    }


    private void constructPopupWindow() {
        mHandler.removeCallbacksAndMessages(null);
        pw = new PopupWindow(QuickAppService.this);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.main_layout, null, false);
        pw.setContentView(view);
        pw.setWidth(display.getWidth());
        pw.setHeight(LayoutParams.WRAP_CONTENT);
        pw.setFocusable(true);
        pw.setOutsideTouchable(true);
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandler.postDelayed(mSetIconTransparent, 8000);
            }
        });


        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mCategories = (LinearLayout) view.findViewById(R.id.categories);
        mPackages = new ArrayList();
        mAdapter = new GridViewAdapter(mContext, mPackages);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pack = (String) mAdapter.getItem(position);
                Intent i = pm.getLaunchIntentForPackage(pack);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (i != null) {
                    startActivity(i);
                }

                pw.dismiss();
            }
        });
        SharedPreferences pref = getSharedPreferences("favourites", MODE_PRIVATE);
        Set<String> appList = pref.getStringSet("apps", null);
        mPackages.clear();
        if (appList != null) {
            for (String pack : appList)
                mPackages.add(pack);
        }
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandler.postDelayed(mSetIconTransparent, 6000);
            }
        });
        pw.showAsDropDown(mView, 0, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        pm = getPackageManager();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
