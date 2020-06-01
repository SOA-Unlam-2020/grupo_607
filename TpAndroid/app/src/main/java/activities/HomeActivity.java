package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facumediotte.tpandroid.R;

import java.text.DecimalFormat;
import java.util.Random;

public class HomeActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView textViewSensorDetect;
    private TextView textViewValuesToSend;

    private DecimalFormat twoDecimals = new DecimalFormat("###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textViewSensorDetect = (TextView) findViewById(R.id.textViewSensorDetect);
        textViewValuesToSend = (TextView) findViewById(R.id.textViewValuesToSend);
    }

    // Metodo que escucha el cambio de los sensores
    @Override
    public void onSensorChanged(SensorEvent event) {

        String txt = "";

        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if ((event.values[0] > 25) || (event.values[1] > 25) || (event.values[2] > 25))
                    {
                        txt += "Acelerometro:\n";
                        txt += "x: " + twoDecimals.format(event.values[0]) + " m/seg2 \n";
                        txt += "y: " + twoDecimals.format(event.values[1]) + " m/seg2 \n";
                        txt += "z: " + twoDecimals.format(event.values[2]) + " m/seg2 \n";
                        textViewValuesToSend.setText(txt);

                        textViewSensorDetect.setText("Vibracion Detectada");
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        View view = this.getWindow().getDecorView();
                        view.setBackgroundColor(color);
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
                    }
                    break;
                default: break;
            }
        }
    }

    // Metodo que escucha el cambio de sensibilidad de los sensores
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
