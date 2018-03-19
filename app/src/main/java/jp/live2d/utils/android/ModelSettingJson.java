/*
   You can modify and use this source freely
   only for the development of application related Live2D.

   (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import jp.live2d.util.Json;
import jp.live2d.util.Json.Value;
import jp.live2d.util.UtFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ModelSettingJson implements ModelSetting {
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String MODEL = "model";
    private static final String TEXTURES = "textures";
    private static final String HIT_AREAS = "hit_areas";
    private static final String PHYSICS = "physics";
    private static final String POSE = "pose";
    private static final String EXPRESSIONS = "expressions";
    private static final String MOTION_GROUPS = "motions";
    private static final String SOUND = "sound";
    private static final String FADE_IN = "fade_in";
    private static final String FADE_OUT = "fade_out";
    private static final String VALUE = "val";
    private static final String FILE = "file";
    private static final String INIT_PARTS_VISIBLE = "init_parts_visible";
    private static final String INIT_PARAM = "init_param";
    private static final String LAYOUT = "layout";
    private Value json;

    public ModelSettingJson(InputStream in) {
        byte[] buf = UtFile.load(in);
        json = Json.parseFromBytes(buf);
    }

    private boolean existMotion(String name) {
        return (json.get(MOTION_GROUPS).get(name) != null); // json.motion_group[name]
    }

    private boolean existMotionSound(String name, int n) {
        return json.get(MOTION_GROUPS).get(name).get(n).get(SOUND) != null;
    }

    private boolean existMotionFadeIn(String name, int n) {
        return json.get(MOTION_GROUPS).get(name).get(n).get(FADE_IN) != null;
    }

    private boolean existMotionFadeOut(String name, int n) {
        return json.get(MOTION_GROUPS).get(name).get(n).get(FADE_OUT) != null;
    }

    @Override
    public String getModelName() {
        if (json.get(NAME) == null)
            return null;
        return json.get(NAME).toString();
    }

    @Override
    public String getModelFile() {
        if (json.get(MODEL) == null)
            return null;
        return json.get(MODEL).toString();
    }

    @Override
    public int getTextureNum() {
        if (json.get(TEXTURES) == null)
            return 0;
        return json.get(TEXTURES).getVector(null).size(); // json.textures.length
    }

    @Override
    public String getTextureFile(int n) {
        return json.get(TEXTURES).get(n).toString(); // json.textures[n]
    }

    @Override
    public int getHitAreasNum() {
        if (json.get(HIT_AREAS) == null)
            return 0;
        return json.get(HIT_AREAS).getVector(null).size(); // json.hit_area.length
    }

    @Override
    public String getHitAreaID(int n) {
        return json.get(HIT_AREAS).get(n).get(ID).toString(); // json.hit_area[n].id
    }

    @Override
    public String getHitAreaName(int n) {
        return json.get(HIT_AREAS).get(n).get(NAME).toString(); // json.hit_area[n].name
    }

    @Override
    public String getPhysicsFile() {
        if (json.get(PHYSICS) == null)
            return null;
        return json.get(PHYSICS).toString();
    }

    @Override
    public String getPoseFile() {
        if (json.get(POSE) == null)
            return null;
        return json.get(POSE).toString();
    }

    @Override
    public int getMotionNum(String name) {
        if (!existMotion(name))
            return 0;
        return json.get(MOTION_GROUPS).get(name).getVector(null).size(); // json.motion_group[name].length
    }

    @Override
    public String getMotionFile(String name, int n) {
        if (!existMotion(name))
            return null;
        return json.get(MOTION_GROUPS).get(name).get(n).get(FILE).toString(); // json.motion_group[name][n].file
    }

    @Override
    public String getMotionSound(String name, int n) {
        if (!existMotionSound(name, n))
            return null;
        return json.get(MOTION_GROUPS).get(name).get(n).get(SOUND).toString(); // json.motion_group[name][n].sound
    }

    @Override
    public int getMotionFadeIn(String name, int n) {
        return (!existMotionFadeIn(name, n)) ? 1000 : json.get(MOTION_GROUPS).get(name).get(n).get(FADE_IN).toInt(); // json.motion_group[name][n].fade_in
    }

    @Override
    public int getMotionFadeOut(String name, int n) {
        return (!existMotionFadeOut(name, n)) ? 1000 : json.get(MOTION_GROUPS).get(name).get(n).get(FADE_OUT).toInt(); // json.motion_group[name][n].fade_out
    }

    @Override
    public String[] getMotionGroupNames() {
        if (json.get(MOTION_GROUPS) == null)
            return null;
        Object[] keys = json.get(MOTION_GROUPS).getMap(null).keySet().toArray();

        if (keys.length == 0)
            return null;

        String[] names = new String[keys.length];

        for (int i = 0; i < names.length; i++) {
            names[i] = (String) keys[i];
        }
        return names;
    }

    // 表示位置
    @SuppressWarnings("unchecked")
    @Override
    public boolean getLayout(Map<String, Float> layout) {
        if (json.get(LAYOUT) == null)
            return false;

        Map<String, Value> map = json.get(LAYOUT).getMap(null);
        String[] keys = map.keySet().toArray(new String[map.size()]);

        for (String key : keys) {
            layout.put(key, json.get(LAYOUT).get(key).toFloat());
        }
        return true;
    }

    // 初期パラメータについて
    @Override
    public int getInitParamNum() {
        if (json.get(INIT_PARAM) == null)
            return 0;
        return json.get(INIT_PARAM).getVector(null).size();
    }

    @Override
    public float getInitParamValue(int n) {
        return json.get(INIT_PARAM).get(n).get(VALUE).toFloat();
    }

    @Override
    public String getInitParamID(int n) {
        return json.get(INIT_PARAM).get(n).get(ID).toString();
    }

    // 初期パーツ表示について
    @Override
    public int getInitPartsVisibleNum() {
        if (json.get(INIT_PARTS_VISIBLE) == null)
            return 0;
        return json.get(INIT_PARTS_VISIBLE).getVector(null).size();
    }

    @Override
    public float getInitPartsVisibleValue(int n) {
        return json.get(INIT_PARTS_VISIBLE).get(n).get(VALUE).toFloat();
    }

    @Override
    public String getInitPartsVisibleID(int n) {
        return json.get(INIT_PARTS_VISIBLE).get(n).get(ID).toString();
    }

    @Override
    public int getExpressionNum() {
        if (json.get(EXPRESSIONS) == null)
            return 0;
        return json.get(EXPRESSIONS).getVector(null).size();
    }

    @Override
    public String getExpressionFile(int n) {
        return json.get(EXPRESSIONS).get(n).get(FILE).toString();
    }

    @Override
    public String getExpressionName(int n) {
        return json.get(EXPRESSIONS).get(n).get(NAME).toString();
    }

    @Override
    public String[] getTextureFiles() {
        String[] ret = new String[getTextureNum()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getTextureFile(i);
        }
        return ret;
    }

    @Override
    public String[] getExpressionFiles() {
        String[] ret = new String[getExpressionNum()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getExpressionFile(i);
        }
        return ret;
    }

    @Override
    public String[] getExpressionNames() {
        String[] ret = new String[getExpressionNum()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getExpressionName(i);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public String[] getSoundPaths() {
        if (json.get(MOTION_GROUPS) == null)
            return null;

        List<String> ret = new ArrayList<>();
        Map<String, Value> map = json.get(MOTION_GROUPS).getMap(null);
        for (Map.Entry<String, Value> e : map.entrySet()) {
            List<Value> motions = e.getValue().getVector(null);

            for (Value motion : motions) {
                Value soundPath = motion.get(SOUND);
                if (soundPath != null) {
                    ret.add(soundPath.toString());
                }
            }
        }

        return ret.toArray(new String[0]);
    }
}