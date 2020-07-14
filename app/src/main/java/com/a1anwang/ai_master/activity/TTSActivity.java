package com.a1anwang.ai_master.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_core.tts.TTSConfig;
import com.a1anwang.ai_core.tts.TTSListener;
import com.a1anwang.ai_core.tts.VoiceName;
import com.a1anwang.ai_master.R;
import com.a1anwang.ai_master.TTSManager;
import com.a1anwang.ai_master.widget.CustomRadioGroup;



public class TTSActivity extends AppCompatActivity {
    public static String Baidu_appId = "16895997";

    public static String Baidu_appKey = "SA79EF1j6KGhHh8w3RtMaj9k";

    public static String Baidu_secretKey = "VrGgqCmmqeEoCo9yahVKLiYZviGCfZGr";


    public static String XunFei_AppId = "5d4bee3e";


    ProgressBar progressBar;

    EditText edit_text;

    SeekBar seekbar_speed,seekbar_pitch,seekbar_volume;
    CustomRadioGroup radiogroup_person,radiogroup_person_2;

    private String[] XunFeiVoiceNameArray={
            VoiceName.XunFei_XiaoYan.getName(),
            VoiceName.XunFei_XuJiu.getName(),
            VoiceName.XunFei_XiaoPing.getName(),
            VoiceName.XunFei_XiaoJing.getName(),
            VoiceName.XunFei_XuXiaoBao.getName(),
            VoiceName.XunFei_ChongChong.getName()
    };


    private String[] BaiduVoiceNameArray={
            VoiceName.Du_XiaoMei.getName(),
            VoiceName.Du_XiaoYu.getName(),
            VoiceName.Du_XiaoYao.getName(),
            VoiceName.Du_YaYa.getName(),
            VoiceName.Du_BoWen.getName(),
            VoiceName.Du_XiaoTong.getName(),
            VoiceName.Du_XiaoMeng.getName(),
            VoiceName.Du_Miduo.getName(),
            VoiceName.Du_XiaoJiao.getName()
    };

    private boolean useXunfei=true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTSManager.getInstance().release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        setTitle("语音合成");
        progressBar=findViewById(R.id.progress);
        edit_text=findViewById(R.id.edit_text);
        seekbar_speed=findViewById(R.id.seekbar_speed);
        seekbar_pitch=findViewById(R.id.seekbar_pitch);
        seekbar_volume=findViewById(R.id.seekbar_volume);


        final View layout_xunfei=findViewById(R.id.layout_xunfei);
        final View layout_baidu=findViewById(R.id.layout_baidu);

        RadioGroup radiogroup_type=findViewById(R.id.radiogroup_type);
        radiogroup_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_xunfei:
                        layout_xunfei.setVisibility(View.VISIBLE);
                        layout_baidu.setVisibility(View.GONE);
                        useXunfei=true;
                        TTSManager.getInstance().release();
                        initXunFeiTTS();
                        break;
                    case R.id.radio_baidu:
                        layout_xunfei.setVisibility(View.GONE);
                        layout_baidu.setVisibility(View.VISIBLE);
                        useXunfei=false;
                        TTSManager.getInstance().release();
                        initBaiduTTS();
                        break;
                }
            }
        });
        radiogroup_person=findViewById(R.id.radiogroup_person);
        radiogroup_person_2=findViewById(R.id.radiogroup_person_2);



        TTSManager.getInstance().setTTSListener(new TTSListener() {
            @Override
            public void onSpeakBegin() {
                LogUtils.e("TTS","---- onSpeakBegin");
            }

            @Override
            public void onSpeakPaused() {
                LogUtils.e("TTS","---- onSpeakPaused");
            }

            @Override
            public void onSpeakResumed() {
                LogUtils.e("TTS","---- onSpeakResumed");
            }

            @Override
            public void onSpeakProgress(float progress) {
               // LogUtils.e("TTS","---- onSpeakProgress:"+progress);
                progressBar.setProgress((int) (progress*100));
            }

            @Override
            public void onSpeakCompleted() {
                LogUtils.e("TTS","---- onCompleted");

                progressBar.setProgress(100);

            }
        });
        initXunFeiTTS();
    }


    private void initXunFeiTTS(){
        TTSManager.getInstance().init(getApplication(),XunFei_AppId,null,null);
    }
    private void initBaiduTTS(){
        TTSManager.getInstance().init(getApplication(),Baidu_appId,Baidu_appKey,Baidu_secretKey);
    }

    public void startSpeak(View view) {
        String voiceName=null;
        if(useXunfei){
            RadioButton radioButton= radiogroup_person.findCheckRadioButton();
            if(radioButton!=null){
                int tag=Integer.valueOf(radioButton.getTag()+"");
                voiceName=XunFeiVoiceNameArray[tag];
            }
        }else{
            RadioButton radioButton= radiogroup_person_2.findCheckRadioButton();
            if(radioButton!=null){
                int tag=Integer.valueOf(radioButton.getTag()+"");
                voiceName=BaiduVoiceNameArray[tag];
            }
        }

        int speed=seekbar_speed.getProgress();
        int volume=seekbar_volume.getProgress();
        int pitch=seekbar_pitch.getProgress();
        TTSConfig ttsConfig= new TTSConfig.Builder().voiceName(voiceName).pitch(pitch).volume(volume).speed(speed).build();
        TTSManager.getInstance().setConfig(ttsConfig);
        TTSManager.getInstance().startSpeaking(edit_text.getText().toString());

    }

    public void stopSpeak(View view) {
        TTSManager.getInstance().stopSpeaking();
    }
}
