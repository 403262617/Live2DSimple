/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.utils.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SoundManager {
    private static final int maxStreams = 1;
    private static final AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setLegacyStreamType(AudioManager.STREAM_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();
    private static SoundPool soundPool;
    private static Map<String, Integer> soundList;

    public static void init() {
        soundPool = new SoundPool.Builder().setMaxStreams(maxStreams).setAudioAttributes(audioAttributes).build();
        soundList = new HashMap<>();
    }

    public static void load(Context applicationContext, String path) {
        if (soundList.containsKey(path))
            return;

        try {
            AssetFileDescriptor assetFileDescriptorArticle = applicationContext.getAssets().openFd(path);
            int soundID = soundPool.load(assetFileDescriptorArticle, 1);
            soundList.put(path, soundID);
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    public static void play(String name) {
        if (!soundList.containsKey(name))
            return;
        soundPool.play(soundList.get(name), 1f, 1f, 1, 0, 1);
    }

    public static void release() {
        soundList.clear();
        soundPool.release();
    }
}