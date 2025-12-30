package ru.mirea.tenyutinmm.lesson9.presentation;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import ru.mirea.tenyutinmm.lesson9.R;

public class CatDetailFragment extends Fragment {

    private ImageView catImageView;
    private Button analyzeButton;
    private TextView resultTextView;

    private ImageClassifier imageClassifier;
    private Bitmap imageBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_cat_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catImageView = view.findViewById(R.id.iv_cat_detail);
        analyzeButton = view.findViewById(R.id.btn_analyze);
        resultTextView = view.findViewById(R.id.tv_result);

        imageClassifier = new ImageClassifier(requireContext());
        imageClassifier.initialize();

        String catUrl = null;
        if (getArguments() != null) {
            catUrl = getArguments().getString("cat_url");
        }

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
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> resultTextView.setText("Результат: " + result));
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Изображение еще не загружено", Toast.LENGTH_SHORT).show();
            }
        });
    }
}