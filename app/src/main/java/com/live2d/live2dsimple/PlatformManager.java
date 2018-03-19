/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
 */
package com.live2d.live2dsimple;

import android.content.Context;
import android.util.Log;
import jp.live2d.ALive2DModel;
import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.framework.IPlatformManager;
import jp.live2d.utils.android.FileManager;
import jp.live2d.utils.android.LoadUtil;
import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;

public final class PlatformManager implements IPlatformManager {
    private static final String TAG = "Live2D App";
    private GL10 gl;

    @Override
    public final byte[] loadBytes(@NotNull Context applicationContext, String path) {
        byte[] ret = null;
        try {
            InputStream in = FileManager.open(applicationContext, path);
            ret = new byte[in.available()];
            in.read(ret, 0, ret.length);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public final String loadString(@NotNull Context applicationContext, String path) {
        String ret = null;
        try {
            InputStream in = FileManager.open(applicationContext, path);
            ret = in.toString();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @NotNull
    @Override
    public final ALive2DModel loadLive2DModel(@NotNull Context applicationContext, String path) {
        return Live2DModelAndroid.loadModel(loadBytes(applicationContext, path));
    }

    @Override
    public final void loadTexture(@NotNull Context applicationContext, ALive2DModel model, int no, String path) {
        try {
            InputStream in = FileManager.open(applicationContext, path);
            boolean mipmap = true;
            // OpenGLの対応するテクスチャを作成。
            // テクスチャを自分で設定する場合は、glGenTexturesで作成した番号に読み込んだ画像データを設定して、Live2Dにテクスチャ番号を渡す。
            int glTexNo = LoadUtil.loadTexture(gl, in, true);
            ((Live2DModelAndroid) model).setTexture(no, glTexNo); // 対応付け
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void log(String txt) {
        Log.i(TAG, txt);
    }

    public final void setGL(GL10 gl) {
        this.gl = gl;
    }
}