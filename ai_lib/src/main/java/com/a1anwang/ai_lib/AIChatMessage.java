package com.a1anwang.ai_lib;

/**
 * Created by a1anwang.com on 2019/6/19.
 */
public class AIChatMessage  {
    public enum MsgType {
        TEXT, Voice
    }


    private String content;

    public MsgType msgType= MsgType.TEXT;



    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    boolean fromUser;
    public AIChatMessage(boolean fromUser, String content) {
        this.fromUser=fromUser;
        this.content=content;

    }

    public AIChatMessage(MsgType msgType, boolean fromUser, String content) {
        this.msgType=msgType;
        this.fromUser=fromUser;
        this.content=content;


    }

    public boolean isFromUser() {
        return fromUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
