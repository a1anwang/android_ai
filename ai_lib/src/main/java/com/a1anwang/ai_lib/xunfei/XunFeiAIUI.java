package com.a1anwang.ai_lib.xunfei;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.a1anwang.ai_lib.AICallback;
import com.a1anwang.ai_lib.AIChatMessage;
import com.a1anwang.ai_lib.IAI;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a1anwang.com on 2019/8/15.
 */
public class XunFeiAIUI implements IAI {

    private static final String TAG = XunFeiAIUI.class.getSimpleName();
    private static final String KEY_SEMANTIC = "semantic";
    private static final String KEY_OPERATION = "operation";
    private static final String SLOTS = "slots";


    private volatile static XunFeiAIUI instance;

    private XunFeiAIUI() {
    }

    public static XunFeiAIUI getInstance() {
        if (instance == null) {
            synchronized (XunFeiAIUI.class) {
                if (instance == null) {
                    instance = new XunFeiAIUI();
                }
            }
        }
        return instance;
    }

    private Context context;
    //AIUI当前状态
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    //AIUI当前录音状态，避免连续两次startRecordAudio时的录音失败
    private boolean mAudioRecording = false;



    AIUIAgent mAgent;

    boolean messageAdded=true;//一条新的消息是否已经被添加，添加过之后都是update
    //记录自开始录音是否有vad前端点事件抛出
    private boolean mHasBOSBeforeEnd = false;
    //当前未结束的语音交互消息，更新语音消息的听写内容时使用
    private RawMessage mAppendVoiceMsg = null;

    //当前未结束的语音交互消息，更新语音消息的听写内容时使用
    private AIChatMessage aiChatVoiceMsg = null;



    //处理PGS听写(流式听写）的数组
    private String[] mIATPGSStack = new String[256];
    private List<String> mInterResultStack = new ArrayList<>();

    private boolean initialized;
    synchronized public void init(Context context){
        if(initialized){
            return;
        }
        this.context=context;
        //创建AIUIAgent
        String params=getAIUIParams();
        JSONObject mAIUIConfig = null;
        try {
            mAIUIConfig=new JSONObject(params);

        } catch (JSONException e) {

        }
        params=mAIUIConfig.toString();
        Log.e(TAG,"AIUI params:"+params);
        mAgent = AIUIAgent.createAgent(context,params,mAIUIListener);
        initialized=true;
    }
    public void fakeAIUIText(String text){
        if(!TextUtils.isEmpty(text)){
            addChatMessage(new AIChatMessage(AIChatMessage.MsgType.TEXT,false,text));
        }
    }
    @Override
    public void release() {
        if(mAgent!=null){
            mAgent.destroy();
            mAgent=null;
        }
        initialized=false;
    }
    private boolean speaking;
    /**
     * 用户开始讲话，录音
     */
    public void startSpeak(){
        resetMessage();
        speaking=true;
        sendWakeUp();//激活AIUI处于工作状态

        startRecord();
    }
    private void resetMessage(){
        if (mAppendVoiceMsg != null) {
            mAppendVoiceMsg = null;
            mInterResultStack.clear();
        }
        //清空PGS听写中间结果
        for (int index = 0; index < mIATPGSStack.length; index++) {
            mIATPGSStack[index] = null;
        }
        mAppendVoiceMsg = new RawMessage(RawMessage.FromType.USER,RawMessage.MsgType.Voice, new byte[]{});
        mAppendVoiceMsg.cacheContent = "";
        //语音消息msgData为录音时长
        mAppendVoiceMsg.msgData = ByteBuffer.allocate(4).putFloat(0).array();

        aiChatVoiceMsg=new AIChatMessage(AIChatMessage.MsgType.Voice,true,new String(mAppendVoiceMsg.msgData));
        messageAdded=false;
    }

    /**
     * 用户停止讲话
     */
    public void stopSpeak(){
        stopRecord();
        speaking=false;
    }

    private AICallback callback;
    @Override
    public void setCallback(AICallback callback) {
        this.callback=callback;
    }

