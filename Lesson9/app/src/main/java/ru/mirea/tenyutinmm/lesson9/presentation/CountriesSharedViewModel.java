package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;
import ru.mirea.tenyutinmm.domain.country.Country;

public class CountriesSharedViewModel extends ViewModel {

    private final MutableLiveData<Country> selectedCountry = new MutableLiveData<>();

    private final Map<String, String> notes = new HashMap<>();
    private final MutableLiveData<String> currentNote = new MutableLiveData<>();

    public void selectCountry(Country country) {
        selectedCountry.setValue(country);
        currentNote.setValue(notes.get(country.name));
    }

    public LiveData<Country> getSelectedCountry() {
        return selectedCountry;
    }

    public void saveNote(String note) {
        Country country = selectedCountry.getValue();
        if (country != null && note != null) {
            notes.put(country.name, note);
            currentNote.setValue(note);
        }
    }

    public LiveData<String> getCurrentNote() {
        return currentNote;
    }
}