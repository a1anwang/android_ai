package com.a1anwang.ai_master.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.a1anwang.ai_core.tts.LogUtils;
import com.a1anwang.ai_core.tts.TTSListener;
import com.a1anwang.ai_core.wakeup.WakeupListener;
import com.a1anwang.ai_lib.AICallback;
import com.a1anwang.ai_lib.AIChatMessage;
import com.a1anwang.ai_master.AIManager;
import com.a1anwang.ai_master.ChatMessage;
import com.a1anwang.ai_master.R;
import com.a1anwang.ai_master.TTSManager;
import com.a1anwang.ai_master.WakeupManager;
import com.a1anwang.ai_master.widget.AIUIChatAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class AIActivity extends AppCompatActivity implements AICallback, TTSListener {
    public static String Baidu_appId = "16895997";

    public static String Baidu_appKey = "SA79EF1j6KGhHh8w3RtMaj9k";

    public static String Baidu_secretKey = "VrGgqCmmqeEoCo9yahVKLiYZviGCfZGr";
    RecyclerView recyclerView;
    AIUIChatAdapter adapter;

    List<ChatMessage> messages=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        setTitle("AI对话");
        AIManager.getInstance().init(getApplication());
        AIManager.getInstance().setCallback(this);

        TTSManager.getInstance().init(getApplication(),Baidu_appId,Baidu_appKey,Baidu_secretKey);
        TTSManager.getInstance().setTTSListener(this);

        WakeupManager.getInstance().init(getApplication(),Baidu_appId,Baidu_appKey,Baidu_secretKey);
        WakeupManager.getInstance().start();
        WakeupManager.getInstance().setWakeupListener(new WakeupListener() {
            @Override
            public void onWakeup(String word) {
                LogUtils.e(" onWakeup:"+word);
                TTSManager.getInstance().startSpeaking("主人请说");
            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter= new AIUIChatAdapter();

        recyclerView.setAdapter(adapter);


        adapter.setNewData(messages);


    }
    public void startClick(View view){
        WakeupManager.getInstance().stop();//先停止唤醒，不然会占用录音
        AIManager.getInstance().startSpeak();//开始讲话录音
    }
    public void stopClick(View view){
        AIManager.getInstance().stopSpeak();
    }


    @Override
    public void onRecordComplete() {
        //用户说话结束。可以开启唤醒了
        Log.e("TAG","用户说话结束。可以开启唤醒了 ");
        WakeupManager.getInstance().start();
    }

    @Override
    public void onResponse(String answer, JSONObject rawData) {
        if(!TextUtils.isEmpty(answer)){
            AIManager.getInstance().fakeAIUIText(answer);
            TTSManager.getInstance().startSpeaking(answer);
        }
    }

    @Override
    public void onAfterResponse(JSONObject rawData) {

    }
    @Override
    public void addChatMessage(final AIChatMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addData(new ChatMessage(message));
                recyclerView.scrollToPosition(adapter.getData().size()-1);

                Log.e("TAG"," ---  now size:"+adapter.getData().size() +"  "+messages.size());
            }
        });
    }

    @Override
    public void updateChatMessage(final AIChatMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(message.getContent())){
                    adapter.notifyItemChanged(adapter.getData().size()-1);
                }
            }
        });
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(float progress) {

    }

    @Override
    public void onSpeakCompleted() {
        //TTS语音播报结束
       String text= TTSManager.getInstance().getText();
       if(text!=null&&text.equals("主人请说")){
           //可以开始录音了
           startClick(null);
       }
    }
}
