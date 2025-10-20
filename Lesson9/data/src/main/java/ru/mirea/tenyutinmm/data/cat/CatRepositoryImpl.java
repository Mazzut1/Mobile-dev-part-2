package ru.mirea.tenyutinmm.data.cat;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.tenyutinmm.data.database.AppDatabase;
import ru.mirea.tenyutinmm.data.database.CatDao;
import ru.mirea.tenyutinmm.data.database.CatEntity;
import ru.mirea.tenyutinmm.data.network.RetrofitClient;
import ru.mirea.tenyutinmm.data.cat.CatApi;
import ru.mirea.tenyutinmm.data.cat.CatImageResponse;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;

public class CatRepositoryImpl implements CatRepository {

    private final CatApi catApi;
    private final CatDao catDao;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public CatRepositoryImpl(Context context) {
        this.catApi = RetrofitClient.createClient("https://api.thecatapi.com/").create(CatApi.class);
        this.catDao = AppDatabase.getDatabase(context).catDao();
    }

    @Override
    public void getCats(CatsCallback callback) {
        databaseExecutor.execute(() -> {
            List<CatEntity> catEntities = catDao.getAll();
            if (catEntities != null && !catEntities.isEmpty()) {
                callback.onSuccess(mapEntitiesToDomain(catEntities));
            } else {
                fetchCatsFromNetwork(callback);
            }
        });
    }

    private void fetchCatsFromNetwork(CatsCallback callback) {
        catApi.getRandomCatImages(20).enqueue(new Callback<List<CatImageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatImageResponse>> call, @NonNull Response<List<CatImageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CatImageResponse> networkCats = response.body();
                    saveCatsToDatabase(networkCats);
                    callback.onSuccess(mapResponsesToDomain(networkCats));
                } else {
                    callback.onError("Failed to load cats");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatImageResponse>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private void saveCatsToDatabase(List<CatImageResponse> networkCats) {
        databaseExecutor.execute(() -> {
            List<CatEntity> catEntities = new ArrayList<>();
            for (CatImageResponse response : networkCats) {
                CatEntity entity = new CatEntity();
                entity.id = response.id;
                entity.url = response.url;
                catEntities.add(entity);
            }
            catDao.insertAll(catEntities);
        });
    }

    private List<Cat> mapEntitiesToDomain(List<CatEntity> entities) {
        List<Cat> cats = new ArrayList<>();
        for (CatEntity entity : entities) {
            cats.add(new Cat(entity.id, entity.url));
        }
        return cats;
    }

    private List<Cat> mapResponsesToDomain(List<CatImageResponse> responses) {
        List<Cat> cats = new ArrayList<>();
        for (CatImageResponse response : responses) {
            cats.add(new Cat(response.id, response.url));
        }
        return cats;
    }
}