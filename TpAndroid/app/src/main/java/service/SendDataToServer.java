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
import utils.StatusSensor;

public class SendDataToServer extends IntentService {

    private APIService soaService;
    private SensorEvent sensorEvent;
    private static final String ENV_DEV = "DEV";
    private static final String ENV_TST = "TEST";
    public static final String SEND_DATA_OK = "service.SEND_DATA_OK";
    public static final String SEND_DATA_ERROR = "service.SEND_DATA_ERROR";
    public static final String SEND_DATA_FAIL = "service.SEND_DATA_FAIL";

    public SendDataToServer() {
        super("SendDataToServer");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getExtras();
        String sensor =  bundle.getString("sensor");
        String data = bundle.getString("data");
        String token = bundle.getString("token");

        //Retrofit
        //setear tst
        sensorEvent = new SensorEvent();
        sensorEvent.setEnv(ENV_TST);
        sensorEvent.setType_events(sensor);
        sensorEvent.setState(StatusSensor.ACTIVO.getStatus());
        sensorEvent.setDescription(data);

        soaService = APIUtils.getAPIService();

        soaService.sendEvent(token,sensorEvent).enqueue(eventCallBack());

    }

    private Callback eventCallBack() {
        return new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Intent intent = new Intent();
                if(response.isSuccessful()){
                    intent.setAction(SEND_DATA_OK);
                } else {
                    intent.setAction(SEND_DATA_ERROR);
                    intent.putExtra("msgError", "Error al registrar el evento.");
                }
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Intent intentRegisterEvent = new Intent();
                intentRegisterEvent.setAction(SEND_DATA_FAIL);
                intentRegisterEvent.putExtra("msgError", "Error al enviar datos al servidor");
                sendBroadcast(intentRegisterEvent);
            }
        };
    }
}
