package ru.mirea.tenyutinmm.data.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.tenyutinmm.domain.todo.Todo;
import ru.mirea.tenyutinmm.domain.todo.TodoRepository;

public class TodoRepositoryImpl implements TodoRepository {

    private final List<Todo> dummyTodoList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int nextId = 1;

    public TodoRepositoryImpl() {
        addTodo("Закончить 5-ю практическую работу", true);
        addTodo("Сходить в магазин за продуктами", false);
        addTodo("Позвонить родителям", false);
        addTodo("Почистить ноутбук от пыли", true);
        addTodo("Прочитать главу новой книги", false);
        addTodo("Спланировать выходные", false);
        addTodo("Записаться к врачу", true);
        addTodo("Полить цветы", false);
        addTodo("Ответить на рабочие письма", true);
        addTodo("Посмотреть новый фильм", false);
        addTodo("Выучить 10 новых слов на английском", false);
        addTodo("Сделать зарядку", true);
    }

    private void addTodo(String title, boolean completed) {
        Todo todo = new Todo();
        todo.id = nextId++;
        todo.userId = 1;
        todo.title = title;
        todo.completed = completed;
        dummyTodoList.add(todo);
    }

    @Override
    public void getTodos(GeneralCallback<List<Todo>> callback) {
        executor.execute(() -> {
            callback.onSuccess(new ArrayList<>(dummyTodoList));
        });
    }

    @Override
    public void updateTodo(Todo todoToUpdate, GeneralCallback<Todo> callback) {
        executor.execute(() -> {
            for (Todo todo : dummyTodoList) {
                if (todo.id == todoToUpdate.id) {
                    todo.completed = todoToUpdate.completed;
                    callback.onSuccess(todo);
                    return;
                }
            }
            callback.onError("Todo with id " + todoToUpdate.id + " not found.");
        });
    }
}