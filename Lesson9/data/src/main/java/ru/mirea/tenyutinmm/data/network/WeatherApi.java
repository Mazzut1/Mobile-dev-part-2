package ru.mirea.tenyutinmm.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/{city}")
    Call<WeatherResponse> getWeather(@Path("city") String city, @Query("format") String format);
}