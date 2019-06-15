package pub.cdl.cameraalbumtest.util;

import pub.cdl.cameraalbumtest.bean.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/15 0:10
 * 4
 */
public class EngWordsUtil {
    private static final Map<String, Trie> map;

    static {
        map = new HashMap<>();
    }

    public static Map<String, Trie> getMap() {
        return map;
    }
}
