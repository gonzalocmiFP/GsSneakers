package com.example.gssneakers.database;

import java.util.List;
import java.util.Map;

public class Product {
    private String id;
    private String nombre;
    private String modelo;
    private String marca;
    private String color;
    private String imagen;
    private double precio;
    private Map<String, Integer> stock;
    private List<String> tallas;

    public Product() {
    }

    public Product(String id, String nombre, String modelo, String marca, String color, String imagen, double precio, Map<String, Integer> stock, List<String> tallas) {
        this.id = id;
        this.nombre = nombre;
        this.modelo = modelo;
        this.marca = marca;
        this.color = color;
        this.imagen = imagen;
        this.precio = precio;
        this.stock = stock;
        this.tallas = tallas;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", modelo='" + modelo + '\'' +
                ", marca='" + marca + '\'' +
                ", color='" + color + '\'' +
                ", imagen='" + imagen + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", tallas=" + tallas +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Map<String, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<String, Integer> stock) {
        this.stock = stock;
    }

    public List<String> getTallas() {
        return tallas;
    }

    public void setTallas(List<String> tallas) {
        this.tallas = tallas;
    }
}
