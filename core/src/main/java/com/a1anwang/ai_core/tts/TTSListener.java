package com.a1anwang.ai_core.tts;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public interface TTSListener {
    void onSpeakBegin();
    void onSpeakPaused();
    void onSpeakResumed();
    void onSpeakProgress(float progress);
    void onSpeakCompleted();

}
