package com.a1anwang.ai_master;

import android.app.Application;

import com.a1anwang.ai_core.wakeup.IWakeup;
import com.a1anwang.ai_core.wakeup.WakeupListener;
import com.a1anwang.lib_baidu.wakeup.BaiduWakeup;
import com.a1anwang.lib_xunfei.wakeup.XunFeiWakeup;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class WakeupManager implements IWakeup{
    private static final String TAG = WakeupManager.class.getSimpleName();

    private volatile static WakeupManager instance;

    private WakeupManager() {
    }

    public static WakeupManager getInstance() {
        if (instance == null) {
            synchronized (WakeupManager.class) {
                if (instance == null) {
                    instance = new WakeupManager();
                }
            }
        }
        return instance;
    }
    private IWakeup wakeup;
    private WakeupListener wakeupListener;

    public void setWakeupListener(WakeupListener wakeupListener) {
        this.wakeupListener = wakeupListener;
        if(wakeup!=null){
            wakeup.setWakeupListener(wakeupListener);
        }
    }



    @Override
    public void init(Application application, String appid, String appkey, String appSecret) {
        if(appid!=null&&appkey!=null&&appSecret!=null){
            wakeup=BaiduWakeup.getInstance();
            wakeup.init(application,appid,appkey,appSecret);
        }else {
            wakeup=XunFeiWakeup.getInstance();
            wakeup.init(application,appid,appkey,appSecret);
        }

        setWakeupListener(wakeupListener);
    }

    private boolean started;

    public boolean isStarted() {
        return started;
    }

    public void start(){
        if(wakeup!=null){
            wakeup.start();
            started=true;
        }
    }
    public void stop(){
        if(wakeup!=null){
            wakeup.stop();
        }
        started=false;
    }
    public void release(){

        if(wakeup!=null){
            wakeup.release();
            wakeup=null;
        }
        started=false;
    }

}
