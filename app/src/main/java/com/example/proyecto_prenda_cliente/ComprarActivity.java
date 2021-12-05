package com.example.proyecto_prenda_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyecto_prenda_cliente.adapters.ProductoAdapter;
import com.example.proyecto_prenda_cliente.model.Producto;
import com.example.proyecto_prenda_cliente.model.Usuario;
import com.example.proyecto_prenda_cliente.utils.Apis;
import com.example.proyecto_prenda_cliente.utils.ProductoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComprarActivity extends AppCompatActivity {

    ProductoService productoService;
    public static List<Producto> listaProducto = new ArrayList<>();
    ListView listView;
    public static Button btnBuscar;
    EditText txtSearch;
    public static Button btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comprar);

        listView = (ListView) findViewById(R.id.listViewShop);
        btnBuscar = (Button) findViewById(R.id.btnSearch);
        btnCart = (Button) findViewById(R.id.btnCart);
        txtSearch = findViewById(R.id.txtSearch);

        new JsonTask().execute("https://gestor-prenda-heroku.herokuapp.com/client/listar/");

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = txtSearch.getText().toString();
                url = url.replaceAll(" ", "%20");

                new JsonTask().execute("https://gestor-prenda-heroku.herokuapp.com/client/listar/" + url);
            }
        });

        btnCart.setOnClickListener(v -> {
            Usuario u = MainActivity.usuario;
            if(u.getCad() == null | u.getCalle() == null | u.getCcv() == null | u.getColonia() ==null | u.getEmail() == null |u.getPostal() == null | u.getTarjeta() == null | u.getTelefono() == null){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hubo un prolema, debe de registrar sus datos de usuario", Toast.LENGTH_LONG);
                toast.show();
            }else {
                if (u.getCad().equals("") | u.getCalle().equals("") | u.getCcv().equals("")
                        | u.getColonia().equals("") | u.getEmail().equals("") | u.getPostal().equals("")
                        | u.getTarjeta().equals("") | u.getTelefono().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Hubo un prolema, debe de registrar sus datos de usuario", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    for (Producto p : listaProducto) {
                        String url = "https://gestor-prenda-heroku.herokuapp.com/client/comprar/" + MainActivity.usuario.getUsername() + "/";
                        url = url + p.getId();
                        Log.e("UUUURRRSSSSS", url);
                        new JsonTask2().execute(url);
                    }
                    listaProducto.clear();
                    new JsonTask().execute("https://gestor-prenda-heroku.herokuapp.com/client/listar/");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Se ha apartado tu pedido, espera tu email registrado", Toast.LENGTH_LONG);
                    toast.show();
                    Intent switchActivityIntent = new Intent(this, ComprasHechasActivity.class);
                    startActivity(switchActivityIntent);
                }
            }


        });
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

    private class JsonTask2 extends AsyncTask<String, String, String> {

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
            Log.e("hola", result);
        }
    }

    private void parseJSON(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Producto>>(){}.getType();
        List<Producto> list = gson.fromJson(json, type);
        for (Producto p : list){
            Log.i("Producto: ", p.getNombre() + "-" + p.getPrecio() + "-" + p.getFoto());
        }
        ProductoAdapter pro = new ProductoAdapter(ComprarActivity.this, R.layout.item_product, list);
        listView.setAdapter(pro);
    }
}