package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facumediotte.tpandroid.R;

import domain.User;

import service.LoginRegisterService;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextDNI = (EditText) findViewById(R.id.editTextDNI);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextCommission = (EditText) findViewById(R.id.editTextCommission);
        editTextGroup = (EditText) findViewById(R.id.editTextGroup);

        buttonLogin.setOnClickListener(botonesListener);
        buttonRegister.setOnClickListener(botonesListener);

        registerLoginReceiver();
    }

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
                    Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_LONG).show();
                    Intent goToHomeActivity = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(goToHomeActivity);
                    break;
                case LoginRegisterService.REGISTER_OK:
                    Toast.makeText(MainActivity.this, "Registro exitoso, favor de loguearse", Toast.LENGTH_LONG).show();
                    break;
                case LoginRegisterService.LOGIN_ERROR:
                case LoginRegisterService.REGISTER_ERROR:
                    msgError = intent.getExtras().getString("msgError");
                    Toast.makeText(MainActivity.this, msgError, Toast.LENGTH_LONG).show();
                    break;
                default: break;
            }
        }
    }

    private void registerLoginReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginRegisterService.LOGIN_OK);
        filter.addAction(LoginRegisterService.LOGIN_ERROR);
        LoginRegisterReceiver rcv = new LoginRegisterReceiver();
        registerReceiver(rcv, filter);
    }

    @Override
    protected void onDestroy() {
        stopService(intentLoginRegister);
        super.onDestroy();
    }
}