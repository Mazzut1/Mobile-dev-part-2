**Отчет по практической работе №6**

**Цель работы**

Целью работы было углубленное изучение компонента Fragment в Android. Ключевыми задачами были: реализация динамической навигации между фрагментами с помощью FragmentManager, управление бэк-стеком, а также освоение современных способов обмена данными между фрагментами, таких как SharedViewModel и Fragment Result API.

**1. Динамическое управление Фрагментами и FragmentManager**

В ходе выполнения работы приложение было полностью переведено на архитектуру Single Activity. Вся навигация между экранами теперь осуществляется в рамках одной MainActivity путем замены Фрагментов внутри FrameLayout.

Для управления навигацией используется боковое меню DrawerLayout. В MainActivity реализован ActionBarDrawerToggle для открытия/закрытия меню и OnNavigationItemSelectedListener для обработки кликов.

При выборе пункта меню выполняется транзакция replace() с добавлением в бэк-стек (addToBackStack(null)). Это обеспечивает корректную и интуитивно понятную работу системной кнопки "Назад": вместо закрытия приложения происходит возврат на предыдущий открытый фрагмент.
<img width="974" height="584" alt="image" src="https://github.com/user-attachments/assets/31858813-a35c-4bed-b3fb-335abfddc8ce" />

**Листинг 1: presentation/MainActivity.java**

Фрагмент кода, демонстрирующий управление FragmentManager, DrawerLayout и обработку навигации с использованием бэк-стека.

```
package ru.mirea.tenyutinmm.lesson9.presentation;

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
}
```
**2. Взаимодействие между Фрагментами**

В рамках практической работы было реализовано несколько заданий по обмену данными между фрагментами. Этот функционал был объединен на вкладке "Страны".

Экран "Страны" представляет собой мастер-детальный интерфейс, реализованный с помощью трех фрагментов:
•	CountriesFragment: Родительский фрагмент-контейнер.
•	CountriesListFragment: Дочерний фрагмент, отображающий список стран (ListView).
•	CountryDetailFragment: Дочерний фрагмент, отображающий детальную информацию.
<img width="974" height="596" alt="image" src="https://github.com/user-attachments/assets/6f011aa4-8948-4f51-8333-34deee2f1b23" />

**Передача данных через Bundle**

Задание: "Передайте во фрагмент свой номер по списку."
При создании дочерних фрагментов в CountriesFragment создается Bundle, в который помещается номер по списку (24). Этот Bundle передается в CountriesListFragment. В onViewCreated фрагмент-получатель извлекает этот номер и выводит его в Logcat, подтверждая успешную передачу данных.
<img width="974" height="590" alt="image" src="https://github.com/user-attachments/assets/d39a7279-4fbd-48a7-9beb-45c16f3274e2" />

**Взаимодействие через SharedViewModel**

Задание: "При выборе пункта, должно производится обновление содержимого экрана в DetailsFragment."
Для передачи информации о выбранной стране из CountriesListFragment в CountryDetailFragment используется общая CountriesSharedViewModel, привязанная к Activity.
1.	CountriesListFragment при клике на элемент списка вызывает метод sharedViewModel.selectCountry(country).
2.	CountryDetailFragment подписывается на LiveData в этой ViewModel и автоматически обновляет свой UI (название, столицу, флаг), как только LiveData получает новые данные.
Это обеспечивает надежное и реактивное взаимодействие между фрагментами без прямых ссылок друг на друга.

**Взаимодействие через Fragment Result API**

Задание: "Реализовать передачу данных из одного фрагмента в другой с помощью FragmentResultApi."
На экране CountryDetailFragment есть кнопка "Добавить заметку", которая открывает NoteBottomSheetFragment.
1.	CountryDetailFragment устанавливает слушатель setFragmentResultListener с ключом noteRequestKey.
2.	NoteBottomSheetFragment (отправитель) содержит EditText и кнопку. При нажатии на кнопку он упаковывает введенный текст в Bundle и отправляет его с помощью setFragmentResult, используя тот же ключ noteRequestKey.
3.	CountryDetailFragment (получатель) немедленно получает результат и обновляет TextView с текстом заметки.
<img width="974" height="595" alt="image" src="https://github.com/user-attachments/assets/e263b6a8-229c-4435-9ff8-b982a5cf028e" />
<img width="974" height="601" alt="image" src="https://github.com/user-attachments/assets/de609622-5b0c-46a6-a88c-fa57afb211d1" />

**3. Контрольное задание**

Контрольное задание требовало реализовать отображение списка с использованием ViewModel и нескольких фрагментов, а также добавить фрагмент "Профиль".

Эта задача была полностью выполнена в рамках нашего приложения:
Отображение списка с ViewModel и фрагментами: Все экраны со списками ("Котики", "Книги", "Дела") реализованы с помощью RecyclerView, Adapter, ViewModel и Fragment.

Фрагмент "Профиль": Создан ProfileFragment, который виден только авторизованным пользователям. Он получает данные о текущем пользователе из Firebase.auth.FirebaseAuth через SharedViewModel и отображает их (Email, UID, аватар).

Навигация и бэк-стек: Вся навигация в приложении построена на фрагментах с корректным использованием бэк-стека, что обеспечивает плавные переходы и ожидаемое поведение кнопки "назад".

**Листинг 1: presentation/ProfileFragment.java**

Фрагмент "Профиль". Он получает общую SharedViewModel, подписывается на LiveData с данными о пользователе и отображает их. Логика выхода из аккаунта также инкапсулирована здесь.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.tenyutinmm.lesson9.R;

public class ProfileFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView emailTextView;
    private TextView uidTextView;
    private Button logoutButton;
    private ImageView avatarImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        emailTextView = view.findViewById(R.id.tv_user_email);
        uidTextView = view.findViewById(R.id.tv_user_uid);
        logoutButton = view.findViewById(R.id.btn_logout);
        avatarImageView = view.findViewById(R.id.iv_avatar);

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                emailTextView.setText(firebaseUser.getEmail());
                uidTextView.setText("UID: " + firebaseUser.getUid());

                String avatarUrl = "https://i.pravatar.cc/300?u=" + firebaseUser.getUid();
                Glide.with(this)
                        .load(avatarUrl)
                        .into(avatarImageView);
            }
        });

        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
```

**Листинг 2: presentation/SharedViewModel.java**

Пример общей ViewModel, которая используется для обмена данными между MainActivity и ProfileFragment.
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>();

    public void setUser(FirebaseUser firebaseUser) {
        user.setValue(firebaseUser);
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }
}
```

В результате выполнения данной практической работы были освоены и применены все ключевые аспекты работы с фрагментами в Android, включая их жизненный цикл, управление транзакциями и современные подходы к межфрагментному взаимодействию.

Работу выполнил Тенютин М.М БСБО-09-22





