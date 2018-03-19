package com.live2d.live2dsimple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import jp.live2d.utils.android.SoundManager;

public final class MainActivity extends Activity {
    // Live2Dの管理
    private LAppLive2DManager live2DMgr;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // オブジェクトを初期化
        live2DMgr = new LAppLive2DManager(this.getApplicationContext());
        // GUIを初期化
        setupGUI();
    }

    @Override
    protected final void onDestroy() {
        exit();
        super.onDestroy();
    }

    @Override
    protected final void onPause() {
        super.onPause();
        live2DMgr.onPause();
    }

    @Override
    protected final void onResume() {
        super.onResume();
        live2DMgr.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundManager.init();
    }

    /*
     * GUIの初期化
     * activity_main.xmlからViewを作成し、そこにLive2Dを配置する
     */
    private void setupGUI() {
        setContentView(R.layout.activity_main);

        //  Viewの初期化
        LAppView view = live2DMgr.createView(this);

        // activity_main.xmlにLive2DのViewをレイアウトする
        FrameLayout layout = findViewById(R.id.live2DLayout);
        layout.addView(view, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        // モデル切り替えボタン
        ImageButton iBtn = findViewById(R.id.imageButton1);
        ClickListener listener = new ClickListener();
        iBtn.setOnClickListener(listener);
    }

    private void exit() {
        SoundManager.release();
    }

    // ボタンを押した時のイベント
    private final class ClickListener implements View.OnClickListener {
        @Override
        public final void onClick(View v) {
            Toast.makeText(MainActivity.this, "change model", Toast.LENGTH_SHORT).show();
            live2DMgr.changeModel(); // Live2D Event
        }
    }
}