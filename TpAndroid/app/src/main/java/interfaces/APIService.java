package interfaces;

import datacontractImpl.LoginResponse;
import datacontractImpl.RegisterResponse;
import domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("login")
    Call<LoginResponse> login(@Body User user);

    @POST("register")
    Call<RegisterResponse> register(@Body User user);
}
