package ru.mirea.tenyutinmm.data.weather;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.tenyutinmm.data.network.RetrofitClient;
import ru.mirea.tenyutinmm.data.network.WeatherApi;
import ru.mirea.tenyutinmm.data.network.WeatherResponse;
import ru.mirea.tenyutinmm.domain.weather.Weather;
import ru.mirea.tenyutinmm.domain.weather.WeatherRepository;

public class WeatherRepositoryImpl implements WeatherRepository {

    private final WeatherApi weatherApi;

    public WeatherRepositoryImpl() {
        this.weatherApi = RetrofitClient.createClient("https://wttr.in/").create(WeatherApi.class);
    }

    @Override
    public void getWeather(String city, WeatherCallback callback) {
        weatherApi.getWeather(city, "j1").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse body = response.body();
                    try {
                        String temperature = body.currentCondition.get(0).tempC + " °C";
                        String description = body.currentCondition.get(0).weatherDesc.get(0).value;
                        Weather weather = new Weather(temperature, description);
                        callback.onSuccess(weather);
                    } catch (Exception e) {
                        callback.onError("Failed to parse weather data");
                    }
                } else {
                    callback.onError("Failed to get weather data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}