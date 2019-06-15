package pub.cdl.cameraalbumtest;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import pub.cdl.cameraalbumtest.bean.*;
import pub.cdl.cameraalbumtest.control.SynConfig;
import pub.cdl.cameraalbumtest.control.Syntherizer;
import pub.cdl.cameraalbumtest.listener.UiMessageListener;
import pub.cdl.cameraalbumtest.service.WordFilter;
import pub.cdl.cameraalbumtest.util.*;
import pub.cdl.cameraalbumtest.value.Provide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowPage extends AppCompatActivity implements
        MainHandlerConstant {
    private Map<String, Word> wordMap;

    private ListView listView;

    private List<Word> list;

    protected String appId = "16120038";

    protected String appKey = "x1WvyI5XFFbIW0bF48gc1zVA";

    protected String secretKey = "c639oSBV43cZXbVcbZ5GXS3lvxjf3KCX";

    protected TtsMode ttsMode = TtsMode.MIX;

    public static Handler mainHandler;

    protected Syntherizer syntherizer;

    private ProgressBar progressBar;

    public static final String MAIN = "main";
    public static final String SEARCH = "search";

    private void initTts() {
        mainHandler = new MyHandler();
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);
        Map<String, String> params = ResourceUtil.getParams(ShowPage.this);
        SynConfig config = new SynConfig(appId, appKey, secretKey, ttsMode, params, listener);
        syntherizer = new SpeakUtil("showPage", ShowPage.this, config, mainHandler);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handle(msg);
        }
    }

    protected void handle(Message message) {
        int i = message.what;
        switch (i) {
            case INIT_SUCCESS:
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Word word = list.get(position);
                        speak(word.getWord());
                        Toast.makeText(ShowPage.this, word.getWord(), Toast.LENGTH_SHORT).show();
                    }
                });
                message.what = PRINT;
                break;
            case TR_OK:
                Translate result = GsonUtil.doParse((String) message.obj);
                if (result.getTrans_result() != null) {
                    Word w;
                    for (TrResult t : result.getTrans_result()) {
                        w = wordMap.get(t.getSrc());
                        if (w != null) {
                            w.setTranslate(t.getDst());
                        }
                    }
                    list = new ArrayList<>(wordMap.values());
                    WordAdapter adapter = new WordAdapter(ShowPage.this, R.layout.word_item, list);
                    listView.setAdapter(adapter);
                }
                progressBar.setVisibility(View.GONE);
                break;
            case TR_FAILD:
                Toast.makeText(ShowPage.this, "internet error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int result;
        switch (item.getItemId()) {
            case R.id.play:
                int i = 0;
                List<Pair<String, String>> texts = new ArrayList<>();
                for (Word word : list) {
                    texts.add(new Pair<>(word.getWord(), i + ""));
                    i++;
                }
                result = syntherizer.batchSpeak(texts);
                checkResult(result, "batchSpeak");
                break;
            case R.id.pause:
                result = syntherizer.pause();
                checkResult(result, "pause");
                break;
            case R.id.resume:
                result = syntherizer.resume();
                checkResult(result, "resume");
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        WordFilter.loadWordFilter(new File(Provide.TESS_DATA + Provide.TESS_DATA_LOCAL + "/eng_2000.csv"));

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar_show);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.list_view);

        Intent intent = getIntent();
        String s = intent.getStringExtra("words");
        String who = intent.getStringExtra("who");

        if (MAIN.equals(who)) {
            wordMap = StringUtil.getWordMap(ShowPage.this, s);
        } else if (SEARCH.equals(who)) {
            wordMap = StringUtil.getSearchMap(ShowPage.this, s);
        }

        list = new ArrayList<>(wordMap.values());

        String toRequest = StringUtil.giveT(wordMap);

        if (!toRequest.isEmpty()) {
            TranslateUtil.getTransResult(toRequest);
            progressBar.setVisibility(View.VISIBLE);
        }

        WordAdapter adapter = new WordAdapter(ShowPage.this, R.layout.word_item, list);
        listView.setAdapter(adapter);
        initTts();
    }

    private void speak(String text) {
        int result = syntherizer.speak(text);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            Log.e("result error", "error cod :" + result + " method" + method);
        }
    }

    @Override
    protected void onDestroy() {
        syntherizer.release();
        Log.i("release", "release ok");
        super.onDestroy();
    }
}
