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
        this.weatherApi = RetrofitClient.createClient("https://api.open-meteo.com/").create(WeatherApi.class);
    }

    @Override
    public void getWeather(String city, WeatherCallback callback) {
        weatherApi.getWeather().enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().currentWeather != null) {
                    double temp = response.body().currentWeather.temperature;
                    int code = response.body().currentWeather.weatherCode;

                    String temperature = temp + " °C";
                    String description = decodeWeatherCode(code);

                    Weather weather = new Weather(temperature, description);
                    callback.onSuccess(weather);
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

    private String decodeWeatherCode(int code) {
        switch (code) {
            case 0: return "Ясно";
            case 1:
            case 2:
            case 3: return "Облачно";
            case 45:
            case 48: return "Туман";
            case 51:
            case 53:
            case 55: return "Морось";
            case 61:
            case 63:
            case 65: return "Дождь";
            case 71:
            case 73:
            case 75: return "Снег";
            case 95:
            case 96:
            case 99: return "Гроза";
            default: return "Переменная облачность";
        }
    }
}