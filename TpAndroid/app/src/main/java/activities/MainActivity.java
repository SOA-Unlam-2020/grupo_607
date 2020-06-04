package activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.senDroid.tpandroid.R;

import domain.User;

import service.LoginRegisterService;

/**
 * Activity para login y registración de usuario
 */
public class MainActivity extends Activity {

    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextDNI;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCommission;
    private EditText editTextGroup;
    private Button buttonLogin;
    private Button buttonRegister;
    private Intent intentLoginRegister;
    private static String LOGIN = "login";
    private static String REGISTER = "register";
    private LoginRegisterReceiver rcv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtengo objetos de la vista
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextDNI = (EditText) findViewById(R.id.editTextDNI);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextCommission = (EditText) findViewById(R.id.editTextCommission);
        editTextGroup = (EditText) findViewById(R.id.editTextGroup);

        //Seteo listeners de los botones
        buttonLogin.setOnClickListener(botonesListener);
        buttonRegister.setOnClickListener(botonesListener);

        //Registro los action del broadcast
        registerLoginReceiver();
    }

    //Listener de botones con lógica
    private View.OnClickListener botonesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String firstName = editTextName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String dni = editTextDNI.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String commission = editTextCommission.getText().toString();
            String group = editTextGroup.getText().toString();

            User user = new User(firstName,lastName,dni,email,password,commission,group);

            switch (v.getId()){
                case R.id.buttonLogin:
                    //LLAMAR INTENT de Login
                    intentLoginRegister = new Intent(MainActivity.this, LoginRegisterService.class);
                    intentLoginRegister.putExtra("User", user);
                    intentLoginRegister.putExtra("action",LOGIN);
                    startService(intentLoginRegister);
                    break;
                case R.id.buttonRegister:
                    //Llamar a un intent de Register
                    intentLoginRegister = new Intent(MainActivity.this, LoginRegisterService.class);
                    intentLoginRegister.putExtra("User", user);
                    intentLoginRegister.putExtra("action",REGISTER);
                    startService(intentLoginRegister);
                    break;
                default:
                    Toast.makeText(MainActivity.this,"Error en Listener de botones",Toast.LENGTH_LONG).show();
            }
        }
    };

    //BroadCastReceiver
    public class LoginRegisterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgError;
            String responseCallback = intent.getAction();
            switch (responseCallback) {
                case LoginRegisterService.LOGIN_OK:
                    String token = intent.getExtras().getString("token");
                    Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                    Intent goToHomeActivity = new Intent(MainActivity.this, HomeActivity.class);
                    goToHomeActivity.putExtra("token", token);
                    startActivity(goToHomeActivity);
                    break;
                case LoginRegisterService.REGISTER_OK:
                    Toast.makeText(MainActivity.this, "Registro exitoso, favor de loguearse", Toast.LENGTH_SHORT).show();
                    break;
                case LoginRegisterService.LOGIN_ERROR:
                case LoginRegisterService.LOGIN_FAIL_CALL:
                case LoginRegisterService.REGISTER_ERROR:
                case LoginRegisterService.REGISTER_FAIL_CALL:
                    msgError = intent.getExtras().getString("msgError");
                    Toast.makeText(MainActivity.this, msgError, Toast.LENGTH_LONG).show();
                    break;
                default: break;
            }
        }
    }

    //Registro los action que espero recibir del broadcast receiver
    private void registerLoginReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginRegisterService.LOGIN_OK);
        filter.addAction(LoginRegisterService.LOGIN_ERROR);
        filter.addAction(LoginRegisterService.LOGIN_FAIL_CALL);
        filter.addAction(LoginRegisterService.REGISTER_OK);
        filter.addAction(LoginRegisterService.REGISTER_ERROR);
        filter.addAction(LoginRegisterService.REGISTER_FAIL_CALL);
        rcv = new LoginRegisterReceiver();
        registerReceiver(rcv, filter);
    }

    //Saco el registro del broadCast Receiver y paro el servicio de LoginRegisterService
    @Override
    protected void onDestroy() {
        if(intentLoginRegister != null){
            stopService(intentLoginRegister);
        }
        unregisterReceiver(rcv);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Validar conexión a internet
        validateConnectivity();
    }

    //Valido si tiene conexión a internet
    public void validateConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        //boolean isWiFi = (activeNetwork != null)?activeNetwork.getType() == ConnectivityManager.TYPE_WIFI: false;

        if(!isConnected){
            Toast.makeText(MainActivity.this, "No se pudo establecer conexión de internet", Toast.LENGTH_LONG).show();
        }
    }

}
