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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ImageClassifier {

    private static final String MODEL_FILE_NAME = "mobilenet_v1_1.0_224.tflite";
    private static final String TAG = "ImageClassifier";
    private final Context context;
    private org.tensorflow.lite.task.vision.classifier.ImageClassifier imageClassifier;

    private final Map<String, String> translationMap = new HashMap<>();

    public ImageClassifier(Context context) {
        this.context = context;
        initializeTranslations();
    }

    private void initializeTranslations() {
        translationMap.put("tabby, tabby cat", "Полосатый кот");
        translationMap.put("tiger cat", "Тигровый кот");
        translationMap.put("Persian cat", "Персидская кошка");
        translationMap.put("Siamese cat, Siamese", "Сиамская кошка");
        translationMap.put("Egyptian cat", "Египетская кошка");
        translationMap.put("lynx, catamount", "Рысь");
        translationMap.put("Chihuahua", "Чихуахуа");
        translationMap.put("golden retriever", "Золотистый ретривер");
        translationMap.put("German shepherd, German shepherd dog", "Немецкая овчарка");
    }

    private String translateLabel(String englishLabel) {
        return translationMap.getOrDefault(englishLabel, englishLabel);
    }

    public void initialize() {
        try {
            org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions options =
                    org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions.builder()
                            .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
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
            return "Классификатор не инициализирован.";
        }

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        TensorImage tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap));
        List<Category> results = imageClassifier.classify(tensorImage).get(0).getCategories();

        if (results != null && !results.isEmpty()) {
            Category topResult = results.get(0);
            String englishLabel = topResult.getLabel();

            String russianLabel = translateLabel(englishLabel);
            float score = topResult.getScore() * 100;

            return String.format(Locale.getDefault(), "%s (%.1f%%)", russianLabel, score);
        } else {
            return "Не удалось классифицировать.";
        }
    }
}