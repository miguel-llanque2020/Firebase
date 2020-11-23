package com.example.firebasedatabaseexamplem.Entidad;

import androidx.annotation.NonNull;

public class Categoria {
    String id_categoria;
    String nombre_categoria;

    public  Categoria(){

    }
    public Categoria(String id_categoria, String nombre_categoria) {
        this.id_categoria = id_categoria;
        this.nombre_categoria = nombre_categoria;
    }

    public String getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre_categoria() {
        return nombre_categoria;
    }

    public void setNombre_categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getNombre_categoria();
    }
}
