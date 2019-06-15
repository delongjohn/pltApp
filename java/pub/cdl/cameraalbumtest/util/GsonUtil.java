package pub.cdl.cameraalbumtest.util;

import com.google.gson.Gson;
import pub.cdl.cameraalbumtest.bean.Translate;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/30 0:41
 * 4
 */
public class GsonUtil {
    private static Gson gson = new Gson();
    public static Translate doParse(String json) {
        return gson.fromJson(json, Translate.class);
    }
}
