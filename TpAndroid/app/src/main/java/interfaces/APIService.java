package interfaces;

import datacontractEvent.RegisterEventResponse;
import datacontractImpl.LoginResponse;
import datacontractImpl.RegisterResponse;
import domain.SensorEvent;
import domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Interfaz para consumir los métodos Rest que expone la API, sendEvent se le pasa el token de Header
 */
public interface APIService {

    @POST("login")
    Call<LoginResponse> login(@Body User user);

    @POST("register")
    Call<RegisterResponse> register(@Body User user);

    @POST("event")
    Call<RegisterEventResponse> sendEvent(  @Header("token") String token,
                                            @Body SensorEvent sensorEvent);
}
