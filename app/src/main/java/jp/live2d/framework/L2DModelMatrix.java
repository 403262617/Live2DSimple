/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.framework;

/*
 * モデルの位置指定に使うと便利な行列
 */
public final class L2DModelMatrix extends L2DMatrix44 {
    private float width; // モデルのサイズ
    private float height;

    L2DModelMatrix(float w, float h) {
        width = w;
        height = h;
    }

    public final void setPosition(float x, float y) {
        translate(x, y);
    }

    public final void setCenterPosition(float x, float y) {
        float w = width * getScaleX();
        float h = height * getScaleY();
        translate(x - w / 2, y - h / 2);
    }

    public final void top(float y) {
        setY(y);
    }

    public final void bottom(float y) {
        float h = height * getScaleY();
        translateY(y - h);
    }

    public final void left(float x) {
        setX(x);
    }

    public final void right(float x) {
        float w = width * getScaleX();
        translateX(x - w);
    }

    public final void centerX(float x) {
        float w = width * getScaleX();
        translateX(x - w / 2);
    }

    public final void centerY(float y) {
        float h = height * getScaleY();
        translateY(y - h / 2);
    }

    public final void setX(float x) {
        translateX(x);
    }

    public final void setY(float y) {
        translateY(y);
    }

    /*
     * 縦幅をもとにしたサイズ変更
     * 縦横比はもとのまま
     * @param h
     */
    public final void setHeight(float h) {
        float scaleX = h / height;
        float scaleY = -scaleX;
        scale(scaleX, scaleY);
    }

    /*
     * 横幅をもとにしたサイズ変更
     * 縦横比はもとのまま
     * @param w
     */
    public final void setWidth(float w) {
        float scaleX = w / width;
        float scaleY = -scaleX;
        scale(scaleX, scaleY);
    }
}