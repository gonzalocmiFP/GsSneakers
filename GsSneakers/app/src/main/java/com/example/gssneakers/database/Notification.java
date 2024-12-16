package com.example.gssneakers.database;

public class Notification {
    private String titulo;
    private String cuerpo;
    private String imagen;

    public Notification(){
    }

    public Notification(String titulo, String cuerpo, String imagen) {
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
