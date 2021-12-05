package com.example.proyecto_prenda_cliente;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_prenda_cliente.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegistrarActivity extends AppCompatActivity {

    public static Button btnRegistrar;
    private static HttpURLConnection con;
    public static EditText txtUser, txtEmail, txtPass, txtPassV;
    public static Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        txtUser = findViewById(R.id.txtUser);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPassword1);
        //passV se usa para verificar que la contrasenas coincidan
        txtPassV = findViewById(R.id.txtPassword2);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUser.getText().toString().equals("") || txtUser.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un problema, El 'Nombre de Usuario' no debe estar vacio", Toast.LENGTH_LONG);
                    toast.show();
                }else if(!isValidEmail(txtEmail.getText())){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un problema, El 'Correo es incorrecto' no debe estar vacio", Toast.LENGTH_LONG);
                    toast.show();
                }else if(txtPass.getText().toString().isEmpty() || txtPassV.getText().toString().isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un problema, Debe tener una 'Contraseña' no debe estar vacia", Toast.LENGTH_LONG);
                    toast.show();
                } else if(!txtPass.getText().toString().equals(txtPassV.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un problema, Las Contraseñas deben de conincidir", Toast.LENGTH_LONG);
                    toast.show();
                }

                else {
                    int SDK_INT = Build.VERSION.SDK_INT;
                    if (SDK_INT > 8)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            usuario = new Usuario();
                            String usuario = txtUser.getText().toString();
                            String pass = txtPass.getText().toString();
                            String email = txtEmail.getText().toString();
                            new JsonTask().execute("https://gestor-prenda-heroku.herokuapp.com/client/registrar/"+usuario+"/"+pass+"/"+email);

                        }

                    }
                }
            }
        });
    }
    //String url = "https://gestor-prenda-heroku.herokuapp.com/client/registrar/"+user+"/"+pass+"/"+email;
    //String url = "http://192.168.100.7:8080/client/registrar/"+user+"/"+pass+"/"+email;
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void Regitrar(){
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.e("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
               usuario = objectMapper.readValue(result, Usuario.class);

               if(usuario.getUsername().equals("Existe")){
                   Toast toast = Toast.makeText(getApplicationContext(),
                           "Hubo un problema, El Usuario o Correo ya esta en uso", Toast.LENGTH_LONG);
                   toast.show();
               }else{
                   Toast toast = Toast.makeText(getApplicationContext(),
                           "Usuario Registrado", Toast.LENGTH_LONG);
                   toast.show();

                   Regitrar();
               }
            } catch (JsonProcessingException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hubo un problema, Usuario incorrecto o No existe", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}