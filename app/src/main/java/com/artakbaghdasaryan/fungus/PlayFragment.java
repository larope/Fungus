package com.artakbaghdasaryan.fungus;

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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class PlayFragment extends Fragment {

    private PlayViewModel mViewModel;

    private AppCompatButton _playOfflineButton;
    private AppCompatButton _playFungusButton;
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
        _playFungusButton = view.findViewById(R.id.playFungusButton);
        _playOnlineButton = view.findViewById(R.id.playOnlineButton);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        _playOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayClassicMode();
            }
        });

        _playFungusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayFungusMode();
            }
        });
        _playOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OnlineGameMenu.class));
            }
        });
    }

    private void PlayFungusMode() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FungusMode.FUNGUS_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String chessGameJson = sharedPreferences.getString(FungusMode.CHESS_GAME_STATE_FUNGUS_MODE, null);

        Gson gson = new Gson();
        ChessGameData data = gson.fromJson(chessGameJson, ChessGameData.class);

        if (data != null && data.timers.containsKey(PieceColor.black) && data.timers.containsKey(PieceColor.white)) {
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

        Gson gson = new Gson();
        ChessGameData data = gson.fromJson(chessGameJson, ChessGameData.class);

        if (data != null && data.timers.containsKey(PieceColor.black) && data.timers.containsKey(PieceColor.white)) {
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