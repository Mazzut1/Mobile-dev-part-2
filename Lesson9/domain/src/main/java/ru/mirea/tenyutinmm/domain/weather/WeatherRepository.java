package ru.mirea.tenyutinmm.domain.weather;

public interface WeatherRepository {
    void getWeather(String city, WeatherCallback callback);

    interface WeatherCallback {
        void onSuccess(Weather weather);
        void onError(String message);
    }
}