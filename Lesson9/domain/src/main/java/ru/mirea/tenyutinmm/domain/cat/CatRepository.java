package ru.mirea.tenyutinmm.domain.cat;

import java.util.List;

public interface CatRepository {
    void getCats(CatsCallback callback);

    interface CatsCallback {
        void onSuccess(List<Cat> cats);
        void onError(String message);
    }
}