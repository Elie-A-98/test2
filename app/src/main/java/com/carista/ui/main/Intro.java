package com.carista.ui.main;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carista.R;
import com.carista.SplashScreen;
import com.carista.utils.Device;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

import org.jetbrains.annotations.Nullable;

public class Intro extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Device.setFirstRun(this);

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_welcome),
                getString(R.string.intro_welcome_description),
                R.mipmap.ic_launcher
        ));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_posts),
                getString(R.string.intro_posts_description),
                R.drawable.ic_add_photo
        ));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.intro_editor),
                getString(R.string.intro_editor_description),
                R.drawable.ic_edit
        ));
    }


    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(this, SplashScreen.class));
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        startActivity(new Intent(this, SplashScreen.class));
    }
}
