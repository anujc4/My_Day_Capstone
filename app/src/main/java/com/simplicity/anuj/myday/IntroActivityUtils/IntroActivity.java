package com.simplicity.anuj.myday.IntroActivityUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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
        addSlide(AppIntroFragment.newInstance("Welcome to My Day",
                "A simple yet elegant companion for you to pour your thoughts into. \nClick next to know more",
                R.drawable.calender_icon,
                Color.parseColor("#9575CD")
        ));

        addSlide(AppIntroFragment.newInstance("What can you do?",
                "Make daily notes just like a Journal and add images to bring your journal to life",
                R.drawable.bulb_icon,
                Color.parseColor("#7E57C2")
        ));
        addSlide(AppIntroFragment.newInstance("What can you do?",
                "Add your Location to daily feeds to personalise your Journal.",
                R.drawable.location_icon,
                Color.parseColor("#673AB7")
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
    }
}
