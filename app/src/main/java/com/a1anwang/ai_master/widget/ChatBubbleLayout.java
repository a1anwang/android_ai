package com.a1anwang.ai_master.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;

/**
 * Created by a1anwang.com on 2019/6/19.
 */
public class ChatBubbleLayout extends BubbleLayout {
    public ChatBubbleLayout(Context context) {
        super(context);
    }

    public ChatBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatBubbleColor(int color){
        setBubbleColor(color);
    }

    public void setChatArrowDirection(ArrowDirection direction) {
        setArrowDirection(direction);
    }
}
