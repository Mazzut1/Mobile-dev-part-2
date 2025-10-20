package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import ru.mirea.tenyutinmm.domain.todo.Todo;
import ru.mirea.tenyutinmm.domain.todo.TodoRepository;

public class TodoViewModel extends ViewModel {
    private final TodoRepository todoRepository;

    private final MutableLiveData<List<Todo>> _todosLiveData = new MutableLiveData<>();
    public final LiveData<List<Todo>> todosLiveData = _todosLiveData;

    private final MutableLiveData<String> _errorLiveData = new MutableLiveData<>();
    public final LiveData<String> errorLiveData = _errorLiveData;

    private final MutableLiveData<Boolean> _loadingLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> loadingLiveData = _loadingLiveData;

    public TodoViewModel(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void loadTodos() {
        _loadingLiveData.postValue(true);
        todoRepository.getTodos(new TodoRepository.GeneralCallback<List<Todo>>() {
            @Override
            public void onSuccess(List<Todo> data) {
                _todosLiveData.postValue(data);
                _loadingLiveData.postValue(false);
            }

            @Override
            public void onError(String message) {
                _errorLiveData.postValue(message);
                _loadingLiveData.postValue(false);
            }
        });
    }

    public void updateTodoStatus(Todo todo, boolean isCompleted) {
        todo.completed = isCompleted;
        todoRepository.updateTodo(todo, new TodoRepository.GeneralCallback<Todo>() {
            @Override
            public void onSuccess(Todo data) {
                loadTodos();
            }

            @Override
            public void onError(String message) {
                _errorLiveData.postValue(message);
            }
        });
    }
}