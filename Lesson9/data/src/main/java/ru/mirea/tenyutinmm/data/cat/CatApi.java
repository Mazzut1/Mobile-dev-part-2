package ru.mirea.tenyutinmm.data.cat;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CatApi {
    @GET("v1/images/search")
    Call<List<CatImageResponse>> getRandomCatImages(@Query("limit") int limit);
}