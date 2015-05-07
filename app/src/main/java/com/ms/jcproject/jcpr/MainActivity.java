package com.ms.jcproject.jcpr;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    private MainFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        mFragment = new MainFragment();
        fm.beginTransaction().add(R.id.main_fragment, mFragment).commit();
        startService(new Intent(this, QuickAppService.class));
    }
}
