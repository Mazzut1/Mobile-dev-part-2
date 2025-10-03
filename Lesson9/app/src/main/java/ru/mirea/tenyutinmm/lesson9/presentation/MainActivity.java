package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.util.Log; //
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.tenyutinmm.lesson9.R;
import ru.mirea.tenyutinmm.lesson9.data.repository.BookRepositoryImpl;
import ru.mirea.tenyutinmm.lesson9.domain.model.Book;
import ru.mirea.tenyutinmm.lesson9.domain.repository.BookRepository;
import ru.mirea.tenyutinmm.lesson9.domain.usecase.GetFavoriteBookUseCase;
import ru.mirea.tenyutinmm.lesson9.domain.usecase.SaveFavoriteBookUseCase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GetFavoriteBookUseCase getFavoriteBookUseCase;
    private SaveFavoriteBookUseCase saveFavoriteBookUseCase;
    private TextView resultTextView;
    private EditText titleEditText;
    private EditText authorEditText;
    private Button saveButton;
    private Button getButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity создана (onCreate)");

        BookRepository bookRepository = new BookRepositoryImpl(getApplicationContext());
        getFavoriteBookUseCase = new GetFavoriteBookUseCase(bookRepository);
        saveFavoriteBookUseCase = new SaveFavoriteBookUseCase(bookRepository);

        resultTextView = findViewById(R.id.tv_result);
        titleEditText = findViewById(R.id.et_book_title);
        authorEditText = findViewById(R.id.et_book_author);
        saveButton = findViewById(R.id.btn_save_book);
        getButton = findViewById(R.id.btn_get_book);

        saveButton.setOnClickListener(view -> saveBook());
        getButton.setOnClickListener(view -> getBook());
    }

    private void saveBook() {
        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        Log.d(TAG, "Нажата кнопка 'Сохранить'. Данные: " + title + ", " + author);

        Book book = new Book(title, author);
        boolean result = saveFavoriteBookUseCase.execute(book);
        resultTextView.setText(String.format("Сохранение успешно: %s", result));
    }

    private void getBook() {
        Log.d(TAG, "Нажата кнопка 'Отобразить'");
        Book book = getFavoriteBookUseCase.execute();
        Log.d(TAG, "Книга получена. Название: " + book.getTitle() + ", Автор: " + book.getAuthor());

        String bookInfo = String.format("«%s»\n%s", book.getTitle(), book.getAuthor());
        resultTextView.setText(bookInfo);
    }
}