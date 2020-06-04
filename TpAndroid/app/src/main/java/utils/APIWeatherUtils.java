package utils;

import interfaces.APIWeatherService;
import retrofitClient.RetrofitClient;

public class APIWeatherUtils {
    public static final String URI_WEATHER = "https://api.openweathermap.org/data/2.5/";


    public static APIWeatherService getAPIWeatherService() {
        return RetrofitClient.getClient(URI_WEATHER).create(APIWeatherService.class);
}
}
