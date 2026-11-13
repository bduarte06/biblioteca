package hn.uth.poo.biblioteca.servicio;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.excepciones.DatoInvalidoException;
import hn.uth.poo.biblioteca.modelo.Autor;
import hn.uth.poo.biblioteca.modelo.Libro;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioAutoresTest {
    @Test
    void impideEliminarAutorConLibros() {
        AutorDAO autorDAO = new AutorDAO() {
            @Override
            public void insertar(Autor autor) {
            }

            @Override
            public Optional<Autor> buscarPorId(int id) {
                return Optional.of(new Autor(id, "Autora", null));
            }

            @Override
            public List<Autor> listar() {
                return List.of();
            }

            @Override
            public void actualizar(Autor autor) {
            }

            @Override
            public void eliminar(int id) {
            }
        };
        LibroDAO libroDAO = new LibroDAO() {
            @Override
            public void insertar(Libro libro) {
            }

            @Override
            public Optional<Libro> buscarPorId(int id) {
                return Optional.empty();
            }

            @Override
            public List<Libro> listar() {
                return List.of();
            }

            @Override
            public List<Libro> listarPorAutor(int autorId) {
                return List.of(new Libro(1, "isbn", "Libro", null, 0, autorId));
            }

            @Override
            public void actualizar(Libro libro) {
            }

            @Override
            public void eliminar(int id) {
            }
        };
        ServicioAutores servicio = new ServicioAutores(autorDAO, libroDAO);

        DatoInvalidoException error = assertThrows(DatoInvalidoException.class, () -> servicio.eliminar(1));

        assertEquals("No se puede eliminar el autor porque todavia tiene libros.", error.getMessage());
    }
}
