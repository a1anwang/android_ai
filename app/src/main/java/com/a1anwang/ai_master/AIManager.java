package com.a1anwang.ai_master;

import android.app.Application;

import com.a1anwang.ai_lib.AICallback;
import com.a1anwang.ai_lib.IAI;
import com.a1anwang.ai_lib.xunfei.XunFeiAIUI;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class AIManager {
    private static final String TAG = AIManager.class.getSimpleName();

    private volatile static AIManager instance;

    private AIManager() {
    }

    public static AIManager getInstance() {
        if (instance == null) {
            synchronized (AIManager.class) {
                if (instance == null) {
                    instance = new AIManager();
                }
            }
        }
        return instance;
    }
    private IAI ai;

    public void init(Application application){
        ai=XunFeiAIUI.getInstance();
        ((XunFeiAIUI) ai).init(application);

    }
    public void fakeAIUIText(String text){
        if(ai!=null){
            ai.fakeAIUIText(text);
        }
    }
    public void startSpeak(){
        if(ai!=null){
            ai.startSpeak();
        }
    }
    public void stopSpeak(){
        if(ai!=null){
            ai.stopSpeak();
        }
    }

    public void setCallback(AICallback callback){
        if(ai!=null){
            ai.setCallback(callback);
        }
    }
}
