package hn.uth.poo.biblioteca.modelo;

public class Libro {
    private int id;
    private String isbn;
    private String titulo;
    private Integer anio;
    private double precio;
    private int autorId;

    public Libro() {
    }

    public Libro(int id, String isbn, String titulo, Integer anio, double precio, int autorId) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.anio = anio;
        this.precio = precio;
        this.autorId = autorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getAutorId() {
        return autorId;
    }

    public void setAutorId(int autorId) {
        this.autorId = autorId;
    }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", isbn='" + isbn + "', titulo='" + titulo + "', anio=" + anio
                + ", precio=" + precio + ", autorId=" + autorId + "}";
    }
}
