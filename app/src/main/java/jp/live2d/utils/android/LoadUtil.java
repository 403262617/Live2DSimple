/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.utils.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import com.live2d.live2dsimple.LAppDefine;
import jp.live2d.util.UtDebug;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.io.IOException;
import java.io.InputStream;

public final class LoadUtil {
    private static final int GEN_TEX_LOOP = 999;

    public static int loadTexture(GL10 gl, InputStream in, boolean mipmap) {
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        int texture;

        if (mipmap) {
            texture = buildMipmap(gl, bitmap);
        } else {
            texture = genTexture(gl);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            Runtime.getRuntime().gc();
        }

        return texture;
    }

    /*
     * glGenTexturesの代わり
     *
     * Androidの一部でglGenTextures()がとびとびのおかしな値を返し、
     * 場合によって負の番号（uintであれば利用可能？）を返すため、正の値が取れるまで繰り返す
     * エラーの場合は０
     * @param gl
     * @return
     */
    private static int genTexture(GL10 gl) {
        int texture = 0;
        int i = 0;

        for (; i < GEN_TEX_LOOP; i++) { // 予期せぬ無限ループを防止
            int[] ret = {0};
            GLES20.glGenTextures(1, ret, 0);
            texture = ret[0];

            if (texture < 0) {
                GLES20.glDeleteTextures(1, ret, 0); // 作成したものを破棄する
            } else {
                break;
            }
        }
        if (i == GEN_TEX_LOOP) {
            UtDebug.error("gen texture loops over " + GEN_TEX_LOOP + "times @UtOpenGL");
            texture = 0;
        }

        return texture;
    }

    private static int buildMipmap(GL10 gl, Bitmap bitmap) {
        return buildMipmap(gl, bitmap, true);
    }

    /*
     * Mipmap texture
     */
    private static int buildMipmap(GL10 gl, Bitmap srcBitmap, boolean recycle) {
        Bitmap bitmap = srcBitmap;
        int level = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int textureID = genTexture(gl);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);

        try {
            // この一文がないと、Lynxで崩れる
            ((GL11) gl).glTexParameteri(GL10.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL10.GL_TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

        while (height >= 1 && width >= 1) {
            // ---- note ----
            // First of all, generate the texture from our bitmap and set it to the according level
            // Lynx3Dではlevel 0 以外で呼び出したときにうまくいかないことがある。
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);

            if (height == 1 || width == 1) {
                if (recycle || bitmap != srcBitmap) {
                    bitmap.recycle();
                    Runtime.getRuntime().gc();
                }
                break;
            }

            level++;

            height /= 2;
            width /= 2;

            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);

            // Clean up
            if (recycle || bitmap != srcBitmap) {
                bitmap.recycle();
                Runtime.getRuntime().gc();
            }
            bitmap = bitmap2;
        }

        return textureID;
    }

    /*
     * アセットからデータを読み込む。
     * @param applicationContext
     * @param filename
     * @return
     */
    public static MediaPlayer loadAssetsSound(Context applicationContext, String filename) {
        if (LAppDefine.DEBUG_LOG)
            Log.d("", "Load sound: " + filename);

        final MediaPlayer player = new MediaPlayer();

        try {
            final AssetFileDescriptor assetFileDescriptorArticle = FileManager.openFd(applicationContext, filename);
            player.reset();
            player.setDataSource(assetFileDescriptorArticle.getFileDescriptor(), assetFileDescriptorArticle.getStartOffset(), assetFileDescriptorArticle.getLength());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            assetFileDescriptorArticle.close();
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        return player;
    }
}