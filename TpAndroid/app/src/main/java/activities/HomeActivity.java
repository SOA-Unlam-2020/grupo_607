package activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.senDroid.tpandroid.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import service.SendDataToServer;
import utils.ObjectSerializer;

/**
 * Activity HomeActivity donde se registran los eventos de sensores
 */
public class HomeActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textViewSensorDetect;
    private TextView textViewValuesToSend;
    private Button buttonHistory;

    private Intent dataToServer;
    private String token;
    private RegisterEventReceiver rcv;
    private ArrayList<String> eventToRegister;
    public static final String SHARED_PREFS_FILE = "RegistroDeEventos";
    public static final String FILE_SH_PREF = "Eventos";

    private DecimalFormat twoDecimals = new DecimalFormat("###.##");
    private static final float PARAM_TO_LOW_BATTERY = (float) 0.15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Obtenemos el Token
        Bundle bundle = getIntent().getExtras();
        token = bundle.getString("token");

        //Obtenemos los sensores disponibles
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Obtenemos los textview a setear
        textViewSensorDetect = (TextView) findViewById(R.id.textViewSensorDetect);
        textViewValuesToSend = (TextView) findViewById(R.id.textViewValuesToSend);

        //Button para ver la historia de eventos
        buttonHistory = (Button) findViewById(R.id.buttonHistory);

        //Le agregamos una función al click Listener
        buttonHistory.setOnClickListener(buttonHistoryOnClick);

        //Inicializado el arraylist que voy a usar para SharedPreferences
        eventToRegister = new ArrayList<>();

        //Registro los filters
        registerEventReceiver();
    }

    //Le agregamos una función al click Listener
    public View.OnClickListener buttonHistoryOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Ir a la pantalla de historial de eventos
            Intent intent = new Intent(HomeActivity.this, ListEvents.class);
            startActivity(intent);
        }
    };


    //Metodo que escucha el cambio de los sensores
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

                        //Guarda datos en SharedPreferences
                        this.saveOnSharedPreferences(txt);

                        //Se le cambia el color del background por uno aleatorio
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        View view = this.getWindow().getDecorView();
                        view.setBackgroundColor(color);

                        //Enviamos evento al servidor
                        String sensor = textViewSensorDetect.getText().toString();
                        String dataToSend = textViewValuesToSend.getText().toString();

                        dataToSend.concat("\n Se registra evento de acelerometro");
                        //Envio a la API de registrar Evento
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

                        //Guarda datos en SharedPreferences
                        this.saveOnSharedPreferences(txt);

                        String sensor = textViewSensorDetect.getText().toString();
                        String dataToSend = textViewValuesToSend.getText().toString();

                        dataToSend += "\nSe registra evento de proximidad";

                        //Valida Permisos para acceder a la camara, si los tiene abro camara
                        if(validaPermisos()) {
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivity(i);
                            //Envio a la API de registrar Evento
                            sendEventToServer(sensor,dataToSend);
                        }

                    }
                    break;
                default: break;
            }
        }
    }

    //Función para enviar datos al registrador de eventos
    private void sendEventToServer(String sensor, String dataToSend) {
        dataToServer = new Intent(HomeActivity.this, SendDataToServer.class);
        dataToServer.putExtra("sensor", sensor);
        dataToServer.putExtra("data", dataToSend);
        dataToServer.putExtra("token",token);
        startService(dataToServer);
    }

    //Función que valida permisos
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
            //Se le pide permisos a la camara
            requestPermissions(new String[]{Manifest.permission.CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si respondio que si, otorgo permisos
        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(HomeActivity.this, "Se concedieron los permisos", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Se le informa al usuario que tiene los permisos desactivados que para usar esta funcionalidad
    //debe activarlos y se le abre un cuadro de solicitud de permisos
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
                    Toast.makeText(HomeActivity.this, msgError, Toast.LENGTH_SHORT).show();
                    break;
                default: break;
            }
        }
    }

    //Registra los Action que puedo recibir como respuesta del broadcastReceiver
    private void registerEventReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SendDataToServer.SEND_DATA_OK);
        filter.addAction(SendDataToServer.SEND_DATA_ERROR);
        filter.addAction(SendDataToServer.SEND_DATA_FAIL);
        rcv = new RegisterEventReceiver();
        registerReceiver(rcv, filter);
    }

    //Inicio sensores, valido conectividad con internet y chequeo estado bateria
    @Override
    protected void onResume() {
        initSensores();
        validateConnectivity();
        checkBattery();
        super.onResume();
    }

    //Saco los registros de los sensores
    @Override
    protected void onPause() {
        stopSensores();
        super.onPause();
    }

    //Saco los registros de los sensores y saco el registro del broadcast receiver
    //Paro el servicio de dataToServer para que no consuma memoria
    @Override
    protected void onDestroy() {
        if(dataToServer != null) {
            stopService(dataToServer);
        }
        stopSensores();
        unregisterReceiver(rcv);
        super.onDestroy();
    }

    //Saco los registros de los sensores
    @Override
    protected void onStop() {
        stopSensores();
        super.onStop();
    }

    //Registro los sensores
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

    //Valido conectividad con internet
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

    //Checkeamos estado bateria baja
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

    //Guardamos datos en SharedPreferences, se le agrego la fecha
    public void saveOnSharedPreferences(String event) {

        //Le pongo fecha y hora para poder verlo en el historial de eventos
        String pattern = "dd-MM-yyyy hh:mm:ss aa";
        String dateInString =new SimpleDateFormat(pattern).format(new Date());
        event += "\n " + dateInString;

        if (null == eventToRegister) {
            eventToRegister = new ArrayList<>();
        }
        eventToRegister.add(event);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(FILE_SH_PREF, ObjectSerializer.serialize(eventToRegister));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

}
