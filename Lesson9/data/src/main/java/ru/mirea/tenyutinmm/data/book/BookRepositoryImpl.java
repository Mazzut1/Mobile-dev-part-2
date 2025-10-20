package ru.mirea.tenyutinmm.data.book;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.tenyutinmm.data.database.AppDatabase;
import ru.mirea.tenyutinmm.data.database.BookDao;
import ru.mirea.tenyutinmm.data.database.BookEntity;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;

public class BookRepositoryImpl implements BookRepository {
    private final BookDao bookDao;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public BookRepositoryImpl(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.bookDao = db.bookDao();
    }

    @Override
    public void saveBook(Book book) {
        databaseExecutor.execute(() -> {
            BookEntity bookEntity = new BookEntity();
            bookEntity.title = book.title;
            bookDao.insert(bookEntity);
        });
    }

    @Override
    public void getAllBooks(BookCallback callback) {
        databaseExecutor.execute(() -> {
            List<BookEntity> entities = bookDao.getAllBooks();
            List<Book> books = new ArrayList<>();
            for (BookEntity entity : entities) {
                books.add(new Book(entity.title));
            }
            callback.onBooksLoaded(books);
        });
    }
}