package com.example.gssneakers.database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

/**
 * Representación del objeto guardada en la colección de Firestore "carrito".
 */
public class CartProduct {
    String id;
    String referencePath;
    String talla;
    Integer cantidad;

    public CartProduct() {
    }

    public CartProduct(String id, DocumentReference referenceToRealProduct, String talla, Integer cantidad) {
        this.id = id;
        this.referencePath = referenceToRealProduct != null ? referenceToRealProduct.getPath() : null;
        this.talla = talla;
        this.cantidad = cantidad;
    }

    public DocumentReference getReferenceToRealProduct(FirebaseFirestore db) {
        return referencePath != null ? db.document(referencePath) : null;
    }

    public void setReferenceToRealProduct(DocumentReference referenceToRealProduct) {
        this.referencePath = referenceToRealProduct != null ? referenceToRealProduct.getPath() : null;
    }

    public String toString(){
        return new Gson().toJson(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
