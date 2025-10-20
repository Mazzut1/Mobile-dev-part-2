package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import ru.mirea.tenyutinmm.domain.cat.Cat;
import ru.mirea.tenyutinmm.domain.cat.CatRepository;

public class CatsViewModel extends ViewModel {

    private final CatRepository catRepository;

    private final MutableLiveData<List<Cat>> catsSource = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingSource = new MutableLiveData<>();
    private final MutableLiveData<String> errorSource = new MutableLiveData<>();

    public final LiveData<Boolean> isLoading = isLoadingSource;
    public final LiveData<String> error = errorSource;

    private final MediatorLiveData<List<Cat>> catsLiveData = new MediatorLiveData<>();
    public LiveData<List<Cat>> getCatsLiveData() {
        return catsLiveData;
    }

    public CatsViewModel(CatRepository catRepository) {
        this.catRepository = catRepository;
        catsLiveData.addSource(catsSource, cats -> {
            catsLiveData.setValue(cats);
            isLoadingSource.setValue(false);
        });
    }

    public void loadCats() {
        isLoadingSource.setValue(true);
        catRepository.getCats(new CatRepository.CatsCallback() {
            @Override
            public void onSuccess(List<Cat> cats) {
                catsSource.postValue(cats);
            }

            @Override
            public void onError(String message) {
                errorSource.postValue(message);
                isLoadingSource.postValue(false);
            }
        });
    }
}