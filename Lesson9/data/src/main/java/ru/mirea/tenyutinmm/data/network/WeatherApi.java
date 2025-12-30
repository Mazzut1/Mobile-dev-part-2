package ru.mirea.tenyutinmm.data.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherApi {
    @GET("v1/forecast?latitude=55.75&longitude=37.61&current_weather=true")
    Call<WeatherResponse> getWeather();
}