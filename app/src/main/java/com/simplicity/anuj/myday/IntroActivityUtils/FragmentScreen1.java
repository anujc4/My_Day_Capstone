package com.simplicity.anuj.myday.IntroActivityUtils;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplicity.anuj.myday.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentScreen1 extends Fragment {


    public FragmentScreen1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_fragment_screen1, container, false);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/south_gardens.ttf");
        TextView mTextTitle = (TextView) view.findViewById(R.id.f1_textView);
        TextView mTextDescription = (TextView) view.findViewById(R.id.f1_textViewDescription);
        mTextTitle.setTypeface(font);
        mTextDescription.setTypeface(font);
        return view;
    }

}
