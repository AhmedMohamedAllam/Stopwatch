package com.ghosts.android.stopwatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ghosts.android.stopwatch.AlertService;
import com.ghosts.android.stopwatch.R;
import com.ghosts.android.stopwatch.Sound;
import com.ghosts.android.stopwatch.Utility;

/**
 * Created by Allam on 16/09/2016.
 */
public class StopwatchFragment extends Fragment {
    private TextView mTimerText, mSoundText, mFractionText;
    private EditText mEditHours, mEditMins, mEditSecs;
    private long startTime, elapsedTime, repeatedTime, startRepeatTime, elapsedRepeatTime;
    private int alertHours, alertMins, alertSecs, fraction;
    private long secs, mins, hours;
    private String mSeconds, mMinutes, mHours, mFraction;
    private boolean isStopped, isStarted, isAlert;
    private Handler mHandler = new Handler();
    public static Sound mSound;
    private Button mAlertButton, mStartButton, mResetButton;
    private Utility mUtility ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        mTimerText = (TextView) view.findViewById(R.id.timer_text);
        mSoundText = (TextView) view.findViewById(R.id.alert_text);
        mFractionText = (TextView) view.findViewById(R.id.fraction_text);

        mStartButton = (Button) view.findViewById(R.id.start);
        mResetButton = (Button) view.findViewById(R.id.reset);
        mAlertButton = (Button) view.findViewById(R.id.alert);

        mEditHours = (EditText) view.findViewById(R.id.hours);
        mEditMins = (EditText) view.findViewById(R.id.minutes);
        mEditSecs = (EditText) view.findViewById(R.id.seconds);

        mSound = new Sound(getActivity());

        mUtility = new Utility(getActivity().getApplicationContext());

            mAlertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mUtility.isNull(mEditHours))
                        alertHours = Integer.parseInt(mEditHours.getText().toString());
                    else
                        alertHours = 0;

                    if (!mUtility.isNull(mEditMins))
                        alertMins = Integer.parseInt(mEditMins.getText().toString());
                    else
                        alertMins = 0;

                    if (!mUtility.isNull(mEditSecs))
                        alertSecs = Integer.parseInt(mEditSecs.getText().toString());
                    else
                        alertSecs = 0;

                    if (!isAlert) {
                        if (alertHours == 0 && alertMins == 0 && alertSecs == 0) {
                            Toast.makeText(getActivity(), "Choose at least 1 Second!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Alert after : "
                                            + mUtility.doubleZero(alertHours) + " : "
                                            + mUtility.doubleZero(alertMins) + " : "
                                            + mUtility.doubleZero(alertSecs),
                                    Toast.LENGTH_SHORT).show();

                            repeatedTime = alertHours * 60 * 60 * 1000 + alertMins * 60 * 1000 + alertSecs * 1000;


                            mUtility.setRepeativeTime(repeatedTime);

                            Toast.makeText(getActivity(), " Repeative time " +
                                    new Utility(getActivity()).getRepeativeTime(), Toast.LENGTH_SHORT).show();

                            AlertService.setServiceOn(getActivity(), true);
                            mHandler.postDelayed(repeatSound, repeatedTime);
                            mSoundText.setVisibility(View.VISIBLE);
                            mSoundText.setText("Alerts each " + mUtility.doubleZero(alertHours) + " : " + mUtility.doubleZero(alertMins) + " : " + mUtility.doubleZero(alertSecs));
                            mEditHours.setText(null);
                            mEditMins.setText(null);
                            mEditSecs.setText(null);
                            isAlert = true;
                            startRepeatTime = System.currentTimeMillis();
                            mAlertButton.setText(R.string.stop_alert);
                            runTimerText();

                        }
                    } else {
                        mAlertButton.setText(R.string.alert);
                        mSoundText.setText(getString(R.string.sound_text));
                        mHandler.removeCallbacks(timerRunnable);
                        mHandler.removeCallbacks(repeatSound);
                        isAlert = false;
                        mUtility.setRepeativeTime(0);
                    }

                }
            });

            mStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isStarted) {
                        runTimerText();
                        mStartButton.setText(R.string.stop);
                        isStarted = true;
                    } else {
                        stopTimerText();
                        mStartButton.setText(R.string.start);
                        isStarted = false;
                    }

                }
            });

            mResetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isStopped = false;
                    isStarted = false;
                    isAlert = false;
                    mHandler.removeCallbacks(timerRunnable);
                    mTimerText.setText("00 : 00 : 00");
                    mFractionText.setText(".00");
                    elapsedTime = 0;
                    mHandler.removeCallbacksAndMessages(repeatSound);
                    mSoundText.setText(getString(R.string.sound_text));
                    mAlertButton.setText(R.string.alert);
                    mStartButton.setText(R.string.start);
                    mUtility.setRepeativeTime(0);
                    unLoadSound();
                }
            });

        return view;
    }

    public static void unLoadSound(){
        mSound.unload();
    }

    private void runTimerText() {
        if (isStopped) {
            startTime = System.currentTimeMillis() - elapsedTime;
        } else {
            startTime = System.currentTimeMillis();
        }
        mHandler.removeCallbacks(timerRunnable);
        mHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimerText() {
        isStopped = true;
        mHandler.removeCallbacks(timerRunnable);
    }

    private void updateTimer(float time) {
        secs = (long) time / 1000;
        mins = (long) (time / 1000) / 60;
        hours = (long) ((time / 1000) / 60) / 60;
        fraction = (int) (time/10)%100;
        mSeconds = mUtility.doubleZero((int) secs % 60);
        mMinutes = mUtility.doubleZero((int) mins % 60);
        mHours = mUtility.doubleZero((int) hours % 60);
        mFraction = mUtility.doubleZero(fraction);

        mTimerText.setText(mHours + " : " + mMinutes + " : " + mSeconds);
        mFractionText.setText("." + mFraction);

    }

    public static void playSound(){
        mSound.play();
    }


    private Runnable repeatSound = new Runnable() {
        @Override
        public void run() {
            elapsedRepeatTime = System.currentTimeMillis() - startRepeatTime;
            playSound();
            mHandler.postDelayed(this, repeatedTime);
        }
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, 10);
        }
    };

    @Override
    public void onDestroy() {
        unLoadSound();
        super.onDestroy();

    }

}
