package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import ru.mirea.tenyutinmm.data.cat.CatRepositoryImpl;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;
import ru.mirea.tenyutinmm.lesson9.R;

public class CatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button logoutButton;
    private CatAdapter catAdapter;
    private CatRepository catRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_cats);
        progressBar = view.findViewById(R.id.progressBar);
        logoutButton = view.findViewById(R.id.btn_logout);

        MainActivity activity = (MainActivity) getActivity();
        boolean isGuest = activity != null && activity.isGuest;

        if (!isGuest) {
            logoutButton.setVisibility(View.VISIBLE);
            logoutButton.setOnClickListener(v -> logout());
        } else {
            logoutButton.setVisibility(View.GONE);
        }

        catRepository = new CatRepositoryImpl(getContext());
        setupRecyclerView();
        loadCats();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        catAdapter = new CatAdapter(cat -> {
            Intent intent = new Intent(getActivity(), CatDetailActivity.class);
            intent.putExtra("cat_url", cat.url);
            startActivity(intent);
        });
        recyclerView.setAdapter(catAdapter);
    }

    private void loadCats() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> catRepository.getCats(new CatRepository.CatsCallback() {
            @Override
            public void onSuccess(List<Cat> cats) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    catAdapter.setCats(cats);
                });
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        })).start();
    }
}