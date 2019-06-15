package pub.cdl.cameraalbumtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchPage extends AppCompatActivity implements MainHandlerConstant{
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        button = findViewById(R.id.se_bt);
        editText = findViewById(R.id.edit);

        button.setOnClickListener((View v) -> {
            Intent intent = new Intent(SearchPage.this, ShowPage.class);
            String w = editText.getText().toString();
            intent.putExtra("words", w);
            intent.putExtra("who", "search");
            startActivity(intent);
        });
    }
}
