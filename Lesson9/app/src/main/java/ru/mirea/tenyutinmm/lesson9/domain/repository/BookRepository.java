package ru.mirea.tenyutinmm.lesson9.domain.repository;

import ru.mirea.tenyutinmm.lesson9.domain.model.Book;

public interface BookRepository {
    boolean saveBook(Book book);
    Book getBook();
}