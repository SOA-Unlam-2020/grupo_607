package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facumediotte.tpandroid.R;
import domain.User;

import service.Login;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextDNI = (EditText) findViewById(R.id.editTextDNI);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextCommission = (EditText) findViewById(R.id.editTextCommission);
        editTextGroup = (EditText) findViewById(R.id.editTextGroup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
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

                //LLAMAR INTENT de Login
                Intent login = new Intent(MainActivity.this, Login.class);
                login.putExtra("User", user);

                startService(login);

            }
        });

    }

}
