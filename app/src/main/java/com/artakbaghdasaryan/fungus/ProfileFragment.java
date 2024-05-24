package com.artakbaghdasaryan.fungus;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private Dialog _exitConfirmationPopup;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        _exitConfirmationPopup = new Dialog(getActivity());

        view.findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _exitConfirmationPopup.setContentView(R.layout.exit_confirmation_window);

                _exitConfirmationPopup.findViewById(R.id.exit_confirmed).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        _exitConfirmationPopup.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), Login.class));
                    }
                });

                _exitConfirmationPopup.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _exitConfirmationPopup.dismiss();
                    }
                });

                _exitConfirmationPopup.show();
            }
        });
    }
}