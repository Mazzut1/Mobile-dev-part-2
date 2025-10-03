package ru.mirea.tenyutinmm.lesson9.domain.usecase;

import ru.mirea.tenyutinmm.lesson9.domain.model.Book;
import ru.mirea.tenyutinmm.lesson9.domain.repository.BookRepository;

public class GetFavoriteBookUseCase {
    private final BookRepository bookRepository;
    public GetFavoriteBookUseCase(BookRepository bookRepository) { this.bookRepository = bookRepository; }
    public Book execute() { return bookRepository.getBook(); }
}
