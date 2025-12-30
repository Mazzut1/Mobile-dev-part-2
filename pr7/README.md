**Отчет по практической работе №7**

**Цель работы:**

Целью работы было освоение современных инструментов навигации в Android. Ключевыми задачами являлись: отказ от findViewById в пользу View Binding, переход на архитектуру Single Activity с использованием Android Navigation Component, реализация бокового меню Navigation Drawer и интеграция всех существующих экранов в единый граф навигации.

**1. Настройка View Binding и зависимостей**

Первым шагом была модификация файла сборки build.gradle.kts. В проекте был активирован механизм View Binding, который позволяет безопасно обращаться к элементам интерфейса, исключая ошибки NullPointerException и проблемы с типами данных. Также были добавлены зависимости для библиотеки Navigation Component.

**Листинг 1: app/build.gradle.kts (фрагмент)**

Демонстрация включения View Binding и добавления библиотек навигации.
```
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "ru.mirea.tenyutinmm.lesson9"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.mirea.tenyutinmm.lesson9"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    aaptOptions {
        noCompress("tflite")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.corouteam:GlideToVectorYou:v2.0.0")

    val nav_version = "2.8.4"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
```
**2. Реализация Navigation Component**
Основой навигации стал Navigation Graph — XML-ресурс, в котором описаны все фрагменты приложения (экраны) и связи между ними. Каждому фрагменту был присвоен уникальный ID, совпадающий с ID пунктов меню, что позволило автоматизировать переходы.

**Листинг 2: res/navigation/mobile_navigation.xml**

```
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/navigation_profile">

    <!-- Профиль -->
    <fragment
        android:id="@+id/navigation_profile"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.ProfileFragment"
        android:label="Профиль"
        tools:layout="@layout/fragment_profile" />

    <!-- Погода -->
    <fragment
        android:id="@+id/navigation_weather"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.WeatherFragment"
        android:label="Погода"
        tools:layout="@layout/fragment_weather" />

    <!-- Котики -->
    <fragment
        android:id="@+id/navigation_cats"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.CatsFragment"
        android:label="Котики"
        tools:layout="@layout/fragment_cats" />

    <!-- Книги -->
    <fragment
        android:id="@+id/navigation_books"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.MyLibraryFragment"
        android:label="Моя библиотека"
        tools:layout="@layout/fragment_my_library" />

    <!-- Дела -->
    <fragment
        android:id="@+id/navigation_todo"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.TodoFragment"
        android:label="Список дел"
        tools:layout="@layout/fragment_todo" />

    <!-- Страны -->
    <fragment
        android:id="@+id/navigation_countries"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.CountriesFragment"
        android:label="Страны"
        tools:layout="@layout/fragment_countries" />

    <!-- Прогрессия -->
    <fragment
        android:id="@+id/navigation_scrollview"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.ScrollViewFragment"
        android:label="Прогрессия"
        tools:layout="@layout/fragment_scroll_view" />

    <!-- Авторы -->
    <fragment
        android:id="@+id/navigation_listview"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.ListViewFragment"
        android:label="Авторы"
        tools:layout="@layout/fragment_list_view" />

    <!-- Экран деталей котика -->
    <fragment
        android:id="@+id/catDetailFragment"
        android:name="ru.mirea.tenyutinmm.lesson9.presentation.CatDetailFragment"
        android:label="Анализ котика"
        tools:layout="@layout/activity_cat_detail" />

</navigation>
```
<img width="708" height="1240" alt="image" src="https://github.com/user-attachments/assets/bc35e100-246a-4ef7-ba1a-33742bb2d817" />
<img width="692" height="1213" alt="image" src="https://github.com/user-attachments/assets/285ea49f-dec7-464b-9ad3-c1d0b8fba064" />

В макете activity_main.xml старый контейнер FrameLayout был заменен на NavHostFragment (класс androidx.fragment.app.FragmentContainerView), который служит хостом для отображения фрагментов из графа навигации.

**3. Navigation Drawer (Боковое меню)**

Для реализации удобной навигации по большому количеству разделов было выбрано боковое меню (Navigation Drawer).
В файле activity_main.xml корневым элементом стал DrawerLayout, содержащий основной контент и NavigationView (само меню).
В файле меню drawer_view.xml были определены пункты навигации, сгруппированные логически. Также была добавлена кнопка "Войти", видимая только для гостевого режима.

