package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;
import java.util.stream.Collectors;
import ru.mirea.tenyutinmm.data.country.CountryRepositoryImpl;
import ru.mirea.tenyutinmm.domain.country.Country;
import ru.mirea.tenyutinmm.domain.country.CountryRepository;
import ru.mirea.tenyutinmm.lesson9.R;

public class CountriesListFragment extends Fragment {

    private CountriesSharedViewModel sharedViewModel;
    private CountryRepository countryRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireParentFragment()).get(CountriesSharedViewModel.class);
        countryRepository = new CountryRepositoryImpl();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            int studentNumber = getArguments().getInt("my_number_student", 0);
            Log.d("CountriesListFragment", "Мой номер по списку: " + studentNumber);
        }

        ListView listView = view.findViewById(R.id.countries_list_view);
        List<Country> countries = countryRepository.getCountries();

        List<String> countryNames = countries.stream().map(c -> c.name).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, countryNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Country selectedCountry = countries.get(position);
            sharedViewModel.selectCountry(selectedCountry);
        });
    }
}