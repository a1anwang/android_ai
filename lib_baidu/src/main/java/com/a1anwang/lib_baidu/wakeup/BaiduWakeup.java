package com.a1anwang.lib_baidu.wakeup;

import android.app.Application;
import android.content.Context;

import com.a1anwang.ai_core.wakeup.IWakeup;
import com.a1anwang.ai_core.wakeup.WakeupListener;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class BaiduWakeup implements IWakeup{


    private static final String TAG = BaiduWakeup.class.getSimpleName();

    private volatile static BaiduWakeup instance;

    private BaiduWakeup() {
    }

    public static BaiduWakeup getInstance() {
        if (instance == null) {
            synchronized (BaiduWakeup.class) {
                if (instance == null) {
                    instance = new BaiduWakeup();
                }
            }
        }
        return instance;
    }

    private Context context;
    private EventManager wakeup;
    private WakeupListener wakeupListener;

    boolean initialized=false;

    synchronized public void init(Application application, String appid, String appkey, String appSecret) {
        if(initialized){
            return;
        }
        this.context=application;
        wakeup = EventManagerFactory.create(context, "wp");
        // 基于SDK唤醒词集成1.3 注册输出事件
        wakeup.registerListener(eventListener); //  EventListener 中 onEvent方法
        initialized=true;
    }



    @Override
    public void start() {
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        String json  = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);

    }

    @Override
    public void stop() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //

    }

    @Override
    public void setWakeupListener(WakeupListener wakeupListener) {
        this.wakeupListener = wakeupListener;

    }

    @Override
    public void release() {
        stop();
        if(wakeup!=null){
            wakeup.unregisterListener(eventListener);
            wakeup = null;
        }
        initialized=false;
    }


    private EventListener eventListener=new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS.equals(name)) { // 识别唤醒词成功
                WakeUpResult result = WakeUpResult.parseJson(name, params);
                int errorCode = result.getErrorCode();
                if (result.hasError()) { // error不为0依旧有可能是异常情况
                    // wakeupListener.onError(errorCode, "", result);
                } else {
                    String word = result.getWord();
                    wakeupListener.onWakeup(word);
                }
            }
        }
    };


}
