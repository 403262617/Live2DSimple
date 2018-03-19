/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.framework;

import android.content.Context;
import jp.live2d.ALive2DModel;

public interface IPlatformManager {
    byte[] loadBytes(Context applicationContext, String path);

    String loadString(Context applicationContext, String path);

    ALive2DModel loadLive2DModel(Context applicationContext, String path);

    void loadTexture(Context applicationContext, ALive2DModel model, int no, String path);

    void log(String txt);
}