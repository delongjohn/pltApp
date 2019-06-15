package pub.cdl.cameraalbumtest.bean;

import com.baidu.tts.client.SpeechSynthesizer;

import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/8 0:37
 * 4
 */
public class VoiceConfig {
    private static String SPEAKER = "0";
    private static String VOLUME = "9";
    private static String SPEED = "5";
    private static String PITCH = "5";

    public static String getSPEAKER() {
        return SPEAKER;
    }

    public static void setSPEAKER(String SPEAKER) {
        VoiceConfig.SPEAKER = SPEAKER;
    }

    public static String getVOLUME() {
        return VOLUME;
    }

    public static void setVOLUME(String VOLUME) {
        VoiceConfig.VOLUME = VOLUME;
    }

    public static String getSPEED() {
        return SPEED;
    }

    public static void setSPEED(String SPEED) {
        VoiceConfig.SPEED = SPEED;
    }

    public static String getPITCH() {
        return PITCH;
    }

    public static void setPITCH(String PITCH) {
        VoiceConfig.PITCH = PITCH;
    }

    public static void getParams(Map<String, String> params) {
        params.put(SpeechSynthesizer.PARAM_SPEAKER, SPEAKER);
        params.put(SpeechSynthesizer.PARAM_VOLUME, VOLUME);
        params.put(SpeechSynthesizer.PARAM_SPEED, SPEED);
        params.put(SpeechSynthesizer.PARAM_PITCH, PITCH);
    }
}
