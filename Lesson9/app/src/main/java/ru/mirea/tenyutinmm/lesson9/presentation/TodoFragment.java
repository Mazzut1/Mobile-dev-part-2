package ru.mirea.tenyutinmm.lesson9.presentation;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mirea.tenyutinmm.lesson9.R;

public class TodoFragment extends Fragment {

    private TodoViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TodoAdapter todoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(TodoViewModel.class);

        recyclerView = view.findViewById(R.id.rv_todos);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();

        viewModel.todosLiveData.observe(getViewLifecycleOwner(), todos -> {
            todoAdapter.setTodos(todos);
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.errorLiveData.observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });

        viewModel.loadTodos();
    }

    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter((todo, isChecked) -> {
            viewModel.updateTodoStatus(todo, isChecked);
            Toast.makeText(getContext(), "Обновляем статус...", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(todoAdapter);
    }
}