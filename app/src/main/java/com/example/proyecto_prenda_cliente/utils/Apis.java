package com.example.proyecto_prenda_cliente.utils;

public class Apis {
    public static final String urlProducto = "http://192.168.100.7:8080/client/";
    public static final String url = "http://192.168.100.7:8080/client/login/noe/";


    public static ProductoService getProductoService(){
        return ClientRetrofit.getProductos(urlProducto).create(ProductoService.class);
    }


}
