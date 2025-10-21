**Отчет по практической работе №5**

**Цель работы**

Целью работы было изучение и практическое применение библиотеки Retrofit для работы с REST API. В рамках работы требовалось реализовать получение списка сущностей, их отображение, а также отправку запросов на обновление данных. Дополнительно требовалось интегрировать библиотеку для загрузки изображений из сети.

**1. Реализация работы с API (Retrofit)**

Для демонстрации работы с сетью в приложении была создана вкладка "Дела". Весь функционал реализован с соблюдением ранее построенной архитектуры MVVM и разделения на слои.

Приложение отправляет асинхронный GET-запрос к публичному API https://jsonplaceholder.typicode.com/todos, получает список задач (todos) в формате JSON и отображает их с помощью RecyclerView. Для парсинга (десериализации) JSON-ответов в Java-объекты (Todo) используется конвертер Gson.
<img width="974" height="464" alt="image" src="https://github.com/user-attachments/assets/d316c42d-69e8-4346-9945-cbc8a0e28ef0" />

Приложение также реализует обновление данных на сервере. При изменении состояния CheckBox в любом из элементов списка, отправляется асинхронный PUT-запрос на сервер для обновления статуса completed соответствующей задачи. Это демонстрирует полный цикл взаимодействия с REST API: получение и обновление данных. Вся логика инкапсулирована в TodoRepositoryImpl и TodoViewModel.

**Листинг 1: data/todo/TodoApi.java**

Интерфейс Retrofit, который описывает эндпоинты API. Он содержит два метода: getTodos() для получения списка (GET-запрос) и updateTodo() для обновления задачи (PUT-запрос).
```
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

Листинг 2: domain/todo/Todo.java
POJO-класс (модель данных), который полностью соответствует структуре JSON-объекта, получаемого от сервера. Gson использует этот класс для автоматической десериализации.
package ru.mirea.tenyutinmm.domain.todo;
public class Todo {
    public int userId;
    public int id;
    public String title;
    public boolean completed;
}
```
**Листинг 3: presentation/TodoViewModel.java**

ViewModel для экрана "Дела". Она запрашивает данные у репозитория и управляет состоянием UI (список дел, статус загрузки, ошибки) через LiveData. Также содержит метод updateTodoStatus для обработки действий пользователя.
```
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
```
**2. Загрузка изображений (Glide)**

Хотя в задании упоминается Picasso, контрольное задание разрешает использование Glide или Coil. В данном проекте для загрузки изображений из сети используется библиотека Glide, так как она уже была интегрирована для отображения аватаров и изображений котиков.

На вкладке "Дела" для каждого элемента списка асинхронно загружается и отображается аватар пользователя. URL изображения генерируется на основе userId задачи. Glide обеспечивает:
•	Асинхронную загрузку в фоновом потоке.
•	Автоматическое кэширование изображений в памяти и на диске.
•	Отображение изображения-заглушки (placeholder) на время загрузки.
•	Применение трансформаций (в данном случае, circleCropTransform для создания круглых аватаров).

Это демонстрирует эффективную интеграцию загрузки изображений в RecyclerView, где производительность и управление памятью имеют решающее значение.
<img width="974" height="594" alt="image" src="https://github.com/user-attachments/assets/d99e7a4f-581e-44da-a357-47028e33fb5c" />

**Листинг 4: presentation/TodoAdapter.java** 

Фрагмент кода из onBindViewHolder адаптера, демонстрирующий использование Glide для загрузки и отображения аватара.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import ru.mirea.tenyutinmm.domain.todo.Todo;
import ru.mirea.tenyutinmm.lesson9.R;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todos = new ArrayList<>();
    private final OnTodoCheckedChangeListener listener;

    public interface OnTodoCheckedChangeListener {
        void onCheckedChanged(Todo todo, boolean isChecked);
    }

    public TodoAdapter(OnTodoCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(todos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userAvatar;
        private final TextView todoTitle;
        private final CheckBox completedCheckBox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.iv_user_avatar);
            todoTitle = itemView.findViewById(R.id.tv_todo_title);
            completedCheckBox = itemView.findViewById(R.id.cb_completed);
        }

        public void bind(final Todo todo, final OnTodoCheckedChangeListener listener) {
            todoTitle.setText(todo.title);
            String avatarUrl = "https://i.pravatar.cc/150?u=" + todo.userId;
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(userAvatar);

            completedCheckBox.setOnCheckedChangeListener(null);
            completedCheckBox.setChecked(todo.completed);

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    listener.onCheckedChanged(todo, isChecked);
                }
            });
        }
    }
}
```

**3. Контрольное задание (интеграция в существующее приложение)**

Контрольное задание требовало реализовать получение сущностей из сети с помощью Retrofit и отображение изображений в существующем приложении.
Эта задача была полностью выполнена в рамках нашего проекта:
•	Получение сущностей с Retrofit: Реализовано на вкладках "Погода" (API wttr.in), "Котики" (API thecatapi.com) и "Дела" (API jsonplaceholder.typicode.com).
•	Обработка ошибок: Во всех ViewModel и репозиториях предусмотрены колбэки onError, которые передают сообщения об ошибках в UI, где они отображаются с помощью Toast.
•	Отображение изображений: Реализовано с помощью Glide на вкладках "Котики", "Дела" и "Профиль", что полностью соответствует требованию задания использовать "Picasso, Coil или Glide".

Вся новая функциональность была гармонично интегрирована в существующую многомодульную MVVM-архитектуру, что демонстрирует глубокое понимание принципов современной Android-разработки.
<img width="974" height="589" alt="image" src="https://github.com/user-attachments/assets/22cafbb0-d8fd-4bb9-af0e-b6273fad2c74" />
<img width="974" height="588" alt="image" src="https://github.com/user-attachments/assets/dce578ec-7a96-44e3-8adf-abdaf0fad2bf" />
<img width="974" height="460" alt="image" src="https://github.com/user-attachments/assets/61dc7cdb-8bbf-4043-84f4-d19d5f95abef" />
<img width="974" height="453" alt="image" src="https://github.com/user-attachments/assets/ad2061a9-3ac3-4914-9ca8-9c7581aa5575" />

В результате выполнения данной практической работы приложение было дополнено полноценным сетевым слоем, способным работать с различными REST API, обрабатывать ответы и ошибки, а также эффективно загружать и отображать изображения из интернета.




