package activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facumediotte.tpandroid.R;

import java.text.DecimalFormat;
import java.util.Random;

import service.SendDataToServer;

public class HomeActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textViewSensorDetect;
    private TextView textViewValuesToSend;
    private Intent dataToServer;
    private String token;

    private DecimalFormat twoDecimals = new DecimalFormat("###.##");

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
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        View view = this.getWindow().getDecorView();
                        view.setBackgroundColor(color);

                        //Enviamos evento al servidor
                        String sensor = textViewSensorDetect.getText().toString();
                        String dataToSend = textViewValuesToSend.getText().toString();

                        dataToServer = new Intent(HomeActivity.this, SendDataToServer.class);
                        dataToServer.putExtra("sensor", sensor);
                        dataToServer.putExtra("data", dataToSend);
                        //dataToServer.putExtra("token",token);
                        startService(dataToServer);
                    }

                    break;
                case Sensor.TYPE_PROXIMITY:

                    // Si detecta 0 lo represento
                    if( event.values[0] == 0 ) {
                        txt += "Proximidad:\n";
                        txt += event.values[0] + "\n";
                        textViewValuesToSend.setText(txt);
                        textViewSensorDetect.setText("Proximidad Detectada");

                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        View view = this.getWindow().getDecorView();
                        view.setBackgroundColor(color);

                        //sendEventToServer();
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
        RegisterEventReceiver rcv = new RegisterEventReceiver();
        registerReceiver(rcv, filter);
    }

    @Override
    protected void onResume() {
        initSensores();
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

}
