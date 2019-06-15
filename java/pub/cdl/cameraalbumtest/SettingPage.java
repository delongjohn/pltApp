package pub.cdl.cameraalbumtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import pub.cdl.cameraalbumtest.bean.DicStatus;
import pub.cdl.cameraalbumtest.service.WordFilter;
import pub.cdl.cameraalbumtest.util.ResourceUtil;
import pub.cdl.cameraalbumtest.util.SqlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingPage extends AppCompatActivity implements View.OnClickListener{
    private Button button;
    private Button about;
    private Button settings;
    private Button dic;
    private Button filter;
    private Switch aSwitch;
    private SqlHelper helper;
    public static final String YES = "yes";
    public static final String NO = "no";
    private static String chooseTemp = WordFilter.getLevel() + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        helper = new SqlHelper(this);

        button = findViewById(R.id.clean);
        about = findViewById(R.id.about);
        dic = findViewById(R.id.setDic);
        filter = findViewById(R.id.filter);
        settings = findViewById(R.id.settings);
        aSwitch = findViewById(R.id.switch_bt);

        button.setOnClickListener(this);
        about.setOnClickListener(this);
        settings.setOnClickListener(this);
        dic.setOnClickListener(this);
        filter.setOnClickListener(this);

        aSwitch.setChecked(true);
        aSwitch.setOnCheckedChangeListener((a, b) -> {
            if (b) {
                WordFilter.setLevel(2);
            } else {
                WordFilter.setLevel(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean:
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingPage.this);
                builder.setTitle("清除历史");
                builder.setMessage("确认删除所以历史记录? (不可恢复)");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.execSQL("delete from Text");
                        Toast.makeText(SettingPage.this, "已经清空", Toast.LENGTH_SHORT).show();
                        db.close();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
            case R.id.about:
                Intent intent = new Intent(SettingPage.this, AboutPage.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Intent intent1 = new Intent(SettingPage.this, ConfigPage.class);
                startActivity(intent1);
                break;
            case R.id.setDic:
                Map<String, String> stringMap = new HashMap<>(3);
                stringMap.put("CET4", "Textf");
                stringMap.put("CET6", "Texts");
                stringMap.put("HighSchool", "Texth");
                Map<String, DicStatus> map = ResourceUtil.getDictionaryStatus(SettingPage.this);
                String[] dics = {"CET4", "CET6", "HighSchool"};
                List<Boolean> list = new ArrayList<>();
                for (String s : dics) {
                    list.add(!NO.equals(map.get(stringMap.get(s)).getIsOn()));
                }
                boolean[] isOn = {list.get(0), list.get(1), list.get(2)};
                AlertDialog.Builder dicBuilder = new AlertDialog.Builder(SettingPage.this);
                dicBuilder.setTitle("标记设置");
                dicBuilder.setMultiChoiceItems(dics, isOn, (a, b, c) -> {

                });
                dicBuilder.show();
                break;
            case R.id.filter:
                final String[] items = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                AlertDialog.Builder filterBt = new AlertDialog.Builder(SettingPage.this);
                filterBt.setTitle("过滤等级");
                filterBt.setCancelable(false);
                filterBt.setSingleChoiceItems(items, WordFilter.getLevel(),
                        (DialogInterface dialog, int which) -> chooseTemp = items[which]
                );
                filterBt.setPositiveButton("OK",
                        (DialogInterface d, int which) -> {
                    int i = Integer.valueOf(chooseTemp);
                    if (i == 0) {
                        aSwitch.setChecked(false);
                    }
                    WordFilter.setLevel(i);
                }
                );
                filterBt.setNegativeButton("Cancel",
                        (DialogInterface d, int which) -> {

                });
                filterBt.show();
                break;
            default:
                break;
        }
    }
}
