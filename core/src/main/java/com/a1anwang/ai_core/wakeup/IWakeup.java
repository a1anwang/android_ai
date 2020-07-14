package com.a1anwang.ai_core.wakeup;

import android.app.Application;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public interface IWakeup {
    void init(Application application, String appid, String appkey, String appSecret);
    void start();
    void stop();
    void setWakeupListener(WakeupListener wakeupListener);
    void release();
}
