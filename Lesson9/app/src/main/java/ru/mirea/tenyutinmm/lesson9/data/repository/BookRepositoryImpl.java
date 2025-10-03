package ru.mirea.tenyutinmm.lesson9.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import ru.mirea.tenyutinmm.lesson9.domain.model.Book;
import ru.mirea.tenyutinmm.lesson9.domain.repository.BookRepository;

public class BookRepositoryImpl implements BookRepository {
    private static final String PREFS_NAME = "BookPrefs";
    private static final String KEY_BOOK_TITLE = "bookTitle";
    private static final String KEY_BOOK_AUTHOR = "bookAuthor";
    private final SharedPreferences sharedPreferences;

    public BookRepositoryImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean saveBook(Book book) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BOOK_TITLE, book.getTitle());
        editor.putString(KEY_BOOK_AUTHOR, book.getAuthor());
        return editor.commit();
    }

    @Override
    public Book getBook() {
        String title = sharedPreferences.getString(KEY_BOOK_TITLE, "Название не найдено");
        String author = sharedPreferences.getString(KEY_BOOK_AUTHOR, "Автор не найден");
        return new Book(title, author);
    }
}
