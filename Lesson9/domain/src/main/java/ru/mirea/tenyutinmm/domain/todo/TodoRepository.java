package ru.mirea.tenyutinmm.domain.todo;

import java.util.List;

public interface TodoRepository {
    void getTodos(GeneralCallback<List<Todo>> callback);
    void updateTodo(Todo todo, GeneralCallback<Todo> callback);

    interface GeneralCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}