**Отчет по практической работе №3**

**Цель работы** Основной целью данной работы была модификация слоя представления (app) для соответствия архитектурному паттерну MVVM. Это включало в себя декомпозицию Fragment на две составляющие: View (за отображение) и ViewModel (за бизнес-логику и управление состоянием). Для обеспечения реактивного обновления UI и решения проблемы сохранения состояния при повороте экрана была внедрена библиотека LiveData.

**1. Внедрение MVVM и LiveData**

Для каждого функционального экрана ("Книги", "Котики", "Погода" и т.д.) был создан свой класс ViewModel, наследуемый от androidx.lifecycle.ViewModel. Вся логика взаимодействия с репозиториями из слоя domain была перенесена из Фрагментов во ViewModel.

Для корректной инициализации ViewModel с передачей зависимостей (таких как репозитории, которым нужен Context) был создан единый класс ViewModelFactory. Эта фабрика отвечает за создание всех ViewModel в приложении, что позволяет изолировать их от Android-компонентов и следовать принципам инверсии управления.
<img width="974" height="433" alt="image" src="https://github.com/user-attachments/assets/7b024fcc-6bfa-439e-96be-bb00265a0ef5" />
**Фрагменты (MyLibraryFragment, CatsFragment и др.) теперь не содержат бизнес-логики. Они только:**
1.	Инициализируют свою ViewModel через ViewModelProvider и нашу фабрику.
2.	Подписываются на изменения LiveData, предоставляемых ViewModel.
3.	Передают действия пользователя (например, клики по кнопкам) в методы ViewModel.


Такой подход делает код Фрагментов значительно чище, а всю логику — легко тестируемой и независимой от UI.
<img width="974" height="442" alt="image" src="https://github.com/user-attachments/assets/428f2aee-1aef-4821-971d-47cf45ed4645" />
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mirea.tenyutinmm.lesson9.R;

public class MyLibraryFragment extends Fragment {

    private MyLibraryViewModel viewModel;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private EditText bookTitleEditText;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(MyLibraryViewModel.class);

        recyclerView = view.findViewById(R.id.rv_books);
        bookTitleEditText = view.findViewById(R.id.et_book_title);
        saveButton = view.findViewById(R.id.btn_save_book);

        setupRecyclerView();

        saveButton.setOnClickListener(v -> {
            String title = bookTitleEditText.getText().toString();
            viewModel.saveBook(title);
            bookTitleEditText.setText("");
            Toast.makeText(getContext(), "Книга сохранена", Toast.LENGTH_SHORT).show();
        });

        viewModel.booksLiveData.observe(getViewLifecycleOwner(), books -> {
            bookAdapter.setBooks(books);
        });

        viewModel.loadBooks();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);
    }
}
```
<img width="974" height="433" alt="image" src="https://github.com/user-attachments/assets/1e659452-cd43-496f-a6bc-bc4d97a701fe" />

Благодаря ViewModel и LiveData, состояние экрана теперь сохраняется при повороте устройства. ViewModel "переживает" пересоздание Фрагмента, а LiveData немедленно отдает новому экземпляру Фрагмента последние актуальные данные, избегая повторных загрузок из сети или базы данных.
<img width="974" height="419" alt="image" src="https://github.com/user-attachments/assets/4d578c10-6461-4f54-860e-64fb1f203b4d" />

**2. Контрольное задание: Изучение MediatorLiveData**

В рамках контрольного задания требовалось изучить и применить MediatorLiveData. Этот компонент был реализован в CatsViewModel для координации нескольких источников данных.
MediatorLiveData используется для объединения двух состояний:
•	_catsSource: LiveData, содержащая сам список котиков, полученный из репозитория.
•	_isLoadingSource: LiveData, хранящая флаг состояния загрузки (true/false).

MediatorLiveData (_catsLiveData) подписывается на _catsSource. Как только из репозитория приходят новые данные, Mediator не только обновляет свое значение (передавая список котиков в UI), но и выполняет побочный эффект — устанавливает _isLoadingSource в false, убирая с экрана ProgressBar.

Это демонстрирует основную мощь MediatorLiveData — возможность реагировать на изменения в одних LiveData и вызывать изменения в других, создавая сложные цепочки реактивной логики.

Ниже приведены листинги ключевых файлов, демонстрирующих реализацию MVVM.

**Листинг 1: presentation/CatsViewModel.java**
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;

public class CatsViewModel extends ViewModel {

    private final CatRepository catRepository;

    private final MutableLiveData<List<Cat>> catsSource = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingSource = new MutableLiveData<>();
    private final MutableLiveData<String> errorSource = new MutableLiveData<>();

    public final LiveData<Boolean> isLoading = isLoadingSource;
    public final LiveData<String> error = errorSource;

    private final MediatorLiveData<List<Cat>> catsLiveData = new MediatorLiveData<>();
    public LiveData<List<Cat>> getCatsLiveData() {
        return catsLiveData;
    }

    public CatsViewModel(CatRepository catRepository) {
        this.catRepository = catRepository;
        catsLiveData.addSource(catsSource, cats -> {
            catsLiveData.setValue(cats);
            isLoadingSource.setValue(false);
        });
    }

    public void loadCats() {
        isLoadingSource.setValue(true);
        catRepository.getCats(new CatRepository.CatsCallback() {
            @Override
            public void onSuccess(List<Cat> cats) {
                catsSource.postValue(cats);
            }

            @Override
            public void onError(String message) {
                errorSource.postValue(message);
                isLoadingSource.postValue(false);
            }
        });
    }
}
```
**Листинг 2: presentation/CatsFragment.java**