<img width="860" height="1313" alt="image" src="https://github.com/user-attachments/assets/3276d7bc-7c0a-45b7-987e-cf904ad17ce8" />

<img width="712" height="1252" alt="image" src="https://github.com/user-attachments/assets/479c65ed-64ca-4663-b1ff-551aa6fcae1e" />

В классе MainActivity была полностью переписана логика навигации. Ручное управление транзакциями (beginTransaction) было заменено на использование NavController и NavigationUI. Это позволило:

1.Автоматически связывать нажатия в меню с переходами по фрагментам.
2.Автоматически управлять кнопкой "Гамбургер" / "Назад" в Toolbar.
3.Автоматически обновлять заголовок Toolbar в зависимости от открытого фрагмента (по атрибуту android:label в графе).

**Листинг 3: presentation/MainActivity.java**
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.tenyutinmm.lesson9.R;
import ru.mirea.tenyutinmm.lesson9.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public boolean isGuest;
    private SharedViewModel sharedViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Настройка Toolbar
        setSupportActionBar(binding.toolbar);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        isGuest = getIntent().getBooleanExtra("IS_GUEST", true);

        if (!isGuest) {
            sharedViewModel.setUser(FirebaseAuth.getInstance().getCurrentUser());
        }

        updateNavHeader();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile,
                R.id.navigation_weather,
                R.id.navigation_cats,
                R.id.navigation_books,
                R.id.navigation_todo,
                R.id.navigation_countries,
                R.id.navigation_scrollview,
                R.id.navigation_listview)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        if (isGuest) {
            binding.navView.getMenu().findItem(R.id.navigation_books).setVisible(false);
            binding.navView.getMenu().findItem(R.id.navigation_profile).setVisible(false);

            if (savedInstanceState == null) {

                navController.navigate(R.id.navigation_weather);
            }
        } else {
            binding.navView.getMenu().findItem(R.id.navigation_login).setVisible(false);
        }

        binding.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_login) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
    }

    private void updateNavHeader() {
        View headerView = binding.navView.getHeaderView(0);
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
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
```
**4. Контрольное задание: Интеграция детального экрана**
В рамках контрольного задания требовалось полностью перевести навигацию на Navigation Component. Для этого экран детального просмотра котика (CatDetailActivity), который ранее был отдельной активностью, был преобразован во фрагмент CatDetailFragment.

1.Создан класс CatDetailFragment с логикой отображения и ML-анализа.
2.Фрагмент добавлен в граф навигации mobile_navigation.xml.
3.В CatsFragment переход осуществляется через NavController.navigate() с передачей аргументов (URL картинки) через Bundle.

Это позволило сохранить целостность навигации: при открытии деталей котика MainActivity остается активной, а в Toolbar автоматически появляется кнопка "Назад", возвращающая к списку.

**Скриншот с данной кнопкой назад представлен выше в отчете!**

**Листинг 4: presentation/CatDetailFragment.java**
```
package ru.mirea.tenyutinmm.lesson9.presentation;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import ru.mirea.tenyutinmm.lesson9.R;

public class CatDetailFragment extends Fragment {

    private ImageView catImageView;
    private Button analyzeButton;
    private TextView resultTextView;

    private ImageClassifier imageClassifier;
    private Bitmap imageBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_cat_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catImageView = view.findViewById(R.id.iv_cat_detail);
        analyzeButton = view.findViewById(R.id.btn_analyze);
        resultTextView = view.findViewById(R.id.tv_result);

        imageClassifier = new ImageClassifier(requireContext());
        imageClassifier.initialize();

        String catUrl = null;
        if (getArguments() != null) {
            catUrl = getArguments().getString("cat_url");
        }

        if (catUrl != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(catUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageBitmap = resource;
                            catImageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        analyzeButton.setOnClickListener(v -> {
            if (imageBitmap != null) {
                new Thread(() -> {
                    final String result = imageClassifier.classify(imageBitmap);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> resultTextView.setText("Результат: " + result));
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Изображение еще не загружено", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```
В результате выполнения работы приложение приобрело законченную, профессиональную архитектуру. Использование Navigation Component и Navigation Drawer обеспечило удобную и стандартизированную навигацию, а View Binding повысил надежность кода.






