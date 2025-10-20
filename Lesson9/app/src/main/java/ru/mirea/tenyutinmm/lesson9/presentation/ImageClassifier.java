package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ImageClassifier {

    private static final String MODEL_FILE_NAME = "mobilenet_v1_1.0_224.tflite";
    private static final String TAG = "ImageClassifier";
    private final Context context;
    private org.tensorflow.lite.task.vision.classifier.ImageClassifier imageClassifier;

    public ImageClassifier(Context context) {
        this.context = context;
    }

    public void initialize() {
        try {
            org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions options =
                    org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions.builder()
                            .setBaseOptions(BaseOptions.builder().useGpu().build())
                            .setMaxResults(1)
                            .build();
            imageClassifier = org.tensorflow.lite.task.vision.classifier.ImageClassifier.createFromFileAndOptions(context, MODEL_FILE_NAME, options);
            Log.d(TAG, "TensorFlow Lite Classifier initialized.");

        } catch (IOException e) {
            Log.e(TAG, "Error initializing TensorFlow Lite Classifier.", e);
        }
    }

    public String classify(Bitmap bitmap) {
        if (imageClassifier == null) {
            Log.e(TAG, "Classifier not initialized.");
            return "Классификатор не инициализирован.";
        }

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        TensorImage tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap));

        List<Category> results = imageClassifier.classify(tensorImage).get(0).getCategories();

        if (results != null && !results.isEmpty()) {
            Category topResult = results.get(0);
            String label = topResult.getLabel();
            float score = topResult.getScore() * 100;

            return String.format(Locale.getDefault(), "%s (%.1f%%)", label, score);
        } else {
            return "Не удалось классифицировать.";
        }
    }
}