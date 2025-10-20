package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import ru.mirea.tenyutinmm.lesson9.R;

public class MainActivity extends AppCompatActivity {
    public boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isGuest = getIntent().getBooleanExtra("IS_GUEST", true);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (isGuest) {
            bottomNav.getMenu().findItem(R.id.navigation_books).setVisible(false);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WeatherFragment()).commit();
        }
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_weather) {
                    selectedFragment = new WeatherFragment();
                } else if (itemId == R.id.navigation_cats) {
                    selectedFragment = new CatsFragment();
                } else if (itemId == R.id.navigation_books) {
                    selectedFragment = new BooksFragment();
                }
                else if (itemId == R.id.navigation_todo) {
                    selectedFragment = new TodoFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };
}