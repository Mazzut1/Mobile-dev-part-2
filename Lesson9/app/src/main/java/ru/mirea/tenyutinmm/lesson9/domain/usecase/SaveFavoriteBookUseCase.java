package ru.mirea.tenyutinmm.lesson9.domain.usecase;

import ru.mirea.tenyutinmm.lesson9.domain.model.Book;
import ru.mirea.tenyutinmm.lesson9.domain.repository.BookRepository;

public class SaveFavoriteBookUseCase {
    private final BookRepository bookRepository;
    public SaveFavoriteBookUseCase(BookRepository bookRepository) { this.bookRepository = bookRepository; }
    public boolean execute(Book book) {
        if (book.getTitle().isEmpty()) { return false; }
        return bookRepository.saveBook(book);
    }
}