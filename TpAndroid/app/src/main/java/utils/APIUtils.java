package utils;

import interfaces.APIService;
import interfaces.APIWeatherService;
import retrofitClient.RetrofitClient;

public class APIUtils {

    public APIUtils() {
    }

    public static final String BASE_URL = "http://so-unlam.net.ar/api/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
