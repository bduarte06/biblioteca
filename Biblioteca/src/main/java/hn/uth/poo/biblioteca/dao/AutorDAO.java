package hn.uth.poo.biblioteca.dao;

import hn.uth.poo.biblioteca.modelo.Autor;

import java.util.List;
import java.util.Optional;

public interface AutorDAO {
    void insertar(Autor autor);

    Optional<Autor> buscarPorId(int id);

    List<Autor> listar();

    void actualizar(Autor autor);

    void eliminar(int id);
}
