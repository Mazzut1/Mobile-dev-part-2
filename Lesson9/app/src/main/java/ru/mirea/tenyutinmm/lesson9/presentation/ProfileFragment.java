package ru.mirea.tenyutinmm.lesson9.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.tenyutinmm.lesson9.R;

public class ProfileFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView emailTextView;
    private TextView uidTextView;
    private Button logoutButton;
    private ImageView avatarImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        emailTextView = view.findViewById(R.id.tv_user_email);
        uidTextView = view.findViewById(R.id.tv_user_uid);
        logoutButton = view.findViewById(R.id.btn_logout);
        avatarImageView = view.findViewById(R.id.iv_avatar);

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                emailTextView.setText(firebaseUser.getEmail());
                uidTextView.setText("UID: " + firebaseUser.getUid());

                String avatarUrl = "https://i.pravatar.cc/300?u=" + firebaseUser.getUid();
                Glide.with(this)
                        .load(avatarUrl)
                        .into(avatarImageView);
            }
        });

        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}