package com.example.proyecto_prenda_cliente.utils;

import com.example.proyecto_prenda_cliente.model.Producto;
import com.example.proyecto_prenda_cliente.model.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProductoService {

    @GET("listar")
    Call<List<Producto>> getProductos();

    @GET("login/{username}")
    Call<Usuario> checkUser(@Path("username") String username);

}
