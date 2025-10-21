**Отчет по практической работе №4**

**Цель работы**

Целью работы было изучение и практическое применение основных способов отображения списков в Android: ScrollView, ListView и RecyclerView. Также в рамках контрольного задания требовалось реализовать отображение списка из "заглушки" (mock data) в репозитории с использованием архитектуры MVVM.

**1. ScrollView (Геометрическая прогрессия)**

Для демонстрации работы ScrollView в приложении была создана вкладка "Прогрессия". Пользовательский интерфейс реализован с помощью ScrollViewFragment, содержащего ScrollView с вертикальным LinearLayout внутри.

В коде фрагмента в цикле создаются 100 экземпляров TextView с помощью LayoutInflater. Каждому TextView присваивается значение очередного элемента геометрической прогрессии со знаменателем 2. Затем эти элементы программно добавляются в LinearLayout. ScrollView обеспечивает возможность вертикальной прокрутки всего сгенерированного списка, что позволяет пользователю просмотреть все 100 элементов.

Этот пример наглядно демонстрирует простоту использования ScrollView для отображения статических, не слишком больших объемов данных.

<img width="974" height="473" alt="image" src="https://github.com/user-attachments/assets/7fa95a7b-d755-4d98-b26c-f76e8bc66805" />

**Листинг 1: presentation/ScrollViewFragment.java**

Фрагмент, демонстрирующий динамическое создание и добавление View в ScrollView.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.math.BigInteger;
import ru.mirea.tenyutinmm.lesson9.R;

public class ScrollViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scroll_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout wrapper = view.findViewById(R.id.wrapper);
        LayoutInflater inflater = getLayoutInflater();

        BigInteger two = new BigInteger("2");
        BigInteger currentValue = BigInteger.ONE;

        for (int i = 0; i < 100; i++) {
            View itemView = inflater.inflate(R.layout.item_scroll, wrapper, false);
            TextView textView = itemView.findViewById(R.id.textView);
            textView.setText(String.format("%d. %s", i + 1, currentValue.toString()));
            wrapper.addView(itemView);
            currentValue = currentValue.multiply(two);
        }
    }
}
```
**2. ListView (Список авторов)**

Для демонстрации работы ListView была создана вкладка "Авторы". Экран реализован с помощью ListViewFragment, который содержит виджет ListView.

Для связи данных с интерфейсом используется стандартный ArrayAdapter. В качестве источника данных выступает строковый массив, содержащий более 30 названий книг и их авторов. Адаптер использует встроенную Android-разметку android.R.layout.simple_list_item_1 для отображения каждого элемента в виде простого TextView. ListView обеспечивает плавную прокрутку всего списка.

Этот пример показывает, как быстро можно отобразить простой список данных с помощью ListView и стандартных адаптеров.
<img width="974" height="485" alt="image" src="https://github.com/user-attachments/assets/13ad342d-375c-4f51-80fe-a85d36079099" />
<img width="974" height="466" alt="image" src="https://github.com/user-attachments/assets/f096df7e-ed74-4de2-b22d-2c1aff2be823" />

**Листинг 2: presentation/ListViewFragment.java**

Фрагмент, демонстрирующий использование ListView со стандартным ArrayAdapter.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.tenyutinmm.lesson9.R;

public class ListViewFragment extends Fragment {

    private final String[] authorsAndBooks = {
            "1. Джордж Оруэлл - 1984", "2. Олдос Хаксли - О дивный новый мир", "3. Рэй Брэдбери - 451 градус по Фаренгейту",
            "4. Михаил Булгаков - Мастер и Маргарита", "5. Фёдор Достоевский - Преступление и наказание",
            "6. Лев Толстой - Война и мир", "7. Габриэль Гарсиа Маркес - Сто лет одиночества",
            "8. Эрих Мария Ремарк - Три товарища", "9. Харпер Ли - Убить пересмешника",
            "10. Джером Сэлинджер - Над пропастью во ржи", "11. Кен Кизи - Пролетая над гнездом кукушки",
            "12. Даниел Киз - Цветы для Элджернона", "13. Виктор Гюго - Отверженные",
            "14. Аркадий и Борис Стругацкие - Пикник на обочине", "15. Станислав Лем - Солярис",
            "16. Фрэнк Герберт - Дюна", "17. Дж. Р. Р. Толкин - Властелин колец",
            "18. Джоан Роулинг - Гарри Поттер и философский камень", "19. Стивен Кинг - Зеленая миля",
            "20. Чак Паланик - Бойцовский клуб", "21. Курт Воннегут - Бойня номер пять",
            "22. Альбер Камю - Посторонний", "23. Жан-Поль Сартр - Тошнота",
            "24. Герман Гессе - Степной волк", "25. Франц Кафка - Процесс",
            "26. Уильям Голдинг - Повелитель мух", "27. Энтони Бёрджесс - Заводной апельсин",
            "28. Дуглас Адамс - Автостопом по галактике", "29. Терри Пратчетт - Цвет волшебства",
            "30. Нил Гейман - Американские боги", "31. Айзек Азимов - Основание",
            "32. Роберт Хайнлайн - Звёздный десант"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                authorsAndBooks
        );

        listView.setAdapter(adapter);
    }
}
```
**3. RecyclerView (Контрольное задание)**

