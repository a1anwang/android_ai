package com.a1anwang.ai_core.tts;

/**
 * Created by a1anwang.com on 2019/8/13.
 */
public enum VoiceName {
    XunFei_XiaoYan("xiaoyan"),
    XunFei_XuJiu("aisjiuxu"),
    XunFei_XiaoPing("aisxping"),
    XunFei_XiaoJing("aisjinger"),
    XunFei_XuXiaoBao("aisbabyxu"),
    XunFei_ChongChong("x_chongchong"),
    Du_XiaoMei("0"),
    Du_XiaoYu("1"),
    Du_XiaoYao("3"),
    Du_YaYa("4"),
    Du_BoWen("106"),
    Du_XiaoTong("110"),
    Du_XiaoMeng("111"),
    Du_Miduo("103"),
    Du_XiaoJiao("5"),
    ;

    private String name;

    private VoiceName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
}
