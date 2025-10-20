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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.tenyutinmm.lesson9.R;

public class CatsFragment extends Fragment {

    private CatsViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button logoutButton;
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
        logoutButton = view.findViewById(R.id.btn_logout);

        setupRecyclerView();

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isGuest) {
            logoutButton.setVisibility(View.VISIBLE);
            logoutButton.setOnClickListener(v -> logout());
        } else {
            logoutButton.setVisibility(View.GONE);
        }

        viewModel.getCatsLiveData().observe(getViewLifecycleOwner(), cats -> {
            catAdapter.setCats(cats);
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if(error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        if (viewModel.getCatsLiveData().getValue() == null) {
            viewModel.loadCats();
        }
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
}