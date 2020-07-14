package com.a1anwang.ai_lib;

import org.json.JSONObject;

/**
 * Created by a1anwang.com on 2019/8/20.
 */
public interface AICallback {

    /**
     * 用户说话结束
     */
    void onRecordComplete();

    /**
     * 技能回复
     * @param answer
     * @param rawData
     */
    void onResponse(String answer, JSONObject rawData);

    /**
     * 后处理回复
     * @param rawData
     */
    void onAfterResponse(JSONObject rawData);


    /**
     * 添加聊天记录
     * @param message
     */
    void addChatMessage(AIChatMessage message);

    /**
     * 更新聊天记录，语音识别一句话时会先调用addChatMessage，然后会多次调用updateChatMessage
     * @param message
     */
    void updateChatMessage(AIChatMessage message);
}
