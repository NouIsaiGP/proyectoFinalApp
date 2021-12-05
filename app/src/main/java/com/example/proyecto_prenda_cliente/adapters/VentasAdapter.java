package com.example.proyecto_prenda_cliente.adapters;

import android.content.Context;
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
import com.example.proyecto_prenda_cliente.model.ProductoVendido;
import com.example.proyecto_prenda_cliente.model.Venta;

import java.util.List;

public class VentasAdapter extends ArrayAdapter<Venta> {

    private Context context;
    private List<Venta> ventas;

    public VentasAdapter(@NonNull Context context, int resource, @NonNull List<Venta> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ventas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_compra,parent,false);



        TextView txtNombre = (TextView) view.findViewById(R.id.txtNameProductBougth);
        TextView txtPrecio = (TextView) view.findViewById(R.id.txtPriceProductBought);
        TextView txtEstado = view.findViewById(R.id.txtState);
        String productos = "Productos: \n";

        for(ProductoVendido p : ventas.get(position).getProductos()){
            productos = productos + p.getNombre() + "\n";
        }
        txtNombre.setText(productos);
        txtEstado.setText(ventas.get(position).getEstado());
        txtPrecio.setText(ventas.get(position).getTotal().toString());

        /*txtNombre.setText(String.format("%s", productos));
        txtPrecio.setText(String.format("Precio:%s", ventas.get(position).getTotal()));*/

        return view;
    }

}
