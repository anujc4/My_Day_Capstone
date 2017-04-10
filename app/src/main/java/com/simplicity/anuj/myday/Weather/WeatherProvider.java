package com.simplicity.anuj.myday.Weather;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by anujc on 4/2/2017.
 */

/*
http://api.openweathermap.org/data/2.5/weather?lat=12.9176315&lon=77.5909999&APPID=6e63c3c7d966e334108185e64356a681


{"coord":{"lon":77.59,"lat":12.92},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"base":"stations",
"main":{"temp":295.18,"pressure":1015,"humidity":64,"temp_min":294.15,"temp_max":296.15},"visibility":6000,"wind":{"speed":2.1,"deg":210}
,"clouds":{"all":0},"dt":1491091200,"sys":{"type":1,"id":7823,"message":0.0023,"country":"IN","sunrise":1491093917,"sunset":1491138076},
"id":1277333,"name":"Bangalore","cod":200}

 */

public class WeatherProvider {


    private static final String LOG_TAG = WeatherProvider.class.getCanonicalName();
    private static WeatherService mWeatherService = null;

    public static WeatherService getWeatherAPI() {

        if (mWeatherService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mWeatherService = retrofit.create(WeatherService.class);
        }
        return mWeatherService;
    }

    public interface WeatherService {
        @GET("data/2.5/weather?")
        Call<GetWeatherResponse> getWeather(@Query("lat") String latitude, @Query("lon") String longitude, @Query("APPID") String API_KEY);
    }

}
