package com.artakbaghdasaryan.fungus;

import androidx.annotation.LongDef;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileOutputStream;

public class SettingsFragment extends Fragment {
// mirrorPieces | mirrorTimer | showLastMove
    public static String SETTINGS_MIRROR_PIECES = "mirrorPieces";
    public static String SETTINGS_MIRROR_TIMER = "mirrorTimer";
    public static String SETTINGS_SHOW_LAST_MOVE = "showLastMove";

    private SettingsViewModel mViewModel;

    private SwitchMaterial _mirrorPieces;
    private SwitchMaterial _mirrorTimer;
    private SwitchMaterial _showLastMove;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _mirrorPieces = view.findViewById(R.id.mirrorPieces);
        _mirrorTimer = view.findViewById(R.id.mirrorTimer);
        _showLastMove = view.findViewById(R.id.showLastMove);

        LoadSettings();
    }

    private void SaveSettings() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(SETTINGS_MIRROR_PIECES, _mirrorPieces.isChecked());
        editor.putBoolean(SETTINGS_MIRROR_TIMER, _mirrorTimer.isChecked());
        editor.putBoolean(SETTINGS_SHOW_LAST_MOVE, _showLastMove.isChecked());

        editor.apply();
    }

    private void LoadSettings() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        boolean mirrorPieces = sharedPref.getBoolean(SETTINGS_MIRROR_PIECES, false);
        boolean mirrorTimer = sharedPref.getBoolean(SETTINGS_MIRROR_TIMER, false);
        boolean showLastMove = sharedPref.getBoolean(SETTINGS_SHOW_LAST_MOVE, false);

        _mirrorPieces.setChecked(mirrorPieces);
        _mirrorTimer.setChecked(mirrorTimer);
        _showLastMove.setChecked(showLastMove);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SaveSettings();
    }
}