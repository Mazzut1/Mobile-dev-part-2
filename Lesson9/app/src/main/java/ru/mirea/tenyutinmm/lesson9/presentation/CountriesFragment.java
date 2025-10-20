package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.tenyutinmm.lesson9.R;

public class CountriesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("my_number_student", 24);

            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.list_container, CountriesListFragment.class, bundle)
                    .add(R.id.detail_container, CountryDetailFragment.class, null)
                    .commit();
        }
    }
}