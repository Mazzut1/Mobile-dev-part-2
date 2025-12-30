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