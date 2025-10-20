package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.tenyutinmm.data.weather.WeatherRepositoryImpl;
import ru.mirea.tenyutinmm.domain.weather.Weather;
import ru.mirea.tenyutinmm.domain.weather.WeatherRepository;
import ru.mirea.tenyutinmm.lesson9.R;

public class WeatherFragment extends Fragment {

    private TextView temperatureTextView, descriptionTextView;
    private ProgressBar progressBar;
    private WeatherRepository weatherRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        temperatureTextView = view.findViewById(R.id.tv_temperature);
        descriptionTextView = view.findViewById(R.id.tv_description);
        progressBar = view.findViewById(R.id.progressBar);

        weatherRepository = new WeatherRepositoryImpl();
        loadWeather();
    }

    private void loadWeather() {
        progressBar.setVisibility(View.VISIBLE);
        weatherRepository.getWeather("Moscow", new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(Weather weather) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    temperatureTextView.setText(weather.temperature);
                    descriptionTextView.setText(weather.description);
                });
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}