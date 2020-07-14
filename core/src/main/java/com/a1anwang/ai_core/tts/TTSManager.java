//package com.a1anwang.ai_core.tts;
//
//import android.app.Application;
//
//import com.a1anwang.tts_lib.baidu.BaiduTTS;
//import com.a1anwang.tts_lib.xunfei.XunFeiTTS;
//
///**
// * Created by a1anwang.com on 2019/8/13.
// */
//public class TTSManager  implements ITTS{
//
//    private static final String TAG = TTSManager.class.getSimpleName();
//
//    private volatile static TTSManager instance;
//
//    private TTSManager() {
//    }
//
//    public static TTSManager getInstance() {
//        if (instance == null) {
//            synchronized (TTSManager.class) {
//                if (instance == null) {
//                    instance = new TTSManager();
//                }
//            }
//        }
//        return instance;
//    }
//    private ITTS tts;
//
//
//    @Override
//    public void init(Application application, String appid, String appkey, String appSecret) {
//        tts=BaiduTTS.getInstance();
//        ((BaiduTTS)tts).init(application,appid,appkey,appSecret);
//
//        setTTSListener(ttsListener);
//    }
//
//    public void initWithXunFei(Application application,String appid ){
//        tts=XunFeiTTS.getInstance();
//        ((XunFeiTTS)tts).init(application,appid);
//        setTTSListener(ttsListener);
//    }
//    public void initWithBaidu(Application application,String appid,String appkey,String appSecret){
//        tts=BaiduTTS.getInstance();
//        ((BaiduTTS)tts).init(application,appid,appkey,appSecret);
//
//        setTTSListener(ttsListener);
//    }
//    public void release(){
//        if(tts!=null){
//            tts.release();
//            tts=null;
//        }
//    }
//
//
//    public void startSpeaking(String text){
//        if(tts!=null){
//            tts.startSpeaking(text);
//        }
//    }
//
//    public void stopSpeaking(){
//        if(tts!=null){
//            tts.stopSpeaking();
//        }
//    }
//
//    private TTSListener ttsListener;
//    public void setTTSListener(TTSListener listener) {
//        this.ttsListener=listener;
//        if(tts!=null){
//            tts.setTTSListener(listener);
//        }
//    }
//    public void setConfig(TTSConfig ttsConfig){
//        if(tts!=null){
//            tts.setConfig(ttsConfig);
//        }
//    }
//
//}
