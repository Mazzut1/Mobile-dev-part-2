**Отчет по практической работе №2**
**Цель работы:** Основной целью данной практической работы являлся рефакторинг проекта из предыдущего задания для приведения его к канонам "Чистой архитектуры". Ключевыми задачами были: разделение приложения на независимые модули (app, data, domain), вынесение логики работы с данными в соответствующие слои, а также реализация контрольного задания по добавлению функционала аутентификации с помощью Firebase и созданию многофункционального приложения.
**1. Рефакторинг архитектуры и создание модулей**
Проект, изначально реализованный в одном модуле, был декомпозирован на три независимых модуля, каждый со своей зоной ответственности:
•	Модуль domain: Содержит "чистую" бизнес-логику, не зависящую от Android SDK. В него были помещены все модели данных (например, Cat, Book), интерфейсы репозиториев (CatRepository, WeatherRepository) и UseCase (если бы они использовались напрямую).
•	Модуль data: Отвечает за реализацию интерфейсов из domain и работу с источниками данных. В нем находятся все реализации репозиториев (например, CatRepositoryImpl), классы для работы с сетью Retrofit (CatApi, WeatherApi), классы для работы с базой данных Room (CatDao, AppDatabase), а также реализации-заглушки.
•	Модуль app: Является слоем представления. Он содержит Activity, Fragment, классы ViewModel, адаптеры и все, что связано с пользовательским интерфейсом. Этот модуль зависит от data и domain для получения данных и выполнения бизнес-логики.
Для связи между модулями были настроены зависимости в файлах build.gradle.kts.
<img width="974" height="559" alt="image" src="https://github.com/user-attachments/assets/5e93e545-fef5-435a-8c9b-f6b4aaad7076" />
<img width="974" height="201" alt="image" src="https://github.com/user-attachments/assets/ff586eb2-bd0c-4516-a2e5-c633981255b0" />
**Листинг 1: domain/cat/CatRepository.java**
``` package ru.mirea.tenyutinmm.domain.cat;

import java.util.List;

public interface CatRepository {
    void getCats(CatsCallback callback);

    interface CatsCallback {
        void onSuccess(List<Cat> cats);
        void onError(String message);
    }
}```
**Листинг 2: data/cat/CatRepositoryImpl.java**
Реализация репозитория в data-слое. Этот класс инкапсулирует сложную логику: он пытается получить данные из базы данных Room (catDao), а если их там нет, то делает запрос в сеть через Retrofit (catApi), сохраняет результат в базу и отдает его.
```package ru.mirea.tenyutinmm.data.cat;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.tenyutinmm.data.database.AppDatabase;
import ru.mirea.tenyutinmm.data.database.CatDao;
import ru.mirea.tenyutinmm.data.database.CatEntity;
import ru.mirea.tenyutinmm.data.network.RetrofitClient;
import ru.mirea.tenyutinmm.data.cat.CatApi;
import ru.mirea.tenyutinmm.data.cat.CatImageResponse;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;

public class CatRepositoryImpl implements CatRepository {

    private final CatApi catApi;
    private final CatDao catDao;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public CatRepositoryImpl(Context context) {
        this.catApi = RetrofitClient.createClient("https://api.thecatapi.com/").create(CatApi.class);
        this.catDao = AppDatabase.getDatabase(context).catDao();
    }

    @Override
    public void getCats(CatsCallback callback) {
        databaseExecutor.execute(() -> {
            List<CatEntity> catEntities = catDao.getAll();
            if (catEntities != null && !catEntities.isEmpty()) {
                callback.onSuccess(mapEntitiesToDomain(catEntities));
            } else {
                fetchCatsFromNetwork(callback);
            }
        });
    }

    private void fetchCatsFromNetwork(CatsCallback callback) {
        catApi.getRandomCatImages(20).enqueue(new Callback<List<CatImageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatImageResponse>> call, @NonNull Response<List<CatImageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CatImageResponse> networkCats = response.body();
                    saveCatsToDatabase(networkCats);
                    callback.onSuccess(mapResponsesToDomain(networkCats));
                } else {
                    callback.onError("Failed to load cats");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatImageResponse>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private void saveCatsToDatabase(List<CatImageResponse> networkCats) {
        databaseExecutor.execute(() -> {
            List<CatEntity> catEntities = new ArrayList<>();
            for (CatImageResponse response : networkCats) {
                CatEntity entity = new CatEntity();
                entity.id = response.id;
                entity.url = response.url;
                catEntities.add(entity);
            }
            catDao.insertAll(catEntities);
        });
    }

    private List<Cat> mapEntitiesToDomain(List<CatEntity> entities) {
        List<Cat> cats = new ArrayList<>();
        for (CatEntity entity : entities) {
            cats.add(new Cat(entity.id, entity.url));
        }
        return cats;
    }

    private List<Cat> mapResponsesToDomain(List<CatImageResponse> responses) {
        List<Cat> cats = new ArrayList<>();
        for (CatImageResponse response : responses) {
            cats.add(new Cat(response.id, response.url));
        }
        return cats;
    }
} ```
**2. Контрольное задание и демонстрация функционала**
В рамках контрольных заданий был реализован широкий спектр функционала, превративший простое приложение в многоцелевой инструмент.
При первом запуске приложение отображает экран входа, реализованный с помощью Firebase Authentication. Пользователь может войти, зарегистрироваться или продолжить как гость.
<img width="974" height="471" alt="image" src="https://github.com/user-attachments/assets/aa9d70fd-3746-4228-add2-15658c2c6efa" />
После входа пользователь попадает на главный экран, который реализован в виде Single Activity с боковым навигационным меню (DrawerLayout). В зависимости от статуса (гость или авторизованный пользователь) состав меню меняется, демонстрируя различные возможности для разных типов пользователей.
<img width="974" height="466" alt="image" src="https://github.com/user-attachments/assets/2a7ad0b1-4815-46c1-87c6-b8f3aa8cb147" />
<img width="974" height="460" alt="image" src="https://github.com/user-attachments/assets/3fd4a05d-45b6-428b-a880-f526b079ad1f" />
Приложение включает в себя следующие ключевые функции, реализованные в виде Фрагментов:
•	Профиль пользователя: Для авторизованных пользователей доступен экран "Профиль", который отображает их Email и UID из Firebase, а также загружает аватар с помощью библиотеки Glide.
<img width="974" height="466" alt="image" src="https://github.com/user-attachments/assets/6e9de727-67fa-4771-90f4-49b8d8e0883e" />
•	Погода (Работа с API): На вкладке "Погода" приложение асинхронно загружает данные о погоде в Москве с помощью Retrofit. Этот экран доступен всем пользователям.
<img width="974" height="466" alt="image" src="https://github.com/user-attachments/assets/3ae275c3-67d2-4577-9a53-de931e29575e" />
•	Котики (API + Room + ML): На вкладке "Котики" отображается список изображений, загружаемых с TheCatApi. Данные кэшируются в локальной базе данных Room. При клике на изображение открывается экран детального просмотра, где реализована функция распознавания изображения с помощью TensorFlow Lite.
<img width="974" height="467" alt="image" src="https://github.com/user-attachments/assets/5ceacafb-bbb8-4681-be68-b9e71cfb850a" />
<img width="974" height="467" alt="image" src="https://github.com/user-attachments/assets/441051c5-87d9-4197-9783-d47b6508cd8d" />
**Листинг 3: presentation/MainActivity.java**
Финальная версия MainActivity, которая управляет DrawerLayout, навигацией между всеми фрагментами, созданием SharedViewModel и разграничением прав доступа для гостевых и авторизованных пользователей.
```package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.tenyutinmm.lesson9.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public boolean isGuest;
    private SharedViewModel sharedViewModel;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        isGuest = getIntent().getBooleanExtra("IS_GUEST", true);

        if (!isGuest) {
            sharedViewModel.setUser(FirebaseAuth.getInstance().getCurrentUser());
        }

        updateNavHeader(navigationView);

        if (isGuest) {
            navigationView.getMenu().findItem(R.id.navigation_books).setVisible(false);
            navigationView.getMenu().findItem(R.id.navigation_profile).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.navigation_login).setVisible(false);
        }

        if (savedInstanceState == null) {
            if (isGuest) {
                navigateToFragment(new WeatherFragment(), false);
                navigationView.setCheckedItem(R.id.navigation_weather);
            } else {
                navigateToFragment(new ProfileFragment(), false);
                navigationView.setCheckedItem(R.id.navigation_profile);
            }
        }
    }

    private void updateNavHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView tvHeaderEmail = headerView.findViewById(R.id.tv_header_email);
        ImageView ivHeaderAvatar = headerView.findViewById(R.id.iv_header_avatar);

        sharedViewModel.getUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                tvHeaderEmail.setText(firebaseUser.getEmail());
                String avatarUrl = "https://i.pravatar.cc/300?u=" + firebaseUser.getUid();
                Glide.with(this).load(avatarUrl).circleCrop().into(ivHeaderAvatar);
            } else {
                tvHeaderEmail.setText("Гость");
                ivHeaderAvatar.setImageResource(R.mipmap.ic_launcher_round);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.navigation_weather) {
            selectedFragment = new WeatherFragment();
        } else if (itemId == R.id.navigation_cats) {
            selectedFragment = new CatsFragment();
        }
        else if (itemId == R.id.navigation_books) {
            selectedFragment = new MyLibraryFragment();
        }
        else if (itemId == R.id.navigation_todo) {
            selectedFragment = new TodoFragment();
        } else if (itemId == R.id.navigation_countries) {
            selectedFragment = new CountriesFragment();
        } else if (itemId == R.id.navigation_scrollview) {
            selectedFragment = new ScrollViewFragment();
        } else if (itemId == R.id.navigation_listview) {
            selectedFragment = new ListViewFragment();
        }

        if (selectedFragment != null) {
            navigateToFragment(selectedFragment, true);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}```

В данных практических работах, работа всеми практиками, а также приложением велась только в одной папке Lesson9. Поэтому весь функционал приложения, который реализовывался во всех практиках, находится в одной папке и в следствии в одном приложении.

Работу выполнил Тенютин М.М БСБО-09-22


