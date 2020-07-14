package com.a1anwang.ai_lib;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public interface IAI {
    void fakeAIUIText(String text);
    void  startSpeak();
    void  stopSpeak();
    void release();
    void  setCallback(AICallback callback);
}
