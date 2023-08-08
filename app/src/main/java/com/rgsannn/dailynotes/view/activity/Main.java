package com.rgsannn.dailynotes.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.rgsannn.dailynotes.R;
import com.rgsannn.dailynotes.view.fragment.AppInfoFragment;
import com.rgsannn.dailynotes.view.fragment.NotesFragment;
import com.rgsannn.dailynotes.view.fragment.ProfileFragment;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class Main extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        final ProfileFragment profileFragment = new ProfileFragment();
        final NotesFragment notesFragment = new NotesFragment();
        final AppInfoFragment appInfoFragment = new AppInfoFragment();

        loadFragment(profileFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_profile:
                    loadFragment(profileFragment);
                    return true;
                case R.id.menu_daily_notes:
                    loadFragment(notesFragment);
                    return true;
                case R.id.menu_app_info:
                    loadFragment(appInfoFragment);
                    return true;
                case R.id.menu_logout:
                    signOut();
                    return true;
            }
            return false;
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        redirectToLogin(); // Redirect to the login page after logout
    }

    private void redirectToLogin() {
        Intent intent = new Intent(Main.this, Onboarding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}