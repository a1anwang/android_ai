package com.a1anwang.ai_master.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.a1anwang.ai_master.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public class CustomRadioGroup extends RadioGroup {


    private int startX = 0, startY = 0, rowNm = 0;

    private List<String> childs = new ArrayList<>();

    private int childId =-1;

    /**
     * 横向间距
     */
    private int childMarginHorizontal = 10;
    /**
     * 纵向间距
     */
    private int childMarginVertical = 10;


    public CustomRadioGroup(Context context) {
        super(context);

    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 0;
        if (getChildCount() > 0) {
            startX = 0;
            //父控件高度

            rowNm = 0;
            for (int i = 0; i < getChildCount(); i++) {

                RadioButton rb = (RadioButton) getChildAt(i);
                //测量子控件
                measureChild(rb, widthMeasureSpec, heightMeasureSpec);

                //子控件宽度+起始位置坐标，如果大于父控件高度，就换行
                int w = rb.getMeasuredWidth() + 2 * childMarginHorizontal + startX + getPaddingLeft() + getPaddingRight();
                if (w > getMeasuredWidth()) {
                    startX = 0;
                    rowNm++;
                }
                //否则起始位置后移
                startX += rb.getMeasuredWidth() + 2 * childMarginHorizontal;
                height = (rowNm + 1) * (rb.getMeasuredHeight() + 2 * childMarginVertical) + getPaddingBottom() + getPaddingTop();

            }
        }

        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        startX = 0;
        startY = 0;
        rowNm = 0;
        for (int i = 0; i < getChildCount(); i++) {

            RadioButton rb = (RadioButton) getChildAt(i);
            int w = rb.getMeasuredWidth() + 2 * childMarginHorizontal + startX + getPaddingLeft() + getPaddingRight();
            if (w > getMeasuredWidth()) {
                startX = 0;
                rowNm++;
            }
            startY = rowNm * (rb.getMeasuredHeight() + 2 * childMarginVertical);

            //绘制每个子控件的位置
            rb.layout(startX, startY, startX + rb.getMeasuredWidth(), startY + rb.getMeasuredHeight());
            startX += rb.getMeasuredWidth() + 2 * childMarginHorizontal;


        }


    }

    private RadioButton getChild() {

        if (childId==-1){
            throw new RuntimeException("没有设置子控件");
        }
        return (RadioButton) LayoutInflater.from(getContext()).inflate( R.layout.radio_item, this, false);
    }

    /**
     * 设置子控件，最好为根节点为RadioButton 的layout
     * @param layout_id 子控件的Id
     */
    public void setChild(int layout_id){
        this.childId=layout_id;
    }

    /**
     * 添加一个名字为str的控件
     * @param str
     */
    public void addView(String str) {
        childs.add(str);
        RadioButton child = getChild();
        child.setText(str);
        addView(child);
        postInvalidate();
    }


    public RadioButton findCheckRadioButton(){
       int count= getChildCount();
       for (int i=0;i<count;i++){
           RadioButton radioButton= (RadioButton) getChildAt(i);
           if(radioButton.isChecked()){
               return radioButton;
           }
       }
       return null;
    }

}