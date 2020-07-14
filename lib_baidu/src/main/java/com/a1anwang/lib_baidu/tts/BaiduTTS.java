package com.a1anwang.lib_baidu.tts;

import android.app.Application;
import android.content.Context;

import com.a1anwang.ai_core.tts.ITTS;
import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_core.tts.TTSConfig;
import com.a1anwang.ai_core.tts.TTSListener;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * Created by a1anwang.com on 2019/8/1.
 */
public class BaiduTTS implements ITTS {
    private static final String TAG = BaiduTTS.class.getSimpleName();

    private volatile static BaiduTTS instance;

    private BaiduTTS() {
    }

    public static BaiduTTS getInstance() {
        if (instance == null) {
            synchronized (BaiduTTS.class) {
                if (instance == null) {
                    instance = new BaiduTTS();
                }
            }
        }
        return instance;
    }

    private Context context;
    private SpeechSynthesizer mSpeechSynthesizer;

    private boolean initialized;
    synchronized    public void init(Application application, String appid, String appkey, String appSecret) {
        if(initialized){
            return;
        }
        context=application;
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context);
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);
        mSpeechSynthesizer.setAppId(appid);
        mSpeechSynthesizer.setApiKey(appkey,appSecret);
        int result=mSpeechSynthesizer.initTts(TtsMode.ONLINE);
//        LogUtils.e("result:"+result);
        initialized=true;
    }

    private int textLength;


    @Override
    public void startSpeaking(String text) {
        if(text!=null&&(textLength=text.length())>0){
            stopSpeaking();
            mSpeechSynthesizer.speak(text);
        }

    }

    @Override
    public void stopSpeaking() {
        mSpeechSynthesizer.stop();

    }
    private TTSListener ttsListener;
    @Override
    public void setTTSListener(TTSListener ttListener) {
        this.ttsListener=ttListener;
    }

    @Override
    public void setConfig(TTSConfig ttsConfig) {
        if(ttsConfig!=null){
            String voiceName= ttsConfig.getVoiceName();
            if(voiceName!=null){
                LogUtils.e(" voiceName:"+voiceName);
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, voiceName);
            }

            if(ttsConfig.getSpeed()>=0){
                int speed=ttsConfig.getSpeed();//取值0-100；
                //百度是[0-15]；
                speed=(int) (speed/100.0f*15);
                LogUtils.e(" speed:"+speed);
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speed+"");
            }
            if(ttsConfig.getVolume()>=0){
                int volume=ttsConfig.getVolume();//取值0-100；
                //百度是[0-15]；
                volume=(int) (volume/100.0f*15);
                LogUtils.e(" volume:"+volume);
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, volume+"");
            }
            if(ttsConfig.getPitch()>=0){
                int pitch=ttsConfig.getPitch();//取值0-100；
                //百度是[0-15]；
                pitch=(int) (pitch/100.0f*15);
                LogUtils.e(" pitch:"+pitch);
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, pitch+"");
            }
        }

    }

    @Override
    public void release() {
        if(mSpeechSynthesizer!=null){
            mSpeechSynthesizer.release();
            mSpeechSynthesizer=null;
        }
        initialized=false;
    }


    private SpeechSynthesizerListener listener= new SpeechSynthesizerListener() {
        @Override
        public void onSynthesizeStart(String s) {
            LogUtils.e("onSynthesizeStart :"+s);
        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

        }

        @Override
        public void onSynthesizeFinish(String s) {
            LogUtils.e("onSynthesizeFinish :"+s);
        }

        @Override
        public void onSpeechStart(String s) {
            LogUtils.e("onSpeechStart :"+s);
            if(ttsListener!=null){
                ttsListener.onSpeakBegin();
            }
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {
            LogUtils.e("onSpeechProgressChanged :"+i);
            if(ttsListener!=null){
                ttsListener.onSpeakProgress(i/(textLength*1.0f));
            }
        }

        @Override
        public void onSpeechFinish(String s) {
            if(ttsListener!=null){
                ttsListener.onSpeakCompleted();
            }
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            LogUtils.e("onError :"+s +" "+speechError);
        }
    };
}
