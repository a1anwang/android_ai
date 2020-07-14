package com.a1anwang.lib_xunfei.wakeup;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.a1anwang.ai_core.wakeup.IWakeup;
import com.a1anwang.ai_core.wakeup.WakeupListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a1anwang.com on 2019/8/16.
 */
public class XunFeiWakeup implements IWakeup {


    private static final String TAG = XunFeiWakeup.class.getSimpleName();

    private volatile static XunFeiWakeup instance;

    private XunFeiWakeup() {
    }

    public static XunFeiWakeup getInstance() {
        if (instance == null) {
            synchronized (XunFeiWakeup.class) {
                if (instance == null) {
                    instance = new XunFeiWakeup();
                }
            }
        }
        return instance;
    }
    private Context context;
    private String appid;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    private boolean initialized;
    synchronized public void init(Application application, String appid, String appkey, String appSecret) {
        if(initialized){
            return;
        }
        this.context=application;
        this.appid=appid;

        SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+appid);
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);

        // 唤醒资源路径，需下载使用对应的语音唤醒SDK。
        mIvw.setParameter( SpeechConstant.IVW_RES_PATH, getResource() );

        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");


        // 持续唤醒
        mIvw.setParameter( SpeechConstant.KEEP_ALIVE, "1" );


        initialized=true;
    }


    @Override
    public void start() {
        mIvw.startListening(listener);
    }

    @Override
    public void stop() {
        mIvw.stopListening();
    }

    private WakeupListener wakeupListener;
    @Override
    public void setWakeupListener(WakeupListener wakeupListener) {
        this.wakeupListener=wakeupListener;
    }

    @Override
    public void release() {
        stop();
        if(mIvw!=null){
            mIvw.destroy();
            mIvw=null;
        }
        initialized=false;
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/ivw.jet");

        return resPath;
    }


    private WakeuperListener listener=new WakeuperListener() {
        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onResult(WakeuperResult result) {
            String resultString;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
                if(result!=null){
                    if(wakeupListener!=null){
                        wakeupListener.onWakeup(object.optString("keyword"));
                    }
                }
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            //Log.e(TAG," resultString:"+resultString);

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
}
