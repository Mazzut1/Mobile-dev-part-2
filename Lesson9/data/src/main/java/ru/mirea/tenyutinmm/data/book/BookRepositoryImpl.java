package ru.mirea.tenyutinmm.data.book;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;

public class BookRepositoryImpl implements BookRepository {

    // --- ЗАГЛУШКА ДАННЫХ (STUB) ---
    private final ArrayList<Book> favoriteBooks = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BookRepositoryImpl(Context context) {
        // Начальные данные для заглушки
        favoriteBooks.add(new Book("Мастер и Маргарита"));
        favoriteBooks.add(new Book("Дюна"));
        favoriteBooks.add(new Book("1984"));
    }

    @Override
    public void saveBook(Book book) {
        // Эмулируем асинхронную работу, как будто сохраняем в базу
        executor.execute(() -> {
            favoriteBooks.add(0, book);
        });
    }

    @Override
    public void getAllBooks(BookCallback callback) {
        // Эмулируем асинхронную работу, как будто читаем из базы
        executor.execute(() -> {
            // Возвращаем копию списка, чтобы избежать проблем с многопоточностью
            callback.onBooksLoaded(new ArrayList<>(favoriteBooks));
        });
    }
}