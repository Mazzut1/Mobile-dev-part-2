package ru.mirea.tenyutinmm.domain.book;

import java.util.List;

public interface BookRepository {
    void saveBook(Book book);
    void getAllBooks(BookCallback callback);

    interface BookCallback {
        void onBooksLoaded(List<Book> books);
    }
}