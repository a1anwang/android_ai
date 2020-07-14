package com.a1anwang.lib_xunfei.tts;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.a1anwang.ai_core.tts.ITTS;
import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_core.tts.TTSConfig;
import com.a1anwang.ai_core.tts.TTSListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by a1anwang.com on 2019/8/8.
 */
public class XunFeiTTS implements ITTS {
    private static final String TAG = XunFeiTTS.class.getSimpleName();

    private volatile static XunFeiTTS instance;

    private XunFeiTTS() {
    }

    public static XunFeiTTS getInstance() {
        if (instance == null) {
            synchronized (XunFeiTTS.class) {
                if (instance == null) {
                    instance = new XunFeiTTS();
                }
            }
        }
        return instance;
    }


    private Context context;
    private String appid;
    SpeechSynthesizer speechSynthesizer;
    private boolean initialized;

    @Override
    synchronized public void init(Application application, String appid, String appkey, String appSecret) {
        if(initialized){
            return;
        }
        this.context=application;
        this.appid=appid;
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+appid);
        speechSynthesizer=SpeechSynthesizer.createSynthesizer(context, new InitListener() {
            @Override
            public void onInit(int errorCode) {//    errorCode - 错误码，0表示成功，详细错误码请看ErrorCode
                LogUtils.e(TAG,"TTS 初始化 "+errorCode);
            }
        });
        initialized=true;
    }

    @Override
    public void release() {
        if(speechSynthesizer!=null){
            speechSynthesizer.destroy();
            speechSynthesizer=null;
        }
        initialized=false;
    }

    @Override
    public void setConfig(TTSConfig ttsConfig) {
        if(ttsConfig!=null){
            String voiceName= ttsConfig.getVoiceName();
            if(voiceName!=null){
                speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceName );
            }
            if(ttsConfig.getSpeed()>=0){//[0, 100]
                speechSynthesizer.setParameter(SpeechConstant.SPEED, ttsConfig.getSpeed()+"");
            }
            if(ttsConfig.getVolume()>=0){//[0, 100]
                speechSynthesizer.setParameter(SpeechConstant.VOLUME, ttsConfig.getVolume()+"");
            }
            if(ttsConfig.getPitch()>=0){//[0, 100]
                speechSynthesizer.setParameter(SpeechConstant.PITCH, ttsConfig.getPitch()+"");
            }
        }
    }



    public void startSpeaking(String text){
        stopSpeaking();
        speechSynthesizer.startSpeaking(text, listener);
    }

    public void stopSpeaking(){

        if(speechSynthesizer.isSpeaking()){
            speechSynthesizer.stopSpeaking();
        }
    }

    private TTSListener ttsListener;
    @Override
    public void setTTSListener(TTSListener ttListener) {
        this.ttsListener=ttListener;
    }

    private SynthesizerListener listener= new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            //开始播放 SDK回调此函数，通知应用层，将要进行播放。
            if(ttsListener!=null){
                ttsListener.onSpeakBegin();
            }
        }

        @Override
        public void onBufferProgress(int progress, int beginPos, int endPos, String info) {
            //缓冲进度 SDK回调此函数，通知应用层，当前合成音频的缓冲进度。


        }

        @Override
        public void onSpeakPaused() {
            //暂停播放 SDK回调此接口，通知应用，将暂停播放。
            if(ttsListener!=null){
                ttsListener.onSpeakPaused();
            }
        }

        @Override
        public void onSpeakResumed() {
            //恢复播放 SDK回调此接口，通知应用，将恢复播放。
            if(ttsListener!=null){
                ttsListener.onSpeakResumed();
            }
        }

        @Override
        public void onSpeakProgress(int progress, int beginPos, int endPos) {
            //播放进度 SDK回调此接口，通知应用，当前的播放进度。

            if(ttsListener!=null){
                ttsListener.onSpeakProgress(progress/100.0f);
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            //结束 SDK回调此接口，通知应用，将结束会话。
           // LogUtils.e(TAG,"onCompleted :"+speechError );
            if(ttsListener!=null){
                ttsListener.onSpeakCompleted();
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle bundle) {
            //合成会话事件 扩展用接口，由具体业务进行约定。
           // LogUtils.e(TAG,"onEvent :"+eventType );
        }
    };

}
