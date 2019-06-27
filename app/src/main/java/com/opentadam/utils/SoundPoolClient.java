/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;
import java.util.HashMap;

public class SoundPoolClient {

    private SoundPool mSoundPool;
    private AssetManager mAssetManager;


    private HashMap<String, Integer> soundValue;

    public void init(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Для устройств до Android 5
            createOldSoundPool();
        } else {
            // Для новых устройств
            createNewSoundPool();
        }

        mAssetManager = context.getAssets();
        soundValue = new HashMap<>();
        soundValue.put(TipeSound.PUSH.nameFailSound
                , -1);
        soundValue.put(TipeSound.STATE_SET.nameFailSound
                , -1);
        soundValue.put(TipeSound.STATE_WAIT.nameFailSound
                , -1);

        soundValue.put(TipeSound.PUSH.nameFailSound
                , loadSound(TipeSound.PUSH.nameFailSound));

        soundValue.put(TipeSound.STATE_SET.nameFailSound
                , loadSound(TipeSound.STATE_SET.nameFailSound));

        soundValue.put(TipeSound.STATE_WAIT.nameFailSound
                , loadSound(TipeSound.STATE_WAIT.nameFailSound));

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        mSoundPool = new SoundPool(10, AudioManager.STREAM_ALARM, 0);
    }

    private int loadSound(String fileName) {

        AssetFileDescriptor afd;
        try {
            afd = mAssetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

    public void playSoundWav(String nameSoundFile) {

        boolean b = mSoundPool != null
                && soundValue.containsKey(nameSoundFile)
                && soundValue.get(nameSoundFile) != -1;

        if (b)
            getStreamIdSound(soundValue.get(nameSoundFile));

    }

    private void getStreamIdSound(int sound) {
        if (sound > 0) {
            mSoundPool.play(sound, 1, 1, 1, 0, 1);
        }
    }

    public void deactivatePool() {
        if (mSoundPool != null)
            mSoundPool.release();
        mSoundPool = null;
    }
}
