package pub.cdl.cameraalbumtest.control;

import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/26 20:55
 * 4
 */
public class SynConfig {
    private String appId;
    private String appKey;
    private String secretKey;
    private TtsMode mode;
    private Map<String, String> params;
    private SpeechSynthesizerListener listener;

    private SynConfig() {}
    public SynConfig(String appId, String appKey, String secretKey, TtsMode mode, Map<String, String> params, SpeechSynthesizerListener listener) {
        this.appId =appId;
        this.appKey = appKey;
        this.secretKey = secretKey;
        this.mode = mode;
        this.params = params;
        this.listener = listener;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public TtsMode getMode() {
        return mode;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public SpeechSynthesizerListener getListener() {
        return listener;
    }
}
