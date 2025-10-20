package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.lesson9.R;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    private List<Cat> cats = new ArrayList<>();
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Cat cat);
    }


    public CatAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setCats(List<Cat> cats) {
        this.cats = cats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        holder.bind(cats.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cats.size();
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView catImageView;
        private final Context context;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            catImageView = itemView.findViewById(R.id.iv_cat);
            context = itemView.getContext();
        }

        public void bind(final Cat cat, final OnItemClickListener listener) {
            Glide.with(context)
                    .load(cat.url)
                    .centerCrop()
                    .into(catImageView);

            itemView.setOnClickListener(v -> listener.onItemClick(cat));
        }
    }
}