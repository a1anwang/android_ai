package com.a1anwang.ai_core.tts;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public class TTSConfig {


    private String voiceName;
    private int speed=-1;
    private int volume=-1;
    private int pitch=-1;

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getPitch() {
        return pitch;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public static class Builder {
        private TTSConfig mConfig;
        public Builder(){
            mConfig=new TTSConfig();
        }
        public Builder voiceName(String voiceName){
            mConfig.setVoiceName(voiceName);
            return this;
        }
        public Builder speed(int speed){
            mConfig.setSpeed(speed);
            return this;
        }
        public Builder volume(int volume){
            mConfig.setVolume(volume);
            return this;
        }
        public Builder pitch(int pitch){
            mConfig.setPitch(pitch);
            return this;
        }
        public TTSConfig build(){
            return mConfig;
        }
    }
}
