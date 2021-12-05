package com.example.proyecto_prenda_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_prenda_cliente.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends AppCompatActivity {

    public static TextView txtWelcomMessage;
    public static Button btnUserSettings, btnShoppingCart, btnUserPurchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Volley(MainActivity.usuario.getUsername());
        setContentView(R.layout.activity_main_menu);

        txtWelcomMessage = findViewById(R.id.textWelcomMessage);
        btnShoppingCart = findViewById(R.id.btnShoppingCart);
        btnUserSettings = findViewById(R.id.btnUserSettings);
        btnUserPurchases = findViewById(R.id.btnUserPurchases);
//        Log.e("nameeeee", MainActivity.usuario.getUsername());
        if(MainActivity.usuario.getUsername() != null) {
            String welcomMessage = txtWelcomMessage.getText().toString() + "\n" + MainActivity.usuario.getUsername();
            txtWelcomMessage.setText(welcomMessage);
        }


        btnUserSettings.setOnClickListener(v -> {
            Volley(MainActivity.usuario.getUsername());Volley(MainActivity.usuario.getUsername());
                Intent switchActivityIntent = new Intent(this, UserSettingsActivity.class);
                startActivity(switchActivityIntent);
        });

        btnShoppingCart.setOnClickListener(v -> {
            Intent switchActivityIntent = new Intent(this, ComprarActivity.class);
            startActivity(switchActivityIntent);
        });

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
                        MainActivity.usuario = objectMapper.readValue(response, Usuario.class);
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

    public void tusCompras(View view) {
        Intent switchActivityIntent = new Intent(this, ComprasHechasActivity.class);
        startActivity(switchActivityIntent);
    }
}