package ru.mirea.tenyutinmm.lesson9.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import ru.mirea.tenyutinmm.domain.todo.Todo;
import ru.mirea.tenyutinmm.lesson9.R;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> todos = new ArrayList<>();
    private final OnTodoCheckedChangeListener listener;

    public interface OnTodoCheckedChangeListener {
        void onCheckedChanged(Todo todo, boolean isChecked);
    }

    public TodoAdapter(OnTodoCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(todos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userAvatar;
        private final TextView todoTitle;
        private final CheckBox completedCheckBox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.iv_user_avatar);
            todoTitle = itemView.findViewById(R.id.tv_todo_title);
            completedCheckBox = itemView.findViewById(R.id.cb_completed);
        }

        public void bind(final Todo todo, final OnTodoCheckedChangeListener listener) {
            todoTitle.setText(todo.title);
            String avatarUrl = "https://i.pravatar.cc/150?u=" + todo.userId;
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(userAvatar);

            completedCheckBox.setOnCheckedChangeListener(null);
            completedCheckBox.setChecked(todo.completed);

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    listener.onCheckedChanged(todo, isChecked);
                }
            });
        }
    }
}