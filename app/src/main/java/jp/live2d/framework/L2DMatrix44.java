/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.framework;

public class L2DMatrix44 {
    float tr[] = new float[16]; // 行列データ

    public L2DMatrix44() {
        identity();
    }

    /*
     * 受け取った2つの行列の掛け算を行う。
     * @param a 入力1
     * @param b 入力2
     * @param dst 出力
     */
    final void mul(float[] a, float[] b, float[] dst) {
        float c[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int n = 4;
        int i, j, k;

        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                for (k = 0; k < n; k++) {
                    c[i + j * 4] += a[i + k * 4] * b[k + j * 4];
                }
            }
        }

        for (i = 0; i < 16; i++) {
            dst[i] = c[i];
        }
    }

    public final void identity() {
        for (int i = 0; i < 16; i++)
            tr[i] = ((i % 5) == 0) ? 1 : 0;
    }

    /*
     * 行列配列を取得
     * @return
     */
    public final float[] getArray() {
        return tr;
    }

    /*
     * 行列配列のコピーを取得
     * @return
     */
    public final float[] getCopyMatrix() {
        return tr.clone(); // 配列のコピーを返す
    }

    /*
     * 行列を設定
     * 長さ16の配列でないときは何もしない
     * @param tr
     */
    public final void setMatrix(float tr[]) {
        // 配列の長さが異なる場合は何もしない
        if (tr == null || this.tr.length != tr.length) return;
        System.arraycopy(tr, 0, this.tr, 0, 16);
    }

    public final float getScaleX() {
        return tr[0];
    }

    public final float getScaleY() {
        return tr[5];
    }

    /*
     * xの値を現在の行列で計算する。
     * @param src
     * @return
     */
    public final float transformX(float src) {
        return tr[0] * src + tr[12];
    }

    /*
     * yの値を現在の行列で計算する。
     * @param src
     * @return
     */
    public final float transformY(float src) {
        return tr[5] * src + tr[13];
    }

    /*
     * xの値を現在の行列で逆計算する。
     * @param src
     * @return
     */
    public final float invertTransformX(float src) {
        return (src - tr[12]) / tr[0];
    }

    /*
     * yの値を現在の行列で逆計算する。
     * @param src
     * @return
     */
    public final float invertTransformY(float src) {
        return (src - tr[13]) / tr[5];
    }

    /*
     * 移動量の計算。
     * @param shiftX
     * @param shiftY
     */
    public final void multTranslate(float shiftX, float shiftY) {
        float tr1[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, shiftX, shiftY, 0, 1};
        mul(tr1, tr, tr);
    }

    final void translate(float x, float y) {
        tr[12] = x;
        tr[13] = y;
    }

    final void translateX(float x) {
        tr[12] = x;
    }

    final void translateY(float y) {
        tr[13] = y;
    }

    /*
     * 拡大率の計算。
     * @param scaleX
     * @param scaleY
     */
    public final void multScale(float scaleX, float scaleY) {
        float tr1[] = {scaleX, 0, 0, 0, 0, scaleY, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        mul(tr1, tr, tr);
    }

    final void scale(float scaleX, float scaleY) {
        tr[0] = scaleX;
        tr[5] = scaleY;
    }
}