    private void sendWakeUp(){
        if (mCurrentState != AIUIConstant.STATE_WORKING) {
            Log.e(TAG,"----send CMD_WAKEUP ");
            mAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
        }
    }
    /**
     * 开始录音
     */
    private void startRecord(){
        if (!mAudioRecording) {

            String params = "data_type=audio,sample_rate=16000";
            //流式识别
            params += ",dwa=wpgs";
            sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null));
            mAudioRecording = true;
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord(){
        if (mAudioRecording) {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
            mAudioRecording = false;
            if(callback!=null){
                callback.onRecordComplete();
            }
        }

    }
    /**
     * 发送AIUI消息
     *
     * @param message
     */
    private void sendMessage(AIUIMessage message) {
        if (mAgent != null) {
            mAgent.sendMessage(message);
        }
    }
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);

            JSONObject paramsJson = new JSONObject(params);

            params = paramsJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    private AIUIListener mAIUIListener=new AIUIListener() {
        @Override
        public void onEvent(AIUIEvent aiuiEvent) {
            switch (aiuiEvent.eventType) {
                case AIUIConstant.EVENT_STATE: {
                    Log.e("alan","---- EVENT_STATE :"+aiuiEvent.arg1);
                    mCurrentState = aiuiEvent.arg1;
                    if (AIUIConstant.STATE_IDLE == mCurrentState) {
                        // 闲置状态，AIUI未开启
                        Log.e(TAG,"闲置状态，AIUI未开启");
                    } else if (AIUIConstant.STATE_READY == mCurrentState) {
                        // AIUI已就绪，等待唤醒
                        Log.e(TAG,"AIUI已就绪，等待唤醒");
                    } else if (AIUIConstant.STATE_WORKING == mCurrentState) {
                        // AIUI工作中，可进行交互
                        Log.e(TAG,"AIUI工作中，可进行交互");
                    }

                }
                break;
                case AIUIConstant.EVENT_RESULT: {
                    //LogUtils.e("alan","---- EVENT_RESULT");
                    processResult(aiuiEvent);
                }
                break;

                case AIUIConstant.EVENT_VAD: {
                    Log.e("alan","---- EVENT_VAD");
                    processVADEvent(aiuiEvent);
                }
                break;

                case AIUIConstant.EVENT_ERROR: {
                    int errorCode = aiuiEvent.arg1;
                    Log.e("alan","---- EVENT_ERROR ："+errorCode);

                    processError(aiuiEvent);
                }
                break;

                case AIUIConstant.EVENT_WAKEUP: {


                }
                break;

                case AIUIConstant.EVENT_SLEEP: {
                    Log.e("alan","---- 休眠了");

                    stopRecord();


                }
                break;
                case AIUIConstant.EVENT_CONNECTED_TO_SERVER: {
                    Log.e("alan", "EVENT_CONNECTED_TO_SERVER:"+aiuiEvent.data.getString("uid"));


                }
                break;
                case AIUIConstant.EVENT_TTS: {

                    break;

                }
            }
        }
    };
    /**
     * 处理vad事件，音量更新
     * @param aiuiEvent
     */
    private void processVADEvent(AIUIEvent aiuiEvent) {
        if (aiuiEvent.arg1 == AIUIConstant.VAD_BOS) {
            mHasBOSBeforeEnd = true;
            if(!messageAdded){
                messageAdded=true;
                addChatMessage(aiChatVoiceMsg);
            }
        }
        if(aiuiEvent.eventType == AIUIConstant.EVENT_VAD) {
            if(aiuiEvent.arg1 == AIUIConstant.VAD_VOL) {
//                if(volumeListener!=null){
//                    volumeListener.onVolumeChanged(5000 + 8000 * aiuiEvent.arg2 / 100);
//                }
            }
        }

    }
    /**
     * 处理AIUI结果事件（听写结果和语义结果）
     *
     * @param event 结果事件
     */
    private void processResult(AIUIEvent event) {
        try {
            JSONObject bizParamJson = new JSONObject(event.info);
            JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
            JSONObject params = data.getJSONObject("params");
            JSONObject content = data.getJSONArray("content").getJSONObject(0);

            long rspTime = event.data.getLong("eos_rslt", -1);  //响应时间
            String sub = params.optString("sub");

            if (content.has("cnt_id")  ) {
                String cnt_id = content.getString("cnt_id");
                Log .e("AIUI","--- 111 data:"+sub +" "+new String(event.data.getByteArray(cnt_id), "utf-8"));
                JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                Log.e("ALAN","-- sub:"+sub+" cntJson:"+cntJson.toString());
                if(sub.equals("nlp")){
                    //讯飞技能的回复
                    if(callback!=null){
                        JSONObject semanticObj=cntJson.optJSONObject("intent");
                        if (semanticObj != null) {
                            SemanticResult semanticResult = initSemanticResult(semanticObj.toString());
                            callback.onResponse(semanticResult.answer,cntJson);
                        }

                    }
                }else if(sub.equals("tpp")){
                    //后处理回复
                    if(callback!=null){
                        callback.onAfterResponse(cntJson);
                    }
                }else if(sub.equals("iat")){
                    //语音听写结果
                     processIATResult(cntJson);
                }
            }
        } catch (Throwable e) {
            Log.e("ALAN","--- ???????:"+e.toString());
        }
    }
    private SemanticResult initSemanticResult(String semanticContent){
        // 解析语义结果
        JSONObject semanticResult;
        SemanticResult mSemanticResult = new SemanticResult();
        try {
            semanticResult = new JSONObject(semanticContent);
            mSemanticResult.rc = semanticResult.optInt("rc");
            if (mSemanticResult.rc == 4) {
                mSemanticResult.answer="这个问题我无法回答";
                mSemanticResult.service = Constant.SERVICE_UNKNOWN;
            } else if (mSemanticResult.rc == 1) {
                mSemanticResult.service = semanticResult.optString("service");
                mSemanticResult.answer = "语义错误";
            } else {
                mSemanticResult.service = semanticResult.optString("service");
                mSemanticResult.answer = semanticResult.optJSONObject("answer") == null ?
                        "已为您完成操作" : semanticResult.optJSONObject("answer").optString("text");
                // 兼容3.1和4.0的语义结果，通过判断结果最外层的operation字段
                boolean isAIUI3_0 = semanticResult.has(KEY_OPERATION);
                if (isAIUI3_0) {
                    //将3.1语义格式的语义转换成4.1
                    JSONObject semantic = semanticResult.optJSONObject(KEY_SEMANTIC);
                    if (semantic != null) {
                        JSONObject slots = semantic.optJSONObject(SLOTS);
                        JSONArray fakeSlots = new JSONArray();
                        Iterator<String> keys = slots.keys();
                        while (keys.hasNext()) {
                            JSONObject item = new JSONObject();
                            String name = keys.next();
                            item.put("name", name);
                            item.put("value", slots.get(name));

                            fakeSlots.put(item);
                        }

                        semantic.put(SLOTS, fakeSlots);
                        semantic.put("intent", semanticResult.optString(KEY_OPERATION));
                        mSemanticResult.semantic = semantic;
                    }
                } else {
                    mSemanticResult.semantic = semanticResult.optJSONArray(KEY_SEMANTIC) == null ?
                            semanticResult.optJSONObject(KEY_SEMANTIC) :
                            semanticResult.optJSONArray(KEY_SEMANTIC).optJSONObject(0);
                }
                mSemanticResult.answer = mSemanticResult.answer.replaceAll("\\[[a-zA-Z0-9]{2}\\]", "");
                mSemanticResult.data = semanticResult.optJSONObject("data");
                if(mSemanticResult.data == null) {
                    mSemanticResult.data = new JSONObject();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG," json e:"+e.toString());
            mSemanticResult.rc = 4;
            mSemanticResult.service = Constant.SERVICE_UNKNOWN;

        }
        return mSemanticResult;
    }
    /**
     * 解析听写结果更新当前语音消息的听写内容
     */
    private void processIATResult(JSONObject cntJson) throws JSONException {
        if (mAppendVoiceMsg == null) return;

        JSONObject text = cntJson.optJSONObject("text");
        // 解析拼接此次听写结果
        StringBuilder iatText = new StringBuilder();
        JSONArray words = text.optJSONArray("ws");
        boolean lastResult = text.optBoolean("ls");
        for (int index = 0; index < words.length(); index++) {
            JSONArray charWord = words.optJSONObject(index).optJSONArray("cw");
            for (int cIndex = 0; cIndex < charWord.length(); cIndex++) {
                iatText.append(charWord.optJSONObject(cIndex).opt("w"));

            }
        }

        Log.e(TAG," iatText:"+iatText.toString());

        String voiceIAT = "";
        String pgsMode = text.optString("pgs");
        //非PGS模式结果
        if (TextUtils.isEmpty(pgsMode)) {
            if (TextUtils.isEmpty(iatText)) return;

            //和上一次结果进行拼接
            if (!TextUtils.isEmpty(mAppendVoiceMsg.cacheContent)) {
                voiceIAT = mAppendVoiceMsg.cacheContent;//+ "\n";
            }
            voiceIAT += iatText;
        } else {
            int serialNumber = text.optInt("sn");
            mIATPGSStack[serialNumber] = iatText.toString();
            //pgs结果两种模式rpl和apd模式（替换和追加模式）
            if ("rpl".equals(pgsMode)) {
                //根据replace指定的range，清空stack中对应位置值
                JSONArray replaceRange = text.optJSONArray("rg");
                int start = replaceRange.getInt(0);
                int end = replaceRange.getInt(1);

                for (int index = start; index <= end; index++) {
                    mIATPGSStack[index] = null;
                }
            }

            StringBuilder PGSResult = new StringBuilder();
            //汇总stack经过操作后的剩余的有效结果信息
            for (int index = 0; index < mIATPGSStack.length; index++) {
                if (TextUtils.isEmpty(mIATPGSStack[index])) continue;

//                if(!TextUtils.isEmpty(PGSResult.toString())) PGSResult.append("\n");
                PGSResult.append(mIATPGSStack[index]);
                //如果是最后一条听写结果，则清空stack便于下次使用
                if (lastResult) {
                    mIATPGSStack[index] = null;
                }
            }
            voiceIAT = join(mInterResultStack) + PGSResult.toString();

            if (lastResult) {
                mInterResultStack.add(PGSResult.toString());
            }
        }
        Log.e(TAG," voiceIAT:"+voiceIAT);
        if (!TextUtils.isEmpty(voiceIAT)) {
            mAppendVoiceMsg.cacheContent = voiceIAT;
            aiChatVoiceMsg.setContent(voiceIAT);
            Log.e(TAG," updateChatMessage 0000:"+aiChatVoiceMsg.getContent());
            updateChatMessage(aiChatVoiceMsg);
        }
    }
    private String join(List<String> data) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < data.size(); index++) {
            builder.append(data.get(index));
        }

        return builder.toString();
    }
    /**
     * 错误处理
     *
     * 在聊天对话消息中添加错误消息提示
     * @param aiuiEvent
     */
    private void processError(AIUIEvent aiuiEvent) {
        //向消息列表中添加AIUI错误消息
        int errorCode = aiuiEvent.arg1;
        //AIUI网络异常，不影响交互，可以作为排查问题的线索和依据
        if (errorCode >= 10200 && errorCode <= 10215) {
            //Timber.e("AIUI Network Warning %d, Don't Panic", errorCode);
            return;
        }

        Log.e("alan","processError: "+errorCode);
        switch (errorCode) {
            case 10120: {
                String text="网络有点问题 :(";


                break;
            }

            case 20006: {
                String text="录音启动失败 :(，请检查是否有其他应用占用录音";

                break;
            }

            default: {
                String text=aiuiEvent.arg1 + " 错误";

            }
        }
    }
    /**
     * 新增聊天消息
     * @param
     */
    public void addChatMessage(AIChatMessage chatMessage) {
         if(callback!=null){
             callback.addChatMessage(chatMessage);
         }
    }

    /**
     * 更新聊天记录
     * @param message
     */
    private void  updateChatMessage(AIChatMessage message) {
        if(callback!=null){
            callback.updateChatMessage(message);
        }
    }
}
