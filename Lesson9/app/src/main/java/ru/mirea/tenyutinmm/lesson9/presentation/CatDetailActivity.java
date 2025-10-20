package ru.mirea.tenyutinmm.lesson9.presentation;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import ru.mirea.tenyutinmm.lesson9.R;

public class CatDetailActivity extends AppCompatActivity {

    private ImageView catImageView;
    private Button analyzeButton;
    private TextView resultTextView;


    private ImageClassifier imageClassifier;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);

        catImageView = findViewById(R.id.iv_cat_detail);
        analyzeButton = findViewById(R.id.btn_analyze);
        resultTextView = findViewById(R.id.tv_result);

        imageClassifier = new ImageClassifier(this);
        imageClassifier.initialize();

        String catUrl = getIntent().getStringExtra("cat_url");
        if (catUrl != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(catUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageBitmap = resource;
                            catImageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        analyzeButton.setOnClickListener(v -> {
            if (imageBitmap != null) {
                new Thread(() -> {
                    final String result = imageClassifier.classify(imageBitmap);
                    runOnUiThread(() -> resultTextView.setText("Результат: " + result));
                }).start();
            } else {
                Toast.makeText(this, "Изображение еще не загружено", Toast.LENGTH_SHORT).show();
            }
        });
    }
}