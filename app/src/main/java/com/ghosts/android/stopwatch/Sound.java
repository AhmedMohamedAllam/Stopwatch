package com.ghosts.android.stopwatch;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by Allam on 30/03/2016.
 */
public class Sound {
    private SoundPool mSoundPool;
    private Integer soundId;
    private AssetManager mAssetManager;

    public Sound(Context context) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mAssetManager = context.getAssets();
        load();
    }

    public void load() {
        try {
            AssetFileDescriptor asf = mAssetManager.openFd("sounds/" + "scream.wav");
            soundId = mSoundPool.load(asf, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void unload(){
        mSoundPool.release();
    }
}
