package pub.cdl.cameraalbumtest.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/2 16:29
 * 4
 */
public class SqlHelper extends SQLiteOpenHelper {
    public final static Map<String, String> map;
    static {
        map = new HashMap<>();
        map.put("eng_4", "Textf");
        map.put("eng_6", "Texts");
        map.put("eng_h", "Texth");
    }
    private static final String name = "PltStore.db";
    private static final int v = 20;
    public static final String CREATE_TIME = "create table Text (" +
            "id integer primary key autoincrement," +
            "content TEXT," +
            "time TEXT)";

    public static final String CREATE_four = "create table Textf (" +
            "id integer primary key autoincrement," +
            "content TEXT," +
            "translate TEXT)";

    public static final String CREATE_six = "create table Texts (" +
            "id integer primary key autoincrement," +
            "content TEXT," +
            "translate TEXT)";

    public static final String CREATE_high = "create table Texth (" +
            "id integer primary key autoincrement," +
            "content TEXT," +
            "translate TEXT)";

    public static final String STATUS_TABLE = "create table Status (" +
            "id integer primary key autoincrement," +
            "statuname TEXT," +
            "able TEXT," +
            "statu TEXT)";

    private Context mainContext;

    public SqlHelper(Context context) {
        super(context, name, null, v);
        mainContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TIME);
        db.execSQL(CREATE_four);
        db.execSQL(CREATE_six);
        db.execSQL(CREATE_high);
        db.execSQL(STATUS_TABLE);
        db.execSQL("insert into Status (statuname, able, statu) values ('Textf', 'yes', 'no')");
        db.execSQL("insert into Status (statuname, able, statu) values ('Texts', 'yes', 'no')");
        db.execSQL("insert into Status (statuname, able, statu) values ('Texth', 'no', 'no')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Text");
        db.execSQL("drop table if exists Texts");
        db.execSQL("drop table if exists Textf");
        db.execSQL("drop table if exists Texth");
        db.execSQL("drop table if exists Status");
        onCreate(db);
    }
}