Фрагмент, который теперь полностью управляется CatsViewModel. Он только подписывается на LiveData и отображает данные, не содержа бизнес-логики.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import ru.mirea.tenyutinmm.lesson9.R;

public class CatsFragment extends Fragment {

    private CatsViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CatAdapter catAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(CatsViewModel.class);

        recyclerView = view.findViewById(R.id.rv_cats);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();

        viewModel.getCatsLiveData().observe(getViewLifecycleOwner(), cats -> {
            catAdapter.setCats(cats);
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        if (viewModel.getCatsLiveData().getValue() == null) {
            viewModel.loadCats();
        }
    }

    private void setupRecyclerView() {
        catAdapter = new CatAdapter(cat -> {
            Intent intent = new Intent(getActivity(), CatDetailActivity.class);
            intent.putExtra("cat_url", cat.url);
            startActivity(intent);
        });
        recyclerView.setAdapter(catAdapter);
    }
}
```
**Листинг 3: presentation/ViewModelFactory.java**

Единая фабрика, отвечающая за создание всех ViewModel в приложении и внедрение в них необходимых зависимостей (репозиториев). Это позволяет ViewModel оставаться независимыми от Android Context.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.tenyutinmm.data.book.BookRepositoryImpl;
import ru.mirea.tenyutinmm.data.cat.CatRepositoryImpl;
import ru.mirea.tenyutinmm.data.country.CountryRepositoryImpl;
import ru.mirea.tenyutinmm.data.todo.TodoRepositoryImpl;
import ru.mirea.tenyutinmm.data.weather.WeatherRepositoryImpl;
import ru.mirea.tenyutinmm.domain.book.BookRepository;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;
import ru.mirea.tenyutinmm.domain.country.CountryRepository;
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
        if (modelClass.isAssignableFrom(MyLibraryViewModel.class)) {
            BookRepository bookRepository = new BookRepositoryImpl(context);
            return (T) new MyLibraryViewModel(bookRepository);
        }
        else if (modelClass.isAssignableFrom(WeatherViewModel.class)) {
            WeatherRepository weatherRepository = new WeatherRepositoryImpl();
            return (T) new WeatherViewModel(weatherRepository);
        } else if (modelClass.isAssignableFrom(CatsViewModel.class)) {
            CatRepository catRepository = new CatRepositoryImpl(context);
            return (T) new CatsViewModel(catRepository);
        } else if (modelClass.isAssignableFrom(TodoViewModel.class)) {
            TodoRepository todoRepository = new TodoRepositoryImpl();
            return (T) new TodoViewModel(todoRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
```

В результате выполнения данной практической работы приложение было успешно переведено на архитектуру MVVM. Вся бизнес-логика и управление состоянием вынесены во ViewModel, а Fragment отвечает только за отображение. Использование LiveData и MediatorLiveData обеспечивает реактивное и отказоустойчивое обновление UI.

**Работу выполни Тенютин М.М БСБО-09-22**








