package com.simplicity.anuj.myday.IntroActivityUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.simplicity.anuj.myday.Activity.MainActivity;
import com.simplicity.anuj.myday.R;

public class IntroActivity extends AppIntro {


    Context mContext;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFadeAnimation();
        mContext = this;

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
//
//        Fragment fragment_one = new FragOne();
//        addSlide(fragment_one);

//        addSlide(new FragmentScreen1());

        addSlide(AppIntroFragment.newInstance("Welcome to My Day",
                "A simple yet elegant companion for you to pour your thoughts into. \nClick next to know more",
                R.drawable.clip_calender,
                Color.parseColor("#9C27B0")
        ));

        addSlide(AppIntroFragment.newInstance("What can you do?",
                "Make daily notes just like a Diary. It's private so no one can peek anymore.",
                R.drawable.clip_notes,
                Color.parseColor("#9C27B0")
        ));

        addSlide(AppIntroFragment.newInstance("And that's not it",
                "Your Entries can now hold your location, images and videos to bring your stories to life." +
                        "\nIt's truly customizable.",
                R.drawable.clip_camera,
                Color.parseColor("#9C27B0")
        ));

        addSlide(AppIntroFragment.newInstance("Worried about Rain?",
                "Your Weather data is automatically stored along with the Entries. Sounds cool?",
                R.drawable.clip_weather,
                Color.parseColor("#9C27B0")
        ));

        addSlide(AppIntroFragment.newInstance("Wait! There's more.",
                "No need to worry about losing your Diary. My Day automatically stores everything on your Google Drive so you can easily recover anytime.",
                R.drawable.clip_cloud,
                Color.parseColor("#9C27B0")
        ));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        onDonePressed();
    }
}
