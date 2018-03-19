/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
*/
package jp.live2d.framework;

import jp.live2d.ALive2DModel;
import jp.live2d.motion.AMotion;
import jp.live2d.motion.MotionQueueManager.MotionQueueEnt;
import jp.live2d.util.Json;
import jp.live2d.util.Json.Value;
import jp.live2d.util.UtFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/*
 * 差分モーション。
 * 通常のモーションは値をsetParamFloatでセットするが、
 * この差分モーションでは値を足すか、掛けるかする。
 *
 * Live2DライブラリのAMotionを継承しているのでMotionQueueManagerで管理できる。
 */
public final class L2DExpressionMotion extends AMotion {
    private static final String EXPRESSION_DEFAULT = "DEFAULT"; // 表情のデフォルト値要素のキー
    private static final int TYPE_SET = 0;
    private static final int TYPE_ADD = 1;
    private static final int TYPE_MULT = 2;
    private ArrayList<L2DExpressionParam> paramList;

    private L2DExpressionMotion() {
        paramList = new ArrayList<>();
    }

    /*
     * JSONファイルから読み込み。
     * 仕様についてはマニュアル参照。JSONスキーマの形式の仕様がある。
     * @param in
     * @return
     * @throws Exception
     */
    public static L2DExpressionMotion loadJson(InputStream in) {
        byte[] buf = UtFile.load(in);
        return loadJson(buf);
    }

    /*
     * JSONファイルから読み込み。
     * 仕様についてはマニュアル参照。JSONスキーマの形式の仕様がある。
     * @param buf
     * @return
     * @throws Exception
     */
    public static L2DExpressionMotion loadJson(byte[] buf) {
        L2DExpressionMotion ret = new L2DExpressionMotion();
        Value json = Json.parseFromBytes(buf);

        ret.setFadeIn(json.get("fade_in").toInt(1000));        // フェードイン
        ret.setFadeOut(json.get("fade_out").toInt(1000));    // フェードアウト

        if (json.get("params") == null)
            return ret;

        // パラメータ一覧
        Value params = json.get("params");
        int paramNum = params.getVector(null).size();

        ret.paramList = new ArrayList<>(paramNum);

        for (int i = 0; i < paramNum; i++) {
            Value param = params.get(i);
            String paramID = param.get("id").toString();    // パラメータID
            float value = param.get("val").toFloat();    // 値

            // 計算方法の設定
            int calcTypeInt;
            String calc = param.get("calc") != null ? param.get("calc").toString() : "add";
            switch (calc) {
                case "add":
                    calcTypeInt = TYPE_ADD;
                    break;
                case "mult":
                    calcTypeInt = TYPE_MULT;
                    break;
                case "set":
                    calcTypeInt = TYPE_SET;
                    break;
                default:
                    // その他 仕様にない値を設定したときは加算モードにすることで復旧
                    calcTypeInt = TYPE_ADD;
                    break;
            }

            // 計算方法 加算
            if (calcTypeInt == TYPE_ADD) {
                float defaultValue = param.get("def") == null ? 0 : param.get("def").toFloat();
                value = value - defaultValue;
            }
            // 計算方法 乗算
            else if (calcTypeInt == TYPE_MULT) {
                float defaultValue = param.get("def") == null ? 1 : param.get("def").toFloat(0);
                if (defaultValue == 0)
                    defaultValue = 1; // 0(不正値)を指定した場合は1(標準)にする
                value = value / defaultValue;
            }

            // 設定オブジェクトを作成してリストに追加する
            L2DExpressionParam item = new L2DExpressionParam();

            item.id = paramID;
            item.type = calcTypeInt;
            item.value = value;

            ret.paramList.add(item);
        }
        return ret;
    }

    /*
     * 表情JSONを読み込み
     * @param in
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, AMotion> loadExpressionJsonV09(InputStream in) {
        HashMap<String, AMotion> expressions = new HashMap<>();
        byte[] buf = UtFile.load(in);
        Value mo = Json.parseFromBytes(buf);
        Value defaultExpr = mo.get(EXPRESSION_DEFAULT); // 相対値の基準となる値

        Set<String> keys = mo.keySet();
        for (String key : keys) {
            if (EXPRESSION_DEFAULT.equals(key))
                continue; // 飛ばす

            Value expr = mo.get(key);

            L2DExpressionMotion exMotion = loadJsonV09(defaultExpr, expr);
            expressions.put(key, exMotion);
        }
        return expressions; // nullには成らない
    }

    /*
     * JSONの解析結果からExpressionを生成する
     * @param v
     */
    private static L2DExpressionMotion loadJsonV09(Value defaultExpr, Value expr) {
        L2DExpressionMotion ret = new L2DExpressionMotion();
        ret.setFadeIn(expr.get("FADE_IN").toInt(1000));
        ret.setFadeOut(expr.get("FADE_OUT").toInt(1000));

        // --- IDリストを生成
        Value defaultParams = defaultExpr.get("PARAMS");
        Value params = expr.get("PARAMS");

        @SuppressWarnings("unchecked")
        Set<String> paramID = params.keySet();
        ArrayList<String> idList = new ArrayList<>(paramID);

        // --------- 値を設定 ---------
        for (int i = idList.size() - 1; i >= 0; --i) {
            String id = idList.get(i);

            float defaultV = defaultParams.get(id).toFloat(0);
            float v = params.get(id).toFloat(0.0f);
            float value = (v - defaultV);
//			ret.addParam(id, value,L2DExpressionMotion.TYPE_ADD);
            L2DExpressionParam param = new L2DExpressionParam();
            param.id = id;
            param.type = L2DExpressionMotion.TYPE_ADD;
            param.value = value;
            ret.paramList.add(param);
        }
        return ret;
    }

    /*
     * モデルのパラメータを更新する。
     * 引数の詳細はドキュメントを参照。
     */
    @Override
    public final void updateParamExe(ALive2DModel model, long timeMSec, float weight, MotionQueueEnt motionQueueEnt) {
        for (int i = paramList.size() - 1; i >= 0; --i) {
            L2DExpressionParam param = paramList.get(i);
            if (param.type == TYPE_ADD)
                model.addToParamFloat(param.id, param.value, weight);    // 相対変化 加算
            else if (param.type == TYPE_MULT)
                model.multParamFloat(param.id, param.value, weight);    // 相対変化 乗算
            else if (param.type == TYPE_SET)
                model.setParamFloat(param.id, param.value, weight);        // 絶対変化
        }
    }

    /*
     * パラメータの設定に使用する
     */
    public static final class L2DExpressionParam {
        public String id;
        // public int index = -1;
        public int type;
        public float value;
    }
}