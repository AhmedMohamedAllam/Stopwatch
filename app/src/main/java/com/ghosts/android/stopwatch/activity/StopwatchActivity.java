package com.ghosts.android.stopwatch.activity;

import android.support.v4.app.Fragment;

import com.ghosts.android.stopwatch.R;
import com.ghosts.android.stopwatch.fragment.StopwatchFragment;

public class StopwatchActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new StopwatchFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_stopwatch;
    }

    @Override
    protected int getLayoutId() {
        return R.id.stopwatch_container;
    }
}
