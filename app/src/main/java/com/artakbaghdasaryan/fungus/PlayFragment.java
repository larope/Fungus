package com.artakbaghdasaryan.fungus;

import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_PIECES;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_TIMER;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_SHOW_LAST_MOVE;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

public class PlayFragment extends Fragment {

    private PlayViewModel mViewModel;

    private AppCompatButton _playOfflineButton;
    private AppCompatButton _playOnlineButton;

    public PlayFragment(){
    }

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _playOfflineButton = view.findViewById(R.id.playOfflineButton);
        _playOnlineButton = view.findViewById(R.id.playOnlineButton);

        _playOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayClassicMode();
            }
        });

        _playOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayFungusMode();
            }
        });
    }

    private void PlayFungusMode() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChessGameFungusMode", Context.MODE_PRIVATE);
        String chessGameJson = sharedPreferences.getString("ChessGameStateFungusMode", null);


        if (chessGameJson != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), FungusMode.class);
            getActivity().startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CreateFungusGame.class);
            getActivity().startActivity(intent);
        }
    }

    private void PlayClassicMode() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChessGame", Context.MODE_PRIVATE);
        String chessGameJson = sharedPreferences.getString("ChessGameState", null);

        if (chessGameJson != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ChessGame.class);
            getActivity().startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CreateClassicGame.class);
            getActivity().startActivity(intent);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlayViewModel.class);

    }
}