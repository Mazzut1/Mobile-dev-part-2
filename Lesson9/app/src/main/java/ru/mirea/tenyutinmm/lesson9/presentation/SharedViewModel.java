package ru.mirea.tenyutinmm.lesson9.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>();

    public void setUser(FirebaseUser firebaseUser) {
        user.setValue(firebaseUser);
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }
}