package service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import domain.SensorEvent;
import interfaces.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.APIUtils;

public class SendDataToServer extends IntentService {

    private APIService soaService;
    private SensorEvent sensorEvent;
    private static final String ENV_DEV = "DEV";
    private static final String ENV_TST = "TEST";

    public SendDataToServer() {
        super("SendDataToServer");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getExtras();
        String sensor =  bundle.getString("sensor");
        String data = bundle.getString("data");
        String token = bundle.getString("token");

        //sensorEvent = new SensorEvent();
        //Retrofit
        //setear tst
        sensorEvent.setEnv(ENV_TST);

        soaService = APIUtils.getAPIService();

        soaService.sendEvent(sensorEvent).enqueue(eventCallBack());

    }

    private Callback eventCallBack() {
        return new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        };
    }
}
