package hn.uth.poo.biblioteca.dao;

import hn.uth.poo.biblioteca.modelo.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroDAO {
    void insertar(Libro libro);

    Optional<Libro> buscarPorId(int id);

    List<Libro> listar();

    List<Libro> listarPorAutor(int autorId);

    void actualizar(Libro libro);

    void eliminar(int id);
}
