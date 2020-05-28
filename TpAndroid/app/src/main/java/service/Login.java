package service;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import activities.MainActivity;
import datacontract.LoginResponse;
import domain.User;
import interfases.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.APIUtils;

public class Login extends Service {

    private User user;
    private static final String ENV_DEV = "DEV";
    private static final String ENV_TST = "TEST";
    private APIService soaService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        user = (User) bundle.get("User");

        //Retrofit
        user.setEnv(ENV_TST); //Modificar

        soaService = APIUtils.getAPIService();

        soaService.login(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if(response.isSuccessful()){
                    LoginResponse loginResponse = response.body();
                    //Ver que hacemos aca, deberíamos ir a un nuevo activity
                    Intent intent1 = new Intent(Login.this, Application.class);
                    startActivity(intent1);
                } else {
                    int responseCode = response.code();
                    try {
                        String msgError = response.errorBody().string();
                        System.out.println(msgError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //MainActivity.showResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("Fallo la llamada al servicio de Login");
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
