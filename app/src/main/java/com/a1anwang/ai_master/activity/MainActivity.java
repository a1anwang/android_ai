package com.a1anwang.ai_master.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_master.NavigationManager;
import com.a1anwang.ai_master.R;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class MainActivity extends AppCompatActivity implements NavigationManager.NaviStateListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavigationManager.getInstance().addNaviStateListener(this,this);


    }
    public void ttsClick(View view){
        Intent intent=new Intent(this,TTSActivity.class);
        startActivity(intent);
    }
    public void wakeupClick(View view){
        Intent intent=new Intent(this,WakeupActivity.class);
        startActivity(intent);
    }
    public void aiClick(View view){
        Intent intent=new Intent(this,AIActivity.class);
        startActivity(intent);
    }


    @Override
    public void onVisibleChanged(boolean visible) {
        LogUtils.e("onVisibleChanged:"+visible +" height:"+ NavigationManager.getInstance().getNavHeight());
    }
}
