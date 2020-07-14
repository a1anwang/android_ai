package com.a1anwang.ai_master;

import com.a1anwang.ai_lib.AIChatMessage;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by a1anwang.com on 2019/8/20.
 */
public class ChatMessage   implements MultiItemEntity {
    public static final int FromType_AI = 1;
    public static final int FromType_User = 2;



    private AIChatMessage aiChatMessage;

    public AIChatMessage getAiChatMessage() {
        return aiChatMessage;
    }

    public ChatMessage(AIChatMessage aiChatMessage) {
        this.aiChatMessage=aiChatMessage;
    }


    @Override
    public int getItemType() {
        return aiChatMessage.isFromUser()?FromType_User:FromType_AI;
    }
}
