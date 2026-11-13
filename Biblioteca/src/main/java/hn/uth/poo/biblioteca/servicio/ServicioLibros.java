package hn.uth.poo.biblioteca.servicio;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.excepciones.DatoInvalidoException;
import hn.uth.poo.biblioteca.modelo.Libro;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public class ServicioLibros {
    private final LibroDAO libroDAO;
    private final AutorDAO autorDAO;

    public ServicioLibros(LibroDAO libroDAO, AutorDAO autorDAO) {
        this.libroDAO = libroDAO;
        this.autorDAO = autorDAO;
    }

    public Libro crear(Libro libro) {
        validar(libro);
        validarISBNUnico(libro.getIsbn(), 0);
        validarAutorExistente(libro.getAutorId());
        libroDAO.insertar(libro);
        return libro;
    }

    public Optional<Libro> buscarPorId(int id) {
        return libroDAO.buscarPorId(id);
    }

    public List<Libro> listar() {
        return libroDAO.listar();
    }

    public List<Libro> listarPorAutor(int autorId) {
        validarAutorExistente(autorId);
        return libroDAO.listarPorAutor(autorId);
    }

    public void actualizar(Libro libro) {
        validar(libro);
        if (libroDAO.buscarPorId(libro.getId()).isEmpty()) {
            throw new DatoInvalidoException("No existe un libro con id " + libro.getId() + ".");
        }
        validarISBNUnico(libro.getIsbn(), libro.getId());
        validarAutorExistente(libro.getAutorId());
        libroDAO.actualizar(libro);
    }

    public void eliminar(int id) {
        if (libroDAO.buscarPorId(id).isEmpty()) {
            throw new DatoInvalidoException("No existe un libro con id " + id + ".");
        }
        libroDAO.eliminar(id);
    }

    private void validar(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new DatoInvalidoException("El titulo del libro es obligatorio.");
        }
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new DatoInvalidoException("El ISBN del libro es obligatorio.");
        }
        libro.setIsbn(libro.getIsbn().trim());
        if (libro.getAnio() != null
                && (libro.getAnio() < 1450 || libro.getAnio() > Year.now().getValue())) {
            throw new DatoInvalidoException("El ano debe estar entre 1450 y " + Year.now().getValue() + ".");
        }
        if (!Double.isFinite(libro.getPrecio()) || libro.getPrecio() < 0) {
            throw new DatoInvalidoException("El precio debe ser un numero mayor o igual a 0.");
        }
    }

    private void validarISBNUnico(String isbn, int idExcluido) {
        boolean repetido = libroDAO.listar().stream()
                .anyMatch(libro -> libro.getIsbn().equalsIgnoreCase(isbn.trim()) && libro.getId() != idExcluido);
        if (repetido) {
            throw new DatoInvalidoException("Ya existe un libro registrado con ese ISBN.");
        }
    }

    private void validarAutorExistente(int autorId) {
        if (autorDAO.buscarPorId(autorId).isEmpty()) {
            throw new DatoInvalidoException("No existe un autor con id " + autorId + ".");
        }
    }
}
