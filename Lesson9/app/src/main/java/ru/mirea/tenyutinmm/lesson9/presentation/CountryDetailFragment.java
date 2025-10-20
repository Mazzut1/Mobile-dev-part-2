package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ru.mirea.tenyutinmm.lesson9.R;

public class CountryDetailFragment extends Fragment {

    private CountriesSharedViewModel sharedViewModel;
    private TextView countryNameTextView;
    private TextView noteResultTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireParentFragment()).get(CountriesSharedViewModel.class);

        getParentFragmentManager().setFragmentResultListener("noteRequestKey", this, (requestKey, bundle) -> {
            String result = bundle.getString("noteKey");
            noteResultTextView.setText("Ваша заметка: " + result);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countryNameTextView = view.findViewById(R.id.tv_country_name);
        noteResultTextView = view.findViewById(R.id.tv_note_result);
        Button addNoteButton = view.findViewById(R.id.btn_add_note);

        sharedViewModel.getSelectedCountry().observe(getViewLifecycleOwner(), country -> {
            countryNameTextView.setText(country);
        });

        addNoteButton.setOnClickListener(v -> {
            new NoteBottomSheetFragment().show(getParentFragmentManager(), "NoteBottomSheet");
        });
    }
}