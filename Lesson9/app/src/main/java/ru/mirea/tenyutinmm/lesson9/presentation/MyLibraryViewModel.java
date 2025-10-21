package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;

public class MyLibraryViewModel extends ViewModel {
    private final BookRepository bookRepository;

    private final MutableLiveData<List<Book>> _booksLiveData = new MutableLiveData<>();
    public final LiveData<List<Book>> booksLiveData = _booksLiveData;

    public MyLibraryViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void loadBooks() {
        bookRepository.getAllBooks(books -> _booksLiveData.postValue(books));
    }

    public void saveBook(String title) {
        if (title != null && !title.trim().isEmpty()) {
            Book book = new Book(title.trim());
            bookRepository.saveBook(book);
            loadBooks();
        }
    }
}