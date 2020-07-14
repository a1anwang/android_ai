package com.a1anwang.ai_master.widget;

import android.widget.ImageView;

import com.a1anwang.ai_lib.AIChatMessage;
import com.a1anwang.ai_master.ChatMessage;
import com.a1anwang.ai_master.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * Created by a1anwang.com on 2019/4/24.
 */
public class AIUIChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

    public AIUIChatAdapter() {
        super(null);

        addItemType(ChatMessage.FromType_AI, R.layout.item_aiui_chat);
        addItemType(ChatMessage.FromType_User, R.layout.item_aiui_chat_right);
    }


    @Override
    protected void convert(BaseViewHolder helper, ChatMessage item) {
        switch (helper.getItemViewType()) {
            case ChatMessage.FromType_AI:
                helper.setText(R.id.tv_content, item.getAiChatMessage().getContent());
                break;
            case ChatMessage.FromType_User:
                if(item.getAiChatMessage().getMsgType()==AIChatMessage.MsgType.TEXT){
                    helper.setGone(R.id.imageview_voice,false);
                }else if(item.getAiChatMessage().getMsgType()==AIChatMessage.MsgType.Voice){
                    helper.setGone(R.id.imageview_voice,true);
                }
                helper.setText(R.id.tv_content, item.getAiChatMessage().getContent());
                ImageView imageView=helper.getView(R.id.imageview_head);
                Glide.with(mContext).load(R.drawable.icon_default_head).apply(RequestOptions.circleCropTransform()).into(imageView);
                break;
        }
    }

}
