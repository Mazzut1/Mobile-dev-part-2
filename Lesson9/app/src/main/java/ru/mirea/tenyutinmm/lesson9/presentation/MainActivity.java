package ru.mirea.tenyutinmm.lesson9.presentation;

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
        }

        if (savedInstanceState == null) {
            navigateToFragment(new ProfileFragment(), false);
            if (isGuest) {
                navigateToFragment(new WeatherFragment(), false);
            }
            navigationView.setCheckedItem(isGuest ? R.id.navigation_weather : R.id.navigation_profile);
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

        if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.navigation_weather) {
            selectedFragment = new WeatherFragment();
        } else if (itemId == R.id.navigation_cats) {
            selectedFragment = new CatsFragment();
        } else if (itemId == R.id.navigation_books) {
            selectedFragment = new BooksFragment();
        } else if (itemId == R.id.navigation_todo) {
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