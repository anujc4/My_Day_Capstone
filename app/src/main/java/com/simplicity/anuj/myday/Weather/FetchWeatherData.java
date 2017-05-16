package com.simplicity.anuj.myday.Weather;

import android.util.Log;

import com.simplicity.anuj.myday.LocationService.LocationFetcher;
import com.simplicity.anuj.myday.Utility.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anujc on 23-04-2017.
 */

//Helper Class to fetch Weather Data

public class FetchWeatherData implements Callback<GetWeatherResponse> {
    private static final String LOG_TAG = FetchWeatherData.class.getSimpleName();
    private static Response<GetWeatherResponse> mResponse;
    private WeatherCallback callback;

    public FetchWeatherData(LocationFetcher mFetcher) {
        callback = null;
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
        Log.e(LOG_TAG, "Weather Data Received Successfully");
    }

    @Override
    public void onFailure(Call<GetWeatherResponse> call, Throwable t) {
        mResponse = null;
        if (callback != null)
            callback.onEventFailed();
        Log.e(LOG_TAG, "Weather Data Receive Failed");
    }

    public void setFetchWeatherListener(WeatherCallback listener) {
        this.callback = listener;
    }

}

