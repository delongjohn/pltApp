package pub.cdl.cameraalbumtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImgTest extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_test);

        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("img");

        imageView = findViewById(R.id.test_img);
        imageView.setImageBitmap(bitmap);
    }
}
