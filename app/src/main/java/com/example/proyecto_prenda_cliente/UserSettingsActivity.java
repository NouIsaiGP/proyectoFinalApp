package com.example.proyecto_prenda_cliente;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_prenda_cliente.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UserSettingsActivity extends AppCompatActivity {

    private Usuario usuario;
    private EditText txtUserSettings, txtEmailSettings, txtAddres,
    txtCodigoPostal, txtColonia, txtTel, txtCardNumber, txtCCV, txtCAD;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        txtAddres = findViewById(R.id.txtAddres);
        txtUserSettings = findViewById(R.id.txtUserSettings);
        txtEmailSettings = findViewById(R.id.txtEmailSettings);
        txtCodigoPostal = findViewById(R.id.txtCodigoPostal);
        txtColonia = findViewById(R.id.txtColonia);
        txtTel = findViewById(R.id.txtTel);
        txtCardNumber = findViewById(R.id.txtCardNumber);
        txtCCV = findViewById(R.id.txtCCV);
        txtCAD = findViewById(R.id.txtCAD);
        btnAdd = findViewById(R.id.btnAddUserSettings);

        usuario = MainActivity.usuario;

        if(usuario != null){
            actualizarDatos();
        }

        txtUserSettings.setEnabled(false);
        txtEmailSettings.setEnabled(false);

        btnAdd.setOnClickListener(v -> {
            if(txtAddres.getText().toString().equals("") || txtCodigoPostal.getText().equals("") || txtColonia.getText().equals("")||txtTel.getText().toString().equals("")||
                    txtCardNumber.getText().toString().equals("")||txtCCV.getText().toString().equals("")||txtCAD.getText().toString().equals("")){
                Toast toast = Toast.makeText(this, "Hubo un problema, uno o mas campos estan vacios", Toast.LENGTH_LONG);
                toast.show();
            }else if (Pattern.matches("[a-zA-Z]+", txtTel.getText().toString()) == false && txtTel.getText().length() != 10 ) {
                Toast toast = Toast.makeText(this, "Hubo un problema, debe ingresar un 'Telefono' valido", Toast.LENGTH_LONG);
                toast.show();
            }else if (Pattern.matches("[a-zA-Z]+", txtCardNumber.getText().toString()) == false && txtCardNumber.getText().length() != 16 ) {
                Toast toast = Toast.makeText(this, "Hubo un problema, debe ingresar un 'Numero de Tarjeta' valido", Toast.LENGTH_LONG);
                toast.show();
            }else if (Pattern.matches("[a-zA-Z]+", txtCCV.getText().toString()) == false && txtCCV.getText().length() != 3 ) {
                Toast toast = Toast.makeText(this, "Hubo un problema, debe de ingresar un 'Codigo de Seguridad' valido", Toast.LENGTH_LONG);
                toast.show();
            }else if (Pattern.matches("[a-zA-Z]+", txtCodigoPostal.getText().toString()) == false && txtCodigoPostal.getText().length() != 5 ) {
                Toast toast = Toast.makeText(this, "Hubo un problema, debe ingresar un 'Codigo Postal' valido", Toast.LENGTH_LONG);
                toast.show();
            }else if(Pattern.matches("\\d{1,2}-\\d{4}", txtCAD.getText().toString()) == false) {
                Toast toast = Toast.makeText(this, "Hubo un problema, deeb ingresar el patron 'mm-yyyy' en el vencimiento de la tarjeta", Toast.LENGTH_LONG);
                toast.show();
            }else{
                String ccvEncrypted = "";
                String TarjetaEncrypted = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ccvEncrypted = encrypt("hola", txtCCV.getText().toString());
                    ccvEncrypted = ccvEncrypted.replaceAll("/","911");

                    TarjetaEncrypted = encrypt("hola", txtCardNumber.getText().toString());
                    TarjetaEncrypted = TarjetaEncrypted.replaceAll("/", "911");

                }

                Log.e("pru", ccvEncrypted);
                Log.e("pru",TarjetaEncrypted);



                String url = "https://gestor-prenda-heroku.herokuapp.com/client/usuario/configurar/" + txtUserSettings.getText().toString() + "/"
                        + txtTel.getText().toString() + "/"
                        + txtCAD.getText().toString() + "/"
                        + txtAddres.getText().toString() + "/"
                        + ccvEncrypted + "/"
                        + txtColonia.getText().toString() + "/"
                        + txtCodigoPostal.getText().toString() + "/"
                        + TarjetaEncrypted;

                String url2 = url.replaceAll(" ", "%20");

                Log.e("pru2",url2);

                new JsonTask().execute(url2);
                    //Log.e("urs", url2);
                Toast toast = Toast.makeText(this, "Usuario actualizado con exito", Toast.LENGTH_LONG);
                toast.show();


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
                    buffer.append(line+"\n");
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
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                usuario = objectMapper.readValue(result, Usuario.class);
                MainActivity.usuario = usuario;
                actualizarDatos();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Log.e("hola",result);
        }
    }

    public void actualizarDatos(){
        txtAddres.setText(usuario.getCalle());
        txtUserSettings.setText(usuario.getUsername());
        txtEmailSettings.setText(usuario.getEmail());
        txtCodigoPostal.setText(usuario.getPostal());
        txtColonia.setText(usuario.getColonia());
        txtTel.setText(usuario.getTelefono());
        Log.e("pru2", usuario.getTarjeta() + " ccv " + usuario.getCcv());
        if(usuario.getTarjeta() != "" && usuario.getCcv() != "" && usuario.getEmail() != null && usuario.getCcv() != null) {
            String tarjeta = decrypt("hola", usuario.getTarjeta());
            String CCV = decrypt("hola", usuario.getCcv());
            Log.e("pru", tarjeta + " ccv " + CCV);
            txtCardNumber.setText(tarjeta);
            txtCCV.setText(CCV);
        }
        txtCAD.setText(usuario.getCad());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String encrypt(final String secret, final String data) {

        byte[] decodedKey = Base64.getDecoder().decode(secret);

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Error occured while encrypting data", e);
        }

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
}