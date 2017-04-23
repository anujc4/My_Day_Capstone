package com.simplicity.anuj.myday.Weather;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;

import com.simplicity.anuj.myday.LocationService.LocationFetcher;
import com.simplicity.anuj.myday.Utility.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anujc on 23-04-2017.
 */

//Helper Class to fetch Weather Data

public class FetchWeatherData implements Callback<GetWeatherResponse> {
    private static Response<GetWeatherResponse> mResponse;
    private WeatherCallback callback;


    public FetchWeatherData(LocationFetcher mFetcher, WeatherCallback callback) {
        this.callback = callback;
        Call<GetWeatherResponse> call = WeatherProvider.getWeatherAPI().getWeather(
                String.valueOf(mFetcher.fetchLatitude()),
                String.valueOf(mFetcher.fetchLongitude()),
                Utils.API_KEY);
        call.enqueue(this);
    }

    public static Response<GetWeatherResponse> fetchAPI() {
        return mResponse;
    }

    @Override
    public void onResponse(Call<GetWeatherResponse> call, Response<GetWeatherResponse> response) {
        mResponse = response;
        callback.onEventCompleted();
        Log.e("FETCH", "Called the Callback Successfully");
    }

    @Override
    public void onFailure(Call<GetWeatherResponse> call, Throwable t) {
        mResponse = null;
        if (callback != null)
            callback.onEventFailed();
        Log.e("FETCH", "Called the Callback Failed");
    }

}


//    private String WEATHER_MAIN_WEATHER;
//    private String WEATHER_DESCRIPTION_WEATHER;
//    private String MAIN_TEMP_WEATHER;
//    private String MAIN_PRESSURE_WEATHER;
//    private String MAIN_HUMIDITY_WEATHER;
//    private String MAIN_TEMP_MIN_WEATHER;
//    private String MAIN_TEMP_MAX_WEATHER;
//    private String CLOUDS_WEATHER;
//    private String NAME_WEATHER;
//        List<Weather> tempWeather = response.body().getWeather();
//        Main tempMain = response.body().getMain();
//        Wind tempWind = response.body().getWind();
//        WEATHER_MAIN_WEATHER = tempWeather.get(0).getMain();
//        WEATHER_DESCRIPTION_WEATHER = tempWeather.get(0).getDescription();
//        MAIN_TEMP_WEATHER = String.valueOf(tempMain.getTemp());
//        MAIN_PRESSURE_WEATHER = String.valueOf(tempMain.getPressure());
//        MAIN_HUMIDITY_WEATHER = String.valueOf(tempMain.getHumidity());
//        MAIN_TEMP_MIN_WEATHER = String.valueOf(tempMain.getTempMin());
//        MAIN_TEMP_MAX_WEATHER = String.valueOf(tempMain.getTempMax());
//        CLOUDS_WEATHER = String.valueOf(tempWind.getSpeed());
//        NAME_WEATHER = response.body().getName();
