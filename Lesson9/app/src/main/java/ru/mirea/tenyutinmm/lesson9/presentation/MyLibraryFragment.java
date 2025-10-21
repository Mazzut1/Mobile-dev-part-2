package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mirea.tenyutinmm.lesson9.R;

public class MyLibraryFragment extends Fragment {

    private MyLibraryViewModel viewModel;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private EditText bookTitleEditText;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(MyLibraryViewModel.class);

        recyclerView = view.findViewById(R.id.rv_books);
        bookTitleEditText = view.findViewById(R.id.et_book_title);
        saveButton = view.findViewById(R.id.btn_save_book);

        setupRecyclerView();

        saveButton.setOnClickListener(v -> {
            String title = bookTitleEditText.getText().toString();
            viewModel.saveBook(title);
            bookTitleEditText.setText("");
            Toast.makeText(getContext(), "Книга сохранена", Toast.LENGTH_SHORT).show();
        });

        viewModel.booksLiveData.observe(getViewLifecycleOwner(), books -> {
            bookAdapter.setBooks(books);
        });

        viewModel.loadBooks();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);
    }
}