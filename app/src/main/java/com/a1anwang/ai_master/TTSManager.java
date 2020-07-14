package com.a1anwang.ai_master;

import android.app.Application;

import com.a1anwang.ai_core.tts.ITTS;
import com.a1anwang.ai_core.tts.TTSConfig;
import com.a1anwang.ai_core.tts.TTSListener;
import com.a1anwang.lib_baidu.tts.BaiduTTS;
import com.a1anwang.lib_xunfei.tts.XunFeiTTS;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public class TTSManager  implements ITTS{

    private static final String TAG = TTSManager.class.getSimpleName();

    private volatile static TTSManager instance;

    private TTSManager() {
    }

    public static TTSManager getInstance() {
        if (instance == null) {
            synchronized (TTSManager.class) {
                if (instance == null) {
                    instance = new TTSManager();
                }
            }
        }
        return instance;
    }
    private ITTS tts;

    private String text;
    public void release(){
        if(tts!=null){
            tts.release();
            tts=null;
        }
    }

    @Override
    public void init(Application application, String appid, String appkey, String appSecret) {
        if(appid!=null&&appkey!=null&&appSecret!=null){
            tts=BaiduTTS.getInstance();
            tts.init(application,appid,appkey,appSecret);
        }else {
            tts=XunFeiTTS.getInstance();
            tts.init(application,appid,appkey,appSecret);
        }
        setTTSListener(ttsListener);
    }

    public void startSpeaking(String text){
        if(tts!=null){
            this.text=text;
            tts.startSpeaking(text);
        }
    }

    public String getText() {
        return text;
    }

    public void stopSpeaking(){
        if(tts!=null){
            tts.stopSpeaking();
        }
    }

    private TTSListener ttsListener;
    public void setTTSListener(TTSListener listener) {
        this.ttsListener=listener;
        if(tts!=null){
            tts.setTTSListener(listener);
        }
    }
    public void setConfig(TTSConfig ttsConfig){
        if(tts!=null){
            tts.setConfig(ttsConfig);
        }
    }

}
