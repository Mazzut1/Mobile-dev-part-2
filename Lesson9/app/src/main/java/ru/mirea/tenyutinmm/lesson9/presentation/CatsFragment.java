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