Контрольное задание требовало реализовать получение данных из "заглушки" (mock data) в репозитории и отобразить их в RecyclerView с использованием архитектуры MVVM. Этот функционал был реализован на вкладке "Книги".

В слое data был модифицирован BookRepositoryImpl, чтобы он возвращал жестко закодированный ArrayList с книгами, эмулируя работу с источником данных.

В слое presentation для этого экрана используются:
•	MyLibraryViewModel: Управляет логикой, запрашивает данные из репозитория и предоставляет их через LiveData.
•	MyLibraryFragment: Подписывается на LiveData из ViewModel и отображает полученный список.
•	BookAdapter: Адаптер для RecyclerView, который связывает данные (объекты Book) с элементами интерфейса.

Обновление интерфейса происходит реактивно через LiveData, что обеспечивает сохранение состояния при повороте экрана.

<img width="974" height="455" alt="image" src="https://github.com/user-attachments/assets/38ca7d48-583b-4b34-aaf5-c2059d6b247f" />
<img width="974" height="466" alt="image" src="https://github.com/user-attachments/assets/fcb6630c-476b-4f0b-b7fe-8c509853a960" />

**Листинг 3: data/book/BookRepositoryImpl.java**

Модифицированный репозиторий, использующий "заглушку" вместо базы данных для предоставления списка книг.
```
package ru.mirea.tenyutinmm.data.book;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.mirea.tenyutinmm.domain.book.Book;
import ru.mirea.tenyutinmm.domain.book.BookRepository;

public class BookRepositoryImpl implements BookRepository {

    private final ArrayList<Book> favoriteBooks = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BookRepositoryImpl(Context context) {
        favoriteBooks.add(new Book("Мастер и Маргарита"));
        favoriteBooks.add(new Book("Дюна"));
        favoriteBooks.add(new Book("1984"));
    }

    @Override
    public void saveBook(Book book) {
        executor.execute(() -> {
            favoriteBooks.add(0, book);
        });
    }

    @Override
    public void getAllBooks(BookCallback callback) {
        executor.execute(() -> {
            callback.onBooksLoaded(new ArrayList<>(favoriteBooks));
        });
    }
}
```
**Листинг 4: presentation/MyLibraryViewModel.java**

ViewModel для экрана "Моя библиотека". Запрашивает данные у репозитория и передает их во фрагмент через LiveData.
```
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
```

**Листинг 5: presentation/MyLibraryFragment.java**

Фрагмент, отображающий список книг. Он получает ViewModel и подписывается на LiveData, полностью отделяя UI от логики.
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
В результате выполнения практической работы были изучены и применены на практике три основных способа отображения списков в Android. Контрольное задание по реализации RecyclerView с "заглушкой" данных было успешно интегрировано в существующую MVVM-архитектуру приложения.

**Работу выполни Тенютин М.М БСБО-09-22**




