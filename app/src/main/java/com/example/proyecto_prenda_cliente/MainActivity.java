package com.example.proyecto_prenda_cliente;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_prenda_cliente.model.Usuario;
import com.example.proyecto_prenda_cliente.utils.ProductoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {

    public static Button btnLogin;
    private static HttpURLConnection con;
    ProductoService service;
    public static Usuario usuario;
    public static EditText txtUser, txtPassword;
    public static TextView txtRegistrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtUser = (EditText) findViewById(R.id.txtUser);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUser.getText().toString().equals("")|| txtPassword.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un problema, Los campos no debe esta vacios", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    String user = txtUser.getText().toString();
                    String pass = txtPassword.getText().toString();
                    Volley2(user, pass);
                }
            }
        });

        txtRegistrar = findViewById(R.id.textView3);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Regitrar();
            }
        });

    }

    private String decrypt(final String secret, final String encryptedString) {

        byte[] decodedKey = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decodedKey = Base64.getDecoder().decode(secret);
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            }
            return new String(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Error occured while decrypting data", e);
        }
    }

    private void menu(String user) {
        usuario = new Usuario();
        usuario.setUsername(user);
        Intent switchActivityIntent = new Intent(this, MainMenuActivity.class);
        switchActivityIntent.putExtra("Usuario", usuario);
        startActivity(switchActivityIntent);
    }

    private void Regitrar(){
        Intent switchActivityIntent = new Intent(this, RegistrarActivity.class);
        startActivity(switchActivityIntent);
    }

    private void Volley(String user){
        String URL1 = "https://gestor-prenda-heroku.herokuapp.com/client/login/"+user;
        StringRequest postRequest = new StringRequest(Request.Method.GET, URL1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("login", jsonObject.toString());

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        usuario = objectMapper.readValue(response, Usuario.class);
                    } catch (JsonProcessingException e) {
                        Log.e("error,",e.getMessage());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(postRequest);
    }

    private void Volley2(String user, String pass){
        String URL2 = "https://spring-boot-jwt-prenda.herokuapp.com/api/login";

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("login", jsonObject.toString());

                    if (jsonObject.toString().contains("has iniciado sesion con exito!")) {
                        menu(user);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hubo un problema, Usuario incorrecto o No existe", Toast.LENGTH_LONG);
                toast.show();
            }
        })
        {
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", pass);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(postRequest);
    }
}