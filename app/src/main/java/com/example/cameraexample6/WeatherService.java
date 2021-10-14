package com.example.cameraexample6;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("/data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String app_id,
            @Query("units") String units
    );


}

