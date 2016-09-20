package com.ghosts.android.stopwatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StopwatchActivity extends AppCompatActivity {

    private TextView mTimerText, mSoundText , mFractionText;
    private EditText mEditHours, mEditMins, mEditSecs;
    private int alertHours, alertMins, alertSecs , fraction;
    private long secs, mins, hours;
    private String mSeconds, mMinutes, mHours , mFraction;
    private long startTime, elapsedTime, repeatedTime, startRepeatTime, elapsedRepeatTime;
    private boolean isStopped, isStarted, isAlert;
    private Handler mHandler = new Handler();
    public static Sound mSound;
    private Button mAlertButton, mStartButton, mResetButton;

    @Override
    protected void onStart() {
        super.onStart();
        AlertService.setServiceOn(getBaseContext(),false);
        setRepeativeTime(getBaseContext(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);


        AlertService.setServiceOn(getApplicationContext(),true);

        mTimerText = (TextView) findViewById(R.id.timer_text);
        mSoundText = (TextView) findViewById(R.id.alert_text);
        mFractionText = (TextView) findViewById(R.id.fraction_text);

        mStartButton = (Button) findViewById(R.id.start);
        mResetButton = (Button) findViewById(R.id.reset);
        mAlertButton = (Button) findViewById(R.id.alert);

        mEditHours = (EditText) findViewById(R.id.hours);
        mEditMins = (EditText) findViewById(R.id.minutes);
        mEditSecs = (EditText) findViewById(R.id.seconds);

        mSound = new Sound(getBaseContext());

        if (savedInstanceState != null) {
            elapsedTime = savedInstanceState.getLong("elapsedTime");
            isStarted = savedInstanceState.getBoolean("isStarted");
            isStopped = savedInstanceState.getBoolean("isStopped");
            isAlert = savedInstanceState.getBoolean("isAlert");
            elapsedRepeatTime = savedInstanceState.getLong("elapsedRepeatTime");
            repeatedTime = savedInstanceState.getLong("repeatedTime");
            alertHours = savedInstanceState.getInt("alertHours");
            alertMins = savedInstanceState.getInt("alertMins");
            alertSecs = savedInstanceState.getInt("alertSecs");
            mFraction = savedInstanceState.getString("mFraction");

            if (isStarted) {
                mStartButton.setText(R.string.stop);
            } else {
                mStartButton.setText(R.string.start);
            }

            startTime = System.currentTimeMillis() - elapsedTime;
            if (!isStopped && isStarted) {
                mHandler.postDelayed(mRunnable, 0);
            } else if (!isStarted && isStopped) {
                updateTimer(elapsedTime);
            }

            if (isAlert) {

                elapsedTime = elapsedRepeatTime;
                mHandler.postDelayed(mRunnable, 0);
                mAlertButton.setText(R.string.stop_alert);
                mSoundText.setVisibility(View.VISIBLE);
                mSoundText.setText("Alerts each "
                        + doubleZero(alertHours) + " : " + doubleZero(alertMins) + " : " + doubleZero(alertSecs));

                mHandler.postDelayed(repeatSound, (repeatedTime - elapsedRepeatTime));

            }

        }


        mAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNull(mEditHours))
                    alertHours = Integer.parseInt(mEditHours.getText().toString());
                else
                    alertHours = 0;
                if (!isNull(mEditMins))
                    alertMins = Integer.parseInt(mEditMins.getText().toString());
                else
                    alertMins = 0;
                if (!isNull(mEditSecs))
                    alertSecs = Integer.parseInt(mEditSecs.getText().toString());
                else
                    alertSecs = 0;
                if (!isAlert) {
                    if (alertHours == 0 && alertMins == 0 && alertSecs == 0) {
                        Toast.makeText(StopwatchActivity.this, "Choose at least 1 Second!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StopwatchActivity.this, "Alert after : "
                                        + doubleZero(alertHours) + " : "
                                        + doubleZero(alertMins) + " : "
                                        + doubleZero(alertSecs),
                                Toast.LENGTH_SHORT).show();

                        repeatedTime = alertHours * 60 * 60 * 1000 + alertMins * 60 * 1000 + alertSecs * 1000;
                        //set Shared preference repeated time
                        setRepeativeTime(getBaseContext(), repeatedTime);

                        mHandler.postDelayed(repeatSound, repeatedTime);
                        mSoundText.setVisibility(View.VISIBLE);
                        mSoundText.setText("Alerts each "
                                + doubleZero(alertHours) + " : " + doubleZero(alertMins) + " : " + doubleZero(alertSecs));
                        mEditHours.setText(null);
                        mEditMins.setText(null);
                        mEditSecs.setText(null);
                        isAlert = true;
                        startRepeatTime = System.currentTimeMillis();
                        mAlertButton.setText(R.string.stop_alert);
                        Running();
                    }
                } else {
                    AlertService.setServiceOn(getBaseContext(), false);
                    mAlertButton.setText(R.string.alert);
                    mSoundText.setVisibility(View.INVISIBLE);
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.removeCallbacks(repeatSound);
                    isAlert = false;
                }


            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isStarted) {
                    Running();
                    mStartButton.setText(R.string.stop);
                    isStarted = true;
                } else {
                    Stopped();
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
                mHandler.removeCallbacks(mRunnable);
                mTimerText.setText("00 : 00 : 00");
                mFractionText.setText(".00");
                elapsedTime = 0;
                mHandler.removeCallbacksAndMessages(repeatSound);
                mSoundText.setVisibility(View.INVISIBLE);
                mAlertButton.setText(R.string.alert);
                mStartButton.setText(R.string.start);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unLoadSound();
        AlertService.setServiceOn(getBaseContext() , true);
    }

    public static void unLoadSound(){
        mSound.unload();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("elapsedTime", elapsedTime);
        outState.putLong("elapsedRepeatTime", System.currentTimeMillis() - startRepeatTime);
        outState.putLong("repeatedTime", repeatedTime);

        outState.putInt("alertHours", alertHours);
        outState.putInt("alertMins", alertMins);
        outState.putInt("alertSecs", alertSecs);
        outState.putString("mFraction" , mFraction);

        outState.putBoolean("isStarted", isStarted);
        outState.putBoolean("isStopped", isStopped);
        outState.putBoolean("isAlert", isAlert);
    }

    private boolean isNull(EditText editText) {
        if (editText.getText().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    private String doubleZero(int x) {
        if (x < 10 && x >= 0) {
            return ("0" + x);
        }
        return x + "";
    }

    private void Running() {
        if (isStopped) {
            startTime = System.currentTimeMillis() - elapsedTime;
        } else {
            startTime = System.currentTimeMillis();
        }

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 0);
    }

    private void Stopped() {
        isStopped = true;
        mHandler.removeCallbacks(mRunnable);
    }

    private void updateTimer(float time) {
        secs = (long) time / 1000;
        mins = (long) (time / 1000) / 60;
        hours = (long) ((time / 1000) / 60) / 60;
        fraction = (int) (time/10)%100;


        mSeconds = doubleZero((int) secs % 60);
        mMinutes = doubleZero((int) mins % 60);
        mHours = doubleZero((int) hours % 60);
        mFraction = doubleZero(fraction);

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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, 10);
        }
    };

    //----------------------******-------------------------------
    public static long getRepeativeTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong("repeat_time" , 0);
    }

    public static void setRepeativeTime(Context context ,long time){
       PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("repeat_time", time).apply();
    }










}
