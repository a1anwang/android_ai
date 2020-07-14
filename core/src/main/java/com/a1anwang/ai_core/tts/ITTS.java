package com.a1anwang.ai_core.tts;

import android.app.Application;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public interface ITTS {
    void init(Application application,String appid,String appkey,String appSecret);
    void startSpeaking(String text);
    void stopSpeaking();
    void setTTSListener(TTSListener ttListener);
    void setConfig(TTSConfig ttsConfig);
    void release();
}
