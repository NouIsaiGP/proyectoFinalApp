package com.example.proyecto_prenda_cliente.utils;


import com.example.proyecto_prenda_cliente.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoginService {

    @GET("/l")
    Call<Usuario> getUsuario();
}
