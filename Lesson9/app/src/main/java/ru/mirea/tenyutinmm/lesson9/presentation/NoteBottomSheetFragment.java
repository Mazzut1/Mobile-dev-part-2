package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import ru.mirea.tenyutinmm.lesson9.R;

public class NoteBottomSheetFragment extends BottomSheetDialogFragment {

    private CountriesSharedViewModel sharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireParentFragment()).get(CountriesSharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText noteEditText = view.findViewById(R.id.et_note);
        Button sendButton = view.findViewById(R.id.btn_send_note);

        sendButton.setOnClickListener(v -> {
            String noteText = noteEditText.getText().toString();
            sharedViewModel.saveNote(noteText);
            dismiss();
        });
    }
}