package activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sendroid.tpandroid.R;

import java.text.DecimalFormat;
import java.util.Random;

import domain.Ubication;
import domain.WeatherResponse;
import interfaces.APIWeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitClient.RetrofitClient;
import service.SendDataToServer;
import utils.APIUtils;
import utils.APIWeatherUtils;

public class HomeActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textViewSensorDetect;
    private TextView textViewValuesToSend;
    private Intent dataToServer;
    private String token;
    private RegisterEventReceiver rcv;

    private DecimalFormat twoDecimals = new DecimalFormat("###.##");
    private static final float PARAM_TO_LOW_BATTERY = (float) 0.15;

    private TextView textViewCity;
    private TextView textViewDescription;
    private TextView textViewHumidity;
    private TextView textViewTemperature;
    private Ubication ubication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getExtras();
        token = bundle.getString("token");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textViewSensorDetect = (TextView) findViewById(R.id.textViewSensorDetect);
        textViewValuesToSend = (TextView) findViewById(R.id.textViewValuesToSend);

        registerEventReceiver();

        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewHumidity = findViewById(R.id.textViewHumidity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                ubication = new Ubication(this);
                double latitud = ubication.getLatitude();
                getWeatherData("lat="+ubication.getLatitude()+"&lon="+ ubication.getAltitude());
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
            }
        }



    }

    private void getWeatherData(String name){

        APIWeatherService apiInterface = APIWeatherUtils.getAPIWeatherService();
        //apiInterface = RetrofitClient.getClient("https://api.openweathermap.org/data/2.5/").create(APIWeatherService.class);

        Call<WeatherResponse> call = apiInterface.getWeatherData(name);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                textViewCity.setText("Moron");
                textViewTemperature.setText("Temperatura"+" "+response.body().getTemp()+" °C");
                textViewDescription.setText("Sensación térmica"+" "+response.body().getFeels_like()+" °C");
                textViewHumidity.setText("Humedad"+" "+response.body().getHumidity()+" %");
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                System.out.println("Llegamos");
                t.printStackTrace();
            }
        });

    }

    // Metodo que escucha el cambio de los sensores
    @Override
    public void onSensorChanged(SensorEvent event) {

        String txt = "";

        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if ((event.values[0] > 25) || (event.values[1] > 25) || (event.values[2] > 25)) {
                        txt += "Acelerometro:\n";
                        txt += "x: " + twoDecimals.format(event.values[0]) + " m/seg2 \n";
                        txt += "y: " + twoDecimals.format(event.values[1]) + " m/seg2 \n";
                        txt += "z: " + twoDecimals.format(event.values[2]) + " m/seg2 \n";
                        textViewValuesToSend.setText(txt);
                        textViewSensorDetect.setText("Vibración Detectada");

                        //Se le cambia el color del background por uno aleatorio
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        View view = this.getWindow().getDecorView();
                        view.setBackgroundColor(color);

                        //Enviamos evento al servidor
                        String sensor = textViewSensorDetect.getText().toString();
                        String dataToSend = textViewValuesToSend.getText().toString();

                        dataToSend.concat("\n Se registra evento de acelerometro");

                        sendEventToServer(sensor,dataToSend);
                    }

                    break;
                case Sensor.TYPE_PROXIMITY:

                    // Si detecta 0 lo represento
                    if( event.values[0] == 0 ) {
                        txt += "Proximidad:\n";
                        txt += event.values[0] + "\n";
                        textViewValuesToSend.setText(txt);
                        textViewSensorDetect.setText("Proximidad Detectada");

                        String sensor = textViewSensorDetect.getText().toString();
                        String dataToSend = textViewValuesToSend.getText().toString();

                        dataToSend += "\nSe registra evento de proximidad";

                        if(validaPermisos()) {
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivity(i);

                            sendEventToServer(sensor,dataToSend);
                        }

                    }
                    break;
                default: break;
            }
        }
    }

    private void sendEventToServer(String sensor, String dataToSend) {
        dataToServer = new Intent(HomeActivity.this, SendDataToServer.class);
        dataToServer.putExtra("sensor", sensor);
        dataToServer.putExtra("data", dataToSend);
        dataToServer.putExtra("token",token);
        startService(dataToServer);
    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{Manifest.permission.CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(HomeActivity.this, "Se concedieron los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==1222){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                ubication = new Ubication(this);
                double latitud = ubication.getLatitude();
                getWeatherData("lat="+ubication.getLatitude()+"&lon="+ ubication.getAltitude());
            }
        }

    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(HomeActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        dialogo.show();
    }



    // Metodo que escucha el cambio de sensibilidad de los sensores
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //BroadCastReceiver

    public class RegisterEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgError;
            String responseCallback = intent.getAction();
            switch (responseCallback) {
                case SendDataToServer.SEND_DATA_OK:
                    Toast.makeText(HomeActivity.this,
                            "Se envió el evento al servidor correctamente",Toast.LENGTH_SHORT).show();
                    break;
                case SendDataToServer.SEND_DATA_ERROR:
                case SendDataToServer.SEND_DATA_FAIL:
                    msgError = intent.getExtras().getString("msgError");
                    Toast.makeText(HomeActivity.this, msgError, Toast.LENGTH_LONG).show();
                    break;
                default: break;
            }
        }
    }


    private void registerEventReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SendDataToServer.SEND_DATA_OK);
        filter.addAction(SendDataToServer.SEND_DATA_ERROR);
        filter.addAction(SendDataToServer.SEND_DATA_FAIL);
        rcv = new RegisterEventReceiver();
        registerReceiver(rcv, filter);
    }

    @Override
    protected void onResume() {
        initSensores();
        validateConnectivity();
        checkBattery();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopSensores();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopSensores();
        unregisterReceiver(rcv);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopSensores();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        initSensores();
        super.onRestart();
    }

    // Metodo para iniciar el acceso a los sensores
    protected void initSensores() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Metodo para parar la escucha de los sensores
    private void stopSensores() {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

    public void validateConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        //boolean isWiFi = (activeNetwork != null)?activeNetwork.getType() == ConnectivityManager.TYPE_WIFI: false;

        if(!isConnected){
            Toast.makeText(HomeActivity.this, "No se pudo establecer conexión de internet", Toast.LENGTH_LONG).show();
            String errorConnectivity = "Error de conexión";
            String dataToSend = "No se pudo lograr establecer una conexión a internet por Wifi o por datos";
            sendEventToServer(errorConnectivity,dataToSend);
        }
    }

    public void checkBattery() {
        Intent batteryStatus = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        if(batteryPct <= PARAM_TO_LOW_BATTERY) {
            Toast.makeText(HomeActivity.this, "Batería baja", Toast.LENGTH_SHORT).show();
            String lowBattery = "Batería baja";
            String dataToSend = "Se detecto que la batería es menor al 15 %";
            sendEventToServer(lowBattery,dataToSend);
        }
    }

}
