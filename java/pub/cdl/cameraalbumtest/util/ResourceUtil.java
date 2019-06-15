package pub.cdl.cameraalbumtest.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import com.baidu.tts.client.SpeechSynthesizer;
import org.apache.commons.lang3.StringUtils;
import pub.cdl.cameraalbumtest.SettingPage;
import pub.cdl.cameraalbumtest.bean.DicStatus;
import pub.cdl.cameraalbumtest.bean.VoiceConfig;
import pub.cdl.cameraalbumtest.bean.Word;
import pub.cdl.cameraalbumtest.value.Provide;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/11 0:08
 * 4
 */
public class ResourceUtil {
    public static Pattern pattern = Pattern.compile("\\s+");

    public static Map<String, String> getParams(Context context) {
        Map<String, String> params = new HashMap<>();
        VoiceConfig.getParams(params);
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        OfflineResource offlineResource = createResource(context, OfflineResource.VOICE_FEMALE);
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
        return params;
    }

    private static OfflineResource createResource(Context context, String voiceType) {
        OfflineResource resource = null;
        try {
            resource = new OfflineResource(context, voiceType);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("tts", "createFailed");
        }
        return resource;
    }

    public static void loadOne(File file, Context context) {
        FileInputStream in;
        String line;
        String[] strings;
        String fileName = file.getName().split(".")[0];
        //SqlHelper helper = new SqlHelper(context, "PltStore.db", null, 5);
        //ContentValues values = new ContentValues();
        //SQLiteDatabase db = helper.getWritableDatabase();
        int i = 0;
        try {
            in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null && i < 10) {
                i++;
                strings = pattern.split(line, 2);
                Toast.makeText(context, strings[0] + "-" + strings[1] + "-" + fileName, Toast.LENGTH_SHORT).show();
                //values.put("word", strings[0]);
                //values.put("translate", strings[1]);
                //db.insert(fileName, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Word> inDb(Context context, Map<String, Word> w) {
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        Map<String, DicStatus> map = getDictionaryStatus(context);
        List<String> strings = new ArrayList<>();
        for (String s : w.keySet()) {
            strings.add("'" + s + "'");
        }
        for (Map.Entry<String, DicStatus> entry : map.entrySet()) {
            if (SettingPage.YES.equals(entry.getValue().getIsOn())) {
                String sql = "select * from " + entry.getKey() + " where content in (" + StringUtils.join(strings, ",") + ")";
                Cursor cursor = database.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    do {
                        String content = cursor.getString(cursor.getColumnIndex("content"));
                        String translate = cursor.getString(cursor.getColumnIndex("translate"));
                        StringBuilder builder = new StringBuilder();
                        Word word = w.get(content);
                        if (word == null) {
                            continue;
                        }
                        switch (entry.getKey()) {
                            case "Textf":
                                builder.append("cet4: ");
                                word.getTag().setFour();
                                break;
                            case "Texth":
                                builder.append("high school: ");
                                word.getTag().setHigh();
                                break;
                            case "Texts":
                                builder.append("cet6: ");
                                word.getTag().setSix();
                                break;
                            default:
                                break;
                        }
                        builder.append(translate);
                        builder.append('\n');
                        word.setTranslate(word.getTranslate() + builder.toString());
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        database.close();
        return w;
    }

    public static Map<String, DicStatus> getDictionaryStatus(Context context) {
        SqlHelper helper = new SqlHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("Status", null, null, null, null, null, null);
        Map<String, DicStatus> map = new HashMap<>(16);
        if (cursor.moveToFirst()) {
            do {
                DicStatus dicStatus = new DicStatus();
                String name = cursor.getString(cursor.getColumnIndex("statuname"));
                String statu = cursor.getString(cursor.getColumnIndex("statu"));
                String on = cursor.getString(cursor.getColumnIndex("able"));
                dicStatus.setName(name);
                dicStatus.setIsLoaded(statu);
                dicStatus.setIsOn(on);
                map.put(name, dicStatus);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return map;
    }

    public static void loadEng() {
        File file;
        String[] strings = {"eng_4", "eng_6", "eng_h"};
        for (String name : strings) {
            file = new File(Provide.TESS_DATA + Provide.TESS_DATA_LOCAL + "/" + name + ".txt");
            if (file.exists()) {
                System.out.println(file.getName());
            }
        }
    }
}
