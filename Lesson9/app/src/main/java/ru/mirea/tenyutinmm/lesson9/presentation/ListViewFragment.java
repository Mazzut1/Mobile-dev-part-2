package ru.mirea.tenyutinmm.lesson9.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.tenyutinmm.lesson9.R;

public class ListViewFragment extends Fragment {

    private final String[] authorsAndBooks = {
            "1. Джордж Оруэлл - 1984", "2. Олдос Хаксли - О дивный новый мир", "3. Рэй Брэдбери - 451 градус по Фаренгейту",
            "4. Михаил Булгаков - Мастер и Маргарита", "5. Фёдор Достоевский - Преступление и наказание",
            "6. Лев Толстой - Война и мир", "7. Габриэль Гарсиа Маркес - Сто лет одиночества",
            "8. Эрих Мария Ремарк - Три товарища", "9. Харпер Ли - Убить пересмешника",
            "10. Джером Сэлинджер - Над пропастью во ржи", "11. Кен Кизи - Пролетая над гнездом кукушки",
            "12. Даниел Киз - Цветы для Элджернона", "13. Виктор Гюго - Отверженные",
            "14. Аркадий и Борис Стругацкие - Пикник на обочине", "15. Станислав Лем - Солярис",
            "16. Фрэнк Герберт - Дюна", "17. Дж. Р. Р. Толкин - Властелин колец",
            "18. Джоан Роулинг - Гарри Поттер и философский камень", "19. Стивен Кинг - Зеленая миля",
            "20. Чак Паланик - Бойцовский клуб", "21. Курт Воннегут - Бойня номер пять",
            "22. Альбер Камю - Посторонний", "23. Жан-Поль Сартр - Тошнота",
            "24. Герман Гессе - Степной волк", "25. Франц Кафка - Процесс",
            "26. Уильям Голдинг - Повелитель мух", "27. Энтони Бёрджесс - Заводной апельсин",
            "28. Дуглас Адамс - Автостопом по галактике", "29. Терри Пратчетт - Цвет волшебства",
            "30. Нил Гейман - Американские боги", "31. Айзек Азимов - Основание",
            "32. Роберт Хайнлайн - Звёздный десант"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                authorsAndBooks
        );

        listView.setAdapter(adapter);
    }
}