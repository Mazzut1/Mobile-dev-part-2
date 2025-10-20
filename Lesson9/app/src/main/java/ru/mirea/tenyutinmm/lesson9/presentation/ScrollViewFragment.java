package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.math.BigInteger;
import ru.mirea.tenyutinmm.lesson9.R;

public class ScrollViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scroll_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout wrapper = view.findViewById(R.id.wrapper);
        LayoutInflater inflater = getLayoutInflater();

        BigInteger two = new BigInteger("2");
        BigInteger currentValue = BigInteger.ONE;

        for (int i = 0; i < 100; i++) {
            View itemView = inflater.inflate(R.layout.item_scroll, wrapper, false);
            TextView textView = itemView.findViewById(R.id.textView);
            textView.setText(String.format("%d. %s", i + 1, currentValue.toString()));
            wrapper.addView(itemView);
            currentValue = currentValue.multiply(two);
        }
    }
}