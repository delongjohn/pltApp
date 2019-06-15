package pub.cdl.cameraalbumtest.service;

import android.util.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import pub.cdl.cameraalbumtest.SettingPage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/18 20:45
 * 4
 */
public class WordFilter {
    private static int level = 2;
    private static Iterable<CSVRecord> csvRecords;
    private final static Set<String> cus = getCommonlyUsedWord();

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        WordFilter.level = level;
    }

    public static Set<String> getWordSet(List<String> strings, String isOn) {
        Set<String> wordSet = new HashSet<>();
        Set<String> returnSet = new HashSet<>();
        int num = level * 200;
        if (SettingPage.YES.equals(isOn) && (level != 0)) {
            for (CSVRecord record : csvRecords) {
                if (Integer.valueOf(record.get("num")) <= num) {
                    wordSet.add(record.get("word"));
                }
            }

            for (String str : strings) {
                if ((!cus.contains(str)) && (!wordSet.contains(str))) {
                    returnSet.add(str);
                }
            }
        } else {
            for (String str : strings) {
                returnSet.add(str);
            }
        }

        return returnSet;
    }

    public static Set<String> getCommonlyUsedWord() {
        Set<String> returnSet = new HashSet<>();
        int sawAbleStart = 32;
        int sawAbleEnd = 127;
        for (int i = sawAbleStart; i < sawAbleEnd; ++i) {
            returnSet.add(Character.toString((char)i));
        }
        String[] wordUnit = {
                "am", "is", "are", "was", "were", "been", "being",
                "him", "his", "himself",
                "has", "had",
                "me", "my", "mine", "myself",
                "her", "hers", "herself",
                "us", "our", "ourselves",
                "you", "your", "yours", "yourself", "yourselves",
                "do", "doing", "does", "did", "done",
                "made", "making",
                "goes", "going",
                "taking",
                "came", "coming"
        };
        for (String one : wordUnit) {
            returnSet.add(one);
        }
        return returnSet;
    }

    public static void loadWordFilter(File file) {
        String[] headers = {"num", "word"};
        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers).withSkipHeaderRecord();
        try {
            Reader reader = new FileReader(file);
            csvRecords = format.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
