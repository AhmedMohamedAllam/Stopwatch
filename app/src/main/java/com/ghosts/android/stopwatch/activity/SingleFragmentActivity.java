package com.ghosts.android.stopwatch.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ahmed Allam on 2/5/2016.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();
    protected abstract int getLayout();
    protected abstract int getLayoutId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(getLayoutId());
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(getLayoutId(), fragment).commit();
        }

    }
}
