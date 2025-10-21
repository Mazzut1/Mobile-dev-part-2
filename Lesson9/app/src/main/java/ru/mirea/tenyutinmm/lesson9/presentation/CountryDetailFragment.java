package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import ru.mirea.tenyutinmm.lesson9.R;

public class CountryDetailFragment extends Fragment {

    private CountriesSharedViewModel sharedViewModel;
    private TextView countryNameTextView, capitalTextView, noteResultTextView;
    private ImageView flagImageView;
    private Button addNoteButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireParentFragment()).get(CountriesSharedViewModel.class);
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
        capitalTextView = view.findViewById(R.id.tv_capital);
        noteResultTextView = view.findViewById(R.id.tv_note_result);
        flagImageView = view.findViewById(R.id.iv_flag);
        addNoteButton = view.findViewById(R.id.btn_add_note);

        sharedViewModel.getSelectedCountry().observe(getViewLifecycleOwner(), country -> {
            if (country != null) {
                countryNameTextView.setText(country.name);
                capitalTextView.setText("Столица: " + country.capital);
                addNoteButton.setVisibility(View.VISIBLE);

                GlideToVectorYou.justLoadImage(getActivity(),
                        android.net.Uri.parse(country.flagUrl), flagImageView);
            }
        });

        sharedViewModel.getCurrentNote().observe(getViewLifecycleOwner(), note -> {
            if (note != null && !note.isEmpty()) {
                noteResultTextView.setText("Ваша заметка: " + note);
            } else {
                noteResultTextView.setText("Заметок пока нет");
            }
        });

        addNoteButton.setOnClickListener(v -> {
            new NoteBottomSheetFragment().show(getParentFragmentManager(), "NoteBottomSheet");
        });
    }
}