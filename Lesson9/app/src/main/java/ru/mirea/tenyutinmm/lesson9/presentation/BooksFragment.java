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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ru.mirea.tenyutinmm.data.book.BookRepositoryImpl;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;
import ru.mirea.tenyutinmm.lesson9.R;

public class BooksFragment extends Fragment {

    private BookRepository bookRepository;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private EditText bookTitleEditText;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookRepository = new BookRepositoryImpl(getContext());

        recyclerView = view.findViewById(R.id.rv_books);
        bookTitleEditText = view.findViewById(R.id.et_book_title);
        saveButton = view.findViewById(R.id.btn_save_book);

        setupRecyclerView();

        saveButton.setOnClickListener(v -> {
            String title = bookTitleEditText.getText().toString().trim();
            if (!title.isEmpty()) {
                bookRepository.saveBook(new Book(title));
                bookTitleEditText.setText("");
                loadBooks();
                Toast.makeText(getContext(), "Книга сохранена", Toast.LENGTH_SHORT).show();
            }
        });

        loadBooks();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);
    }

    private void loadBooks() {
        bookRepository.getAllBooks(books -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    bookAdapter.setBooks(books);
                });
            }
        });
    }
}