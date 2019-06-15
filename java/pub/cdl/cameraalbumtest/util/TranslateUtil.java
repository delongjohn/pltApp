package pub.cdl.cameraalbumtest.util;

import android.os.Handler;
import android.os.Message;
import okhttp3.*;
import pub.cdl.cameraalbumtest.ShowPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/28 2:29
 * 4
 */
public class TranslateUtil {
    private static final String TRANS_API_HOST = "http://ck.johncdl.com";
    private static final String ID = "20190424000291227";
    private static final String KEY = "xbcM3mBUQF9OXKHaEPQp";
    private static final String FROM = "en";
    private static final String TO = "zh";

    public static void getTransResult(String query) {
        Request request = getRequest(query);
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = 4;
                message.obj = null;
                ShowPage.mainHandler.sendMessage(message);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.what = 3;
                message.obj = response.body().string();
                ShowPage.mainHandler.sendMessage(message);
            }
        });
    }

    public static Request getRequest(String q) {
        Map<String, String> params = buildParams(q);
        String url = HttpUtil.getUrlWithQueryString(TRANS_API_HOST, params);
        return new Request.Builder().url(url).build();
    }

    private static Map<String, String> buildParams(String query) {
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", FROM);
        params.put("to", TO);
        params.put("appid", ID);
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        String src = ID + query + salt + KEY;
        params.put("sign", MD5Util.md5(src));
        return params;
    }
}
