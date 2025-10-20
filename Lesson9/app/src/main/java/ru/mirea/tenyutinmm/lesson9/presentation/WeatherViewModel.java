package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.mirea.tenyutinmm.domain.weather.Weather;
import ru.mirea.tenyutinmm.domain.weather.WeatherRepository;

public class WeatherViewModel extends ViewModel {

    private final WeatherRepository weatherRepository;

    private final MutableLiveData<Weather> _weatherLiveData = new MutableLiveData<>();
    public final LiveData<Weather> weatherLiveData = _weatherLiveData;

    private final MutableLiveData<String> _errorLiveData = new MutableLiveData<>();
    public final LiveData<String> errorLiveData = _errorLiveData;

    private final MutableLiveData<Boolean> _loadingLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> loadingLiveData = _loadingLiveData;

    public WeatherViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void loadWeather() {
        _loadingLiveData.setValue(true);
        weatherRepository.getWeather("Moscow", new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(Weather weather) {
                _weatherLiveData.postValue(weather);
                _loadingLiveData.postValue(false);
            }

            @Override
            public void onError(String message) {
                _errorLiveData.postValue(message);
                _loadingLiveData.postValue(false);
            }
        });
    }
}
