package interfaces;

import domain.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIWeatherService {

    @GET("weather?appid=cb6f9678e93eec8d821b9cef3d6add91&units=metric")
    Call<WeatherResponse> getWeatherData(@Query("q") String name);
}
