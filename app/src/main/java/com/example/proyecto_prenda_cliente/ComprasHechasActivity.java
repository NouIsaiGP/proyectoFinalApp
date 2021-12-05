package com.example.proyecto_prenda_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_prenda_cliente.adapters.ProductoAdapter;
import com.example.proyecto_prenda_cliente.adapters.VentasAdapter;
import com.example.proyecto_prenda_cliente.model.Producto;
import com.example.proyecto_prenda_cliente.model.Usuario;
import com.example.proyecto_prenda_cliente.model.Venta;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComprasHechasActivity extends AppCompatActivity {

    public static List<Venta> ventas = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras_hechas);

        listView = findViewById(R.id.listCompras);

        new JsonTask().execute("https://gestor-prenda-heroku.herokuapp.com/client/compras/"+MainActivity.usuario.getUsername());
    }

    public void Menu(View view) {
        Intent switchActivityIntent = new Intent(this, MainMenuActivity.class);
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
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

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
            parseJSON(result);
            Log.e("hola", result);
        }
    }


    private void parseJSON(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Venta>>(){}.getType();
        List<Venta> list = gson.fromJson(json, type);
        for (Venta v : list){
            Log.i("Producto: ", v.getId() + " - " + v.getTotal() + " - " + v.getEstado() + " - " );
        }
        VentasAdapter ven = new VentasAdapter(ComprasHechasActivity.this, R.layout.item_compra, list);
        listView.setAdapter(ven);
    }
}