package com.a1anwang.ai_master.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_core.wakeup.WakeupListener;
import com.a1anwang.ai_master.R;
import com.a1anwang.ai_master.TTSManager;
import com.a1anwang.ai_master.WakeupManager;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class WakeupActivity extends AppCompatActivity {

    public static String Baidu_appId = "16895997";

    public static String Baidu_appKey = "SA79EF1j6KGhHh8w3RtMaj9k";

    public static String Baidu_secretKey = "VrGgqCmmqeEoCo9yahVKLiYZviGCfZGr";


    public static String XunFei_AppId = "5d4bee3e";



    TextView tv_tip;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        setTitle("语音唤醒");

        TTSManager.getInstance().init(getApplication(),XunFei_AppId,null,null);


        WakeupManager.getInstance().setWakeupListener(new WakeupListener() {
            @Override
            public void onWakeup(String word) {
                LogUtils.e(" onWakeup:"+word);
                TTSManager.getInstance().startSpeaking("主人你好");
            }
        });


        tv_tip=findViewById(R.id.tv_tip);
        if(WakeupManager.getInstance().isStarted()){
            tv_tip.setVisibility(View.VISIBLE);
        }else{
            tv_tip.setVisibility(View.GONE);
        }

        RadioGroup radiogroup_type=findViewById(R.id.radiogroup_type);
        radiogroup_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_xunfei:
                        WakeupManager.getInstance().release();
                        initXunFeiWakeup();
                        tv_tip.setVisibility(View.GONE);
                        break;
                    case R.id.radio_baidu:
                        WakeupManager.getInstance().release();
                        initBaiduWakeup();
                        tv_tip.setVisibility(View.GONE);
                        break;
                }
            }
        });
        initXunFeiWakeup();

    }

    private void initBaiduWakeup() {

        WakeupManager.getInstance().init(getApplication(),Baidu_appId,Baidu_appKey,Baidu_secretKey);
    }

    private void initXunFeiWakeup() {

        WakeupManager.getInstance().init(getApplication(),XunFei_AppId,null,null);

    }


    public void startClick(View view){
        WakeupManager.getInstance().start();
        tv_tip.setVisibility(View.VISIBLE);
    }
    public void stopClick(View view){
        WakeupManager.getInstance().stop();
        tv_tip.setVisibility(View.GONE);

    }
}
