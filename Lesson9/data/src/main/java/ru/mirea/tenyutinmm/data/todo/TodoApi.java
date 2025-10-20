package ru.mirea.tenyutinmm.data.todo;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.mirea.tenyutinmm.domain.todo.Todo;

public interface TodoApi {
    @GET("todos")
    Call<List<Todo>> getTodos();

    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") int todoId, @Body Todo todo);
}