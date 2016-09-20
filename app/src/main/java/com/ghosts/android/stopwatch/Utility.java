package com.ghosts.android.stopwatch;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.EditText;

/**
 * Created by Allam on 16/09/2016.
 */
public class Utility {

    private  Context mContext;

    public Utility(Context context) {
        mContext = context;
    }

    public  long getRepeativeTime(){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getLong("repeat_time" , 0);
    }

    public  void setRepeativeTime(long time){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putLong("repeat_time", time).apply();
    }

    public  String doubleZero(int x) {
        if (x < 10 && x >= 0) {
            return ("0" + x);
        }
        return x + "";
    }

    public  boolean isNull(EditText editText) {
        if (editText.getText().length() == 0) {
            return true;
        } else {
            return false;
        }
    }


}
