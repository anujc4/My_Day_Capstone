package com.simplicity.anuj.myday.Weather;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplicity.anuj.myday.R;
import com.simplicity.anuj.myday.Utility.Utils;

public class WeatherMainFragment extends android.app.Fragment {

    TextView weatherDescriptionTextView;
    TextView mainPressureTextView;
    TextView mainHumidityTextView;
    TextView mainTempMinTextView;
    TextView mainTempMaxTextView;
    TextView cloudsTextView;
    TextView nameTextView;
    ImageView forecastIconImageView;

    //Default Constructor
    public WeatherMainFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        String WEATHER_DESCRIPTION_WEATHER = getArguments().getString(Utils.WEATHER_DESCRIPTION_WEATHER);
        String MAIN_PRESSURE_WEATHER = getArguments().getString(Utils.MAIN_PRESSURE_WEATHER);
        String MAIN_HUMIDITY_WEATHER = getArguments().getString(Utils.MAIN_HUMIDITY_WEATHER);
        String MAIN_TEMP_MIN_WEATHER = getArguments().getString(Utils.MAIN_TEMP_MIN_WEATHER);
        String MAIN_TEMP_MAX_WEATHER = getArguments().getString(Utils.MAIN_TEMP_MAX_WEATHER);
        String CLOUDS_WEATHER = getArguments().getString(Utils.CLOUDS_WEATHER);
        String NAME_WEATHER = getArguments().getString(Utils.NAME_WEATHER);

        weatherDescriptionTextView.setText(WEATHER_DESCRIPTION_WEATHER);
        mainPressureTextView.setText(MAIN_PRESSURE_WEATHER);
        mainHumidityTextView.setText(MAIN_HUMIDITY_WEATHER);
        mainTempMinTextView.setText(MAIN_TEMP_MIN_WEATHER);
        mainTempMaxTextView.setText(MAIN_TEMP_MAX_WEATHER);
        cloudsTextView.setText(CLOUDS_WEATHER);
        nameTextView.setText(NAME_WEATHER);

        int WEATHER_CONDITION_ID = getDrawableResourceForWeatherCondition(getArguments().getInt(Utils.WEATHER_CONDITION_ID));
        if (WEATHER_CONDITION_ID != -1)
            forecastIconImageView.setBackgroundResource(WEATHER_CONDITION_ID);
        else
            forecastIconImageView.setBackgroundResource(R.drawable.art_clear);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_main, container, false);
        weatherDescriptionTextView = (TextView) view.findViewById(R.id.weather_fragment_forecast_textview);
        mainPressureTextView = (TextView) view.findViewById(R.id.weather_fragment_pressure_textview);
        mainHumidityTextView = (TextView) view.findViewById(R.id.weather_fragment_humidity_textview);
        mainTempMinTextView = (TextView) view.findViewById(R.id.weather_fragment_low_textview);
        mainTempMaxTextView = (TextView) view.findViewById(R.id.weather_fragment_high_textview);
        cloudsTextView = (TextView) view.findViewById(R.id.weather_fragment_wind_textview);
        nameTextView = (TextView) view.findViewById(R.id.weather_fragment_name_textview);
        forecastIconImageView = (ImageView) view.findViewById(R.id.weather_fragment_icon);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public int getDrawableResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}

