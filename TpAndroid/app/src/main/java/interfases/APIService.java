package interfases;

import datacontract.LoginResponse;
import domain.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("login")
    Call<LoginResponse> login(@Body User user);
}
