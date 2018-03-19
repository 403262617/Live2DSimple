/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.io.*;

public final class FileManager {
    public static boolean isResourceExists(Context applicationContext, String path) {
        try {
            InputStream ignored = applicationContext.getAssets().open(path);
            ignored.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static InputStream openResource(Context applicationContext, String path) throws IOException {
        return applicationContext.getAssets().open(path);
    }

    public static boolean isCacheExists(Context applicationContext, String path) {
        File file = new File(applicationContext.getCacheDir(), path);
        return file.exists();
    }

    private static FileInputStream openCache(Context applicationContext, String path) throws FileNotFoundException {
        File file = new File(applicationContext.getCacheDir(), path);
        return new FileInputStream(file);
    }

    /*
     * @param path
     * @param isCache trueならキャッシュを開く、falseならリソースを開く
     * @return
     * @throws IOException
     */
    private static InputStream open(Context applicationContext, String path, boolean isCache) throws IOException {
        if (isCache) {
            return openCache(applicationContext, path);
        } else {
            return openResource(applicationContext, path);
        }
    }

    public static InputStream open(Context applicationContext, String path) throws IOException {
        return openResource(applicationContext, path);
    }

    public static AssetFileDescriptor openFd(Context applicationContext, String path) throws IOException {
        return applicationContext.getAssets().openFd(path);
    }
}