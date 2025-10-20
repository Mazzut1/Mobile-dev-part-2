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
import androidx.lifecycle.ViewModelProvider;
import ru.mirea.tenyutinmm.lesson9.R;

public class WeatherFragment extends Fragment {

    private WeatherViewModel viewModel;
    private TextView temperatureTextView, descriptionTextView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(WeatherViewModel.class);

        temperatureTextView = view.findViewById(R.id.tv_temperature);
        descriptionTextView = view.findViewById(R.id.tv_description);
        progressBar = view.findViewById(R.id.progressBar);

        viewModel.weatherLiveData.observe(getViewLifecycleOwner(), weather -> {
            temperatureTextView.setText(weather.temperature);
            descriptionTextView.setText(weather.description);
        });

        viewModel.errorLiveData.observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.loadWeather();
    }
}