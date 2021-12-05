package com.example.proyecto_prenda_cliente.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proyecto_prenda_cliente.ComprarActivity;
import com.example.proyecto_prenda_cliente.R;
import com.example.proyecto_prenda_cliente.model.Producto;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends ArrayAdapter<Producto> {

    private Context context;
    private List<Producto> productos;
    Bitmap bitmap;



    public ProductoAdapter(@NonNull Context context, int resource, @NonNull List<Producto> objects) {
        super(context, resource, objects);
        this.context = context;
        this.productos = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_product,parent,false);

        String fileName = productos.get(position).getFoto();
        String urlBase = "https://gestor-prenda-heroku.herokuapp.com/productos/mostrar/up/";
        String pathUrl = urlBase + fileName;

        TextView txtNombre = (TextView) view.findViewById(R.id.txtNameProduct);
        TextView txtPrecio = (TextView) view.findViewById(R.id.txtPriceProduct);
        ImageView img = (ImageView) view.findViewById(R.id.imageProduct);
        Button btnAdd = (Button) view.findViewById(R.id.btnAdd);

        for (Producto p : ComprarActivity.listaProducto){

            if(p.getNombre().equals(productos.get(position).getNombre())){
                Log.e("Producto: ", p.getNombre());
                btnAdd.setText("QUITAR");
            }

        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAdd.getText().equals("AGREGAR")){
                    ComprarActivity.listaProducto.add(productos.get(position));
                    btnAdd.setText("QUITAR");
                } else if(btnAdd.getText().equals("QUITAR")){
                        if( ComprarActivity.listaProducto.contains(productos.get(position)) ){

                            ComprarActivity.listaProducto.remove(productos.get(position));
                            btnAdd.setText("AGREGAR");
                        }
                }
                ComprarActivity.btnCart.setText("(" + ComprarActivity.listaProducto.size() + ")");

            }
        });

        txtNombre.setText(String.format("%s", productos.get(position).getNombre()));
        txtPrecio.setText(String.format("Precio:%s", productos.get(position).getPrecio()));
        new GetImageFromUrl(img).execute(pathUrl);

        return view;
    }

    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap>{
        ImageView imageView;
        public GetImageFromUrl(ImageView img){
            this.imageView = img;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            bitmap = null;
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

}
