package ru.mirea.tenyutinmm.data.book;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;

public class BookRepositoryImpl implements BookRepository {

    private final ArrayList<Book> favoriteBooks = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BookRepositoryImpl(Context context) {
        favoriteBooks.add(new Book("Мастер и Маргарита"));
        favoriteBooks.add(new Book("Дюна"));
        favoriteBooks.add(new Book("1984"));
    }

    @Override
    public void saveBook(Book book) {
        executor.execute(() -> {
            favoriteBooks.add(0, book);
        });
    }

    @Override
    public void getAllBooks(BookCallback callback) {
        executor.execute(() -> {
            callback.onBooksLoaded(new ArrayList<>(favoriteBooks));
        });
    }
}