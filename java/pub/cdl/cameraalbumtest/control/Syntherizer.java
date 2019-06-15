package pub.cdl.cameraalbumtest.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/26 20:43
 * 4
 */
public class Syntherizer {
    private static final int PRINT = 0;
    private static final int UI_CHANGE_INPUT_TEXT_SELECTION = 1;
    private static final int UI_CHANGE_SYNTHES_TEXT_SELECTION = 2;
    private static final int INIT_SUCCESS = 2;
    private static final String TAG = "Syntherizer";

    private static boolean isInit = false;
    private boolean isFileChecked = true;

    protected SpeechSynthesizer synthesizer;
    protected Context context;
    protected Handler mainHandler;

    public Syntherizer(Context context, Handler mainHandler) {
        if (isInit) {
            throw new RuntimeException("Before this, please release");
        }
        this.context = context;
        this.mainHandler = mainHandler;
        isInit = true;
    }

    public Syntherizer(Context context, SynConfig config, Handler mainHandler) {
        this(context, mainHandler);
    }

    protected boolean init(SynConfig config) {
        boolean isMix = config.getMode().equals(TtsMode.MIX);
        synthesizer = SpeechSynthesizer.getInstance();
        synthesizer.setContext(context);
        synthesizer.setSpeechSynthesizerListener(config.getListener());

        synthesizer.setAppId(config.getAppId());
        synthesizer.setApiKey(config.getAppKey(), config.getSecretKey());

        if (isMix) {
            AuthInfo authInfo = synthesizer.auth(config.getMode());
            if (!authInfo.isSuccess()) {
                String e = authInfo.getTtsError().getDetailMessage();
                Log.e(TAG, e);
                return false;
            } else {
                Log.d(TAG, "ojbk");
            }
        }

        setParams(config.getParams());
        int result = synthesizer.initTts(config.getMode());
        if (result != 0) {
            Log.e(TAG, "init failed");
            return false;
        }

        sendToUi(INIT_SUCCESS, "init ok");
        return true;
    }

    public int speak(String text) {
        Log.i(TAG, "speak text:" + text);
        return synthesizer.speak(text);
    }

    public int speak(String text, String callBack) {
        return synthesizer.speak(text, callBack);
    }

    public int batchSpeak(List<Pair<String, String>> texts) {
        List<SpeechSynthesizeBag> bags = new ArrayList<>();
        for (Pair<String, String> pair : texts) {
            SpeechSynthesizeBag synthesizeBag = new SpeechSynthesizeBag();
            synthesizeBag.setText(pair.first);
            if (pair.second != null) {
                synthesizeBag.setUtteranceId(pair.second);
            }
            bags.add(synthesizeBag);
        }
        return synthesizer.batchSpeak(bags);
    }

    public void setParams(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> i : params.entrySet()) {
                synthesizer.setParam(i.getKey(), i.getValue());
            }
        }
    }

    public int pause() {
        return synthesizer.pause();
    }

    public int resume() {
        return synthesizer.resume();
    }

    public int stop() {
        return synthesizer.stop();
    }

    public void setVolume(float leftV, float rightV) {
        synthesizer.setStereoVolume(leftV, rightV);
    }

    public void release() {
        synthesizer.stop();
        synthesizer.release();
        synthesizer = null;
        isInit = false;
    }

    protected void sendToUi(String message) {
        sendToUi(PRINT, message);
    }

    protected void sendToUi(int action, String message) {
        Log.i(TAG, message);
        Message msg = Message.obtain();
        msg.what = action;
        msg.obj = message + "\n";
        mainHandler.sendMessage(msg);
    }
}
