package hn.uth.poo.biblioteca.servicio;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.excepciones.DatoInvalidoException;
import hn.uth.poo.biblioteca.modelo.Autor;

import java.util.List;
import java.util.Optional;

public class ServicioAutores {
    private final AutorDAO autorDAO;
    private final LibroDAO libroDAO;

    public ServicioAutores(AutorDAO autorDAO, LibroDAO libroDAO) {
        this.autorDAO = autorDAO;
        this.libroDAO = libroDAO;
    }

    public Autor crear(Autor autor) {
        validarNombre(autor.getNombre());
        autorDAO.insertar(autor);
        return autor;
    }

    public Optional<Autor> buscarPorId(int id) {
        return autorDAO.buscarPorId(id);
    }

    public List<Autor> listar() {
        return autorDAO.listar();
    }

    public void actualizar(Autor autor) {
        validarNombre(autor.getNombre());
        exigirExistente(autor.getId());
        autorDAO.actualizar(autor);
    }

    public void eliminar(int id) {
        exigirExistente(id);
        if (!libroDAO.listarPorAutor(id).isEmpty()) {
            throw new DatoInvalidoException("No se puede eliminar el autor porque todavia tiene libros.");
        }
        autorDAO.eliminar(id);
    }

    private void exigirExistente(int id) {
        if (autorDAO.buscarPorId(id).isEmpty()) {
            throw new DatoInvalidoException("No existe un autor con id " + id + ".");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre del autor es obligatorio.");
        }
    }
}
