package pub.cdl.cameraalbumtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.googlecode.tesseract.android.TessBaseAPI;
import pub.cdl.cameraalbumtest.bean.DateText;
import pub.cdl.cameraalbumtest.bean.DicStatus;
import pub.cdl.cameraalbumtest.bean.Text;
import pub.cdl.cameraalbumtest.util.ResourceUtil;
import pub.cdl.cameraalbumtest.util.SqlHelper;
import pub.cdl.cameraalbumtest.value.Provide;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int SDK_VERSION = 24;
    private static final int SDK_VERSION_4_4 = 19;
    private static final String DOC = "com.android.providers.media.documents";
    private static final String COM_ANDROID_PROVIDERS_DOWNLOADS_DOCUMENTS = "com.android.providers.downloads.documents";
    private static final String PUBLIC_DOWNLOADS = "content://downloads/public_downloads";
    private static final String TYPE_CONTENT = "content";
    private static final String TYPE_FILE = "file";
    public static final int SQL_RETURN = 4;
    public static final int OCR_RETURN = 5;
    public static final int DIC_OK = 6;
    private SqlHelper helper;
    private Uri imageUri;
    private static TessBaseAPI baseAPI = new TessBaseAPI();
    private ListView listView;
    public static Handler mainHandler;
    private List<Text> textList;
    private ProgressBar progressBar;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, SearchPage.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SQL_RETURN:
                    textList = (List<Text>) msg.obj;
                    DateText dateText = new DateText(MainActivity.this, R.layout.text_item, textList);
                    listView.setAdapter(dateText);
                    listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                        Text text = textList.get(position);
                        Intent intent = new Intent(MainActivity.this, ShowPage.class);
                        intent.putExtra("words", text.getContent());
                        intent.putExtra("who", "main");
                        startActivity(intent);
                    });
                    break;
                case OCR_RETURN:
                    progressBar.setVisibility(View.GONE);
                    startShow((String) msg.obj);
                    break;
                case DIC_OK:
                    String s = (String) msg.obj;
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.execSQL("update Status set statu = 'yes' where statuname = '" + s + "'");
                    db.close();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take_photo:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= SDK_VERSION) {
                    imageUri = FileProvider.getUriForFile(this, "pub.cdl.fuck.fileProvider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.choose:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.settings:
                Intent intent1 = new Intent(MainActivity.this, SettingPage.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void startShow(String s) {
        insert(s);
        Intent intent = new Intent(MainActivity.this, ShowPage.class);
        intent.putExtra("words", s);
        intent.putExtra("who", "main");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        getOCRText(bitmap, false);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= SDK_VERSION_4_4) {
                        handleImageOn4(data);
                    } else {
                        handleImageB4(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageB4(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageOn4(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (DOC.equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if (COM_ANDROID_PROVIDERS_DOWNLOADS_DOCUMENTS.equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(PUBLIC_DOWNLOADS), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if (TYPE_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if (TYPE_FILE.equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "Fuck YOU", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.texts);

        mainHandler = new mHandler();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        progressBar = findViewById(R.id.main_p_bar);
        progressBar.setVisibility(View.GONE);

        helper = new SqlHelper(this);

        initPermission();

        preTess();
        baseAPI.init(Provide.TESS_DATA, "eng");

        buildList();
        loadDictionary();
    }

    private class DictionaryLoader implements Runnable{
        private String s;
        private File file;
        public DictionaryLoader(String s) {
            this.s = s;
            file = new File(Provide.TESS_DATA + Provide.TESS_DATA_LOCAL + "/" + s + ".txt");
        }

        @Override
        public void run() {
            if (file.exists()) {
                String nowName = SqlHelper.map.get(s);
                String sql = "insert into " + nowName + " (content, translate) values (?, ?)";
                SqlHelper helper = new SqlHelper(MainActivity.this);
                Message message = new Message();
                message.what = DIC_OK;
                SQLiteDatabase db = helper.getWritableDatabase();
                db.beginTransaction();
                SQLiteStatement statement = db.compileStatement(sql);
                try {
                    FileInputStream in = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    String[] result;
                    while ((line = reader.readLine()) != null) {
                        result = ResourceUtil.pattern.split(line);
                        if (result.length < 2) {
                            Log.e("readerLine", result[0]);
                            continue;
                        }
                        statement.bindString(1, result[0].toLowerCase());
                        statement.bindString(2, result[1]);
                        statement.execute();
                        statement.clearBindings();
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                    message.obj = nowName;
                    mainHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadDictionary() {
        String[] strings = {"eng_4", "eng_6", "eng_h"};
        Map<String, DicStatus> map = ResourceUtil.getDictionaryStatus(MainActivity.this);
        for (String s : strings) {
            String status = map.get(SqlHelper.map.get(s)).getIsLoaded();
            if (status != null && status.equals("no")) {
                Runnable runnable = new DictionaryLoader(s);
                threadPoolExecutor.execute(runnable);
            }
        }
    }

    private void buildList() {
       Runnable runnable =  () -> {
           SqlHelper mHelper = new SqlHelper(MainActivity.this);
           SQLiteDatabase db = mHelper.getWritableDatabase();
           List<Text> list = new ArrayList<>();
           Cursor cursor = db.query("Text", null, null, null, null, null, null);
           if (cursor.moveToFirst()) {
               do {
                   String time = cursor.getString(cursor.getColumnIndex("time"));
                   String content = cursor.getString(cursor.getColumnIndex("content"));
                   Text text = new Text();
                   text.setTime(time);
                   text.setContent(content);
                   list.add(text);
               } while (cursor.moveToNext());
           }
           Message message = new Message();
           message.obj = list;
           message.what = SQL_RETURN;
           mainHandler.sendMessage(message);
           cursor.close();
           db.close();
       };
       threadPoolExecutor.execute(runnable);
    }


    private void insert(String s) {
        Date date = new Date();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", s);
        values.put("time", date.getTime() + "");
        db.insert("Text", null, values);
        db.close();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            getOCRText(bitmap, true);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void preDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(Provide.TAG_MAIN, "PathError");
            }
        } else {
            Log.i(Provide.TAG_MAIN, "Created!");
        }
    }

    private void loadTessFile(String path) {
        try {
            String[] fileList = getAssets().list(path);
            for (String file : fileList) {
                String dataPath = Provide.TESS_DATA + path + "/" + file;
                if (!(new File(dataPath)).exists()) {
                    InputStream in = getAssets().open(path + "/" + file);
                    OutputStream out = new FileOutputStream(dataPath);
                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    out.close();

                }
            }
        } catch (IOException e) {
            Log.e(Provide.TAG_MAIN, "fuckTess" + e.toString());
        }
    }

    private void preTess() {
        try {
            preDir(Provide.TESS_DATA + Provide.TESS_DATA_LOCAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadTessFile(Provide.TESS_DATA_LOCAL);
    }

    private void getOCRText(final Bitmap bitmap, final boolean isParse) {
        Runnable i = () -> {
            Message message = new Message();
            message.what = OCR_RETURN;
            baseAPI.setImage(bitmap);
            String result = "empty";
            try {
                result = baseAPI.getUTF8Text();
            } catch (Exception e) {
                Log.e(Provide.TAG_MAIN, "Error");
            }
            message.obj = result;
            mainHandler.sendMessage(message);
        };
        threadPoolExecutor.execute(i);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buildList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildList();
    }

    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toDo = new ArrayList<>();

        for (String p : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, p)) {
                toDo.add(p);
            }
        }
        String[] tmp = new String[toDo.size()];
        if (!toDo.isEmpty()) {
            ActivityCompat.requestPermissions(this, toDo.toArray(tmp), 123);
        }
    }
}
