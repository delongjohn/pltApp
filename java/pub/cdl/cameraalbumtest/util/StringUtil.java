package pub.cdl.cameraalbumtest.util;

import android.content.Context;
import pub.cdl.cameraalbumtest.bean.Word;
import pub.cdl.cameraalbumtest.service.WordFilter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/25 1:30
 * 4
 */
public class StringUtil {
    private static Pattern pattern = Pattern.compile("\\W");
    private static String t = null;
    public static String getTranslate() {
        String tr = t;
        t = null;
        return tr;
    }

    public static String[] getStrings(String s) {
        return pattern.split(s);
    }

    public static Map<String, Word> getWordMap(Context context, String s) {
        return getWordMap(context, getStrings(s), "yes");
    }

    private static Map<String, Word> getWordMap(Context context, String[] strings, String st) {
        List<String> stringList = new ArrayList<>();
        for (String r : strings) {
            if (!r.isEmpty()) {
                stringList.add(r.trim().toLowerCase());
            }
        }
        Map<String, Word> wordMap = new HashMap<>(16);
        Map<String, Word> w;
        Set<String> stringSet = WordFilter.getWordSet(stringList, st);

        for (String a : stringList) {
            if (stringSet.contains(a)) {
                if (wordMap.get(a) == null) {
                    Word word = new Word();
                    word.setWord(a);
                    wordMap.put(a, word);
                } else {
                    wordMap.get(a).setCount();
                }
            }
        }
        w = ResourceUtil.inDb(context, wordMap);
        return w.size() == 0 ? null : w;
    }

    public static String giveT(Map<String, Word> w) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Word> entry : w.entrySet()) {
            if (entry.getValue().getTag().isNormal()) {
                builder.append(entry.getKey());
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public static Map<String, Word> getSearchMap(Context context, String s) {
        String[] now;
        now = s.replace("\\", "/").split("/");
        return getWordMap(context, now, "no");
    }
}
