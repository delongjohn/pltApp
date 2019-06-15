package pub.cdl.cameraalbumtest.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import pub.cdl.cameraalbumtest.control.SynConfig;
import pub.cdl.cameraalbumtest.control.Syntherizer;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/25 22:44
 * 4
 */
public class SpeakUtil extends Syntherizer {
    private static final int INIT = 1;

    private static final int RELEASE = 11;
    private HandlerThread hThread;
    private Handler tHandler;


    private static final String TAG = "SpeakUtil";

    public SpeakUtil(String threadName, Context context, SynConfig config, Handler mainHandler) {
        super(context, mainHandler);
        initThread(threadName);
        runInThread(INIT, config);
    }

    protected void initThread(String name) {
        hThread = new HandlerThread(name);
        hThread.start();

        tHandler = new Handler(hThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case INIT:
                        SynConfig config = (SynConfig) msg.obj;
                        boolean isOk = init(config);
                        if (isOk) {
                            Log.i(TAG, "nonBlock init ok");
                        } else {
                            Log.i(TAG, "nonBlock failed");
                        }
                        break;
                    case RELEASE:
                        SpeakUtil.super.release();
                        if (Build.VERSION.SDK_INT < 18) {
                            getLooper().quit();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void release() {
        runInThread(RELEASE);
        if (Build.VERSION.SDK_INT >= 18) {
            hThread.quitSafely();
        }
    }

    private void runInThread(int action, Object object) {
        Message message = Message.obtain();
        message.what = action;
        message.obj = object;
        tHandler.sendMessage(message);
    }

    private void runInThread(int action) {
        runInThread(action, null);
    }
}
