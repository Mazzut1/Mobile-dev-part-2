package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ru.mirea.tenyutinmm.data.book.BookRepositoryImpl;
import ru.mirea.tenyutinmm.data.cat.CatRepositoryImpl;
import ru.mirea.tenyutinmm.data.todo.TodoRepositoryImpl;
import ru.mirea.tenyutinmm.data.weather.WeatherRepositoryImpl;
import ru.mirea.tenyutinmm.domain.book.BookRepository;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;
import ru.mirea.tenyutinmm.domain.todo.TodoRepository;
import ru.mirea.tenyutinmm.domain.weather.WeatherRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BooksViewModel.class)) {
            BookRepository bookRepository = new BookRepositoryImpl(context);
            return (T) new BooksViewModel(bookRepository);
        } else if (modelClass.isAssignableFrom(CatsViewModel.class)) {
            CatRepository catRepository = new CatRepositoryImpl(context);
            return (T) new CatsViewModel(catRepository);
        } else if (modelClass.isAssignableFrom(WeatherViewModel.class)) {
            WeatherRepository weatherRepository = new WeatherRepositoryImpl();
            return (T) new WeatherViewModel(weatherRepository);
        }
        else if (modelClass.isAssignableFrom(TodoViewModel.class)) {
            TodoRepository todoRepository = new TodoRepositoryImpl();
            return (T) new TodoViewModel(todoRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}