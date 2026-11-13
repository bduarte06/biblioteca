package hn.uth.poo.biblioteca.servicio;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.excepciones.DatoInvalidoException;
import hn.uth.poo.biblioteca.modelo.Autor;
import hn.uth.poo.biblioteca.modelo.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioLibrosTest {
    private final AutoresEnMemoria autores = new AutoresEnMemoria();
    private final LibrosEnMemoria libros = new LibrosEnMemoria();
    private final ServicioLibros servicio = new ServicioLibros(libros, autores);

    @BeforeEach
    void prepararAutor() {
        autores.autores.clear();
        libros.libros.clear();
        autores.insertar(new Autor(1, "Gabriel Garcia Marquez", "Colombiana"));
    }

    @Test
    void crearAsignaIdYConservaRelacionConAutor() {
        Libro libro = servicio.crear(new Libro(0, "9780307474728", "Cien anos de soledad",
                1967, 250.0, 1));

        assertEquals(1, libro.getId());
        assertEquals(1, servicio.listarPorAutor(1).size());
        assertEquals(1, servicio.listarPorAutor(1).get(0).getAutorId());
    }

    @Test
    void rechazaIsbnRepetido() {
        servicio.crear(new Libro(0, "9780307474728", "Primero", 1967, 100, 1));

        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,
                () -> servicio.crear(new Libro(0, "9780307474728", "Duplicado", 1968, 100, 1)));

        assertEquals("Ya existe un libro registrado con ese ISBN.", error.getMessage());
    }

    @Test
    void rechazaDatosFueraDeReglas() {
        assertThrows(DatoInvalidoException.class,
                () -> servicio.crear(new Libro(0, "1", "Sin autor", 1500, 10, 99)));
        assertThrows(DatoInvalidoException.class,
                () -> servicio.crear(new Libro(0, "2", "Ano futuro", 3000, 10, 1)));
        assertThrows(DatoInvalidoException.class,
                () -> servicio.crear(new Libro(0, "3", "Precio negativo", 2000, -1, 1)));
        assertThrows(DatoInvalidoException.class,
                () -> servicio.crear(new Libro(0, "4", "   ", 2000, 10, 1)));
    }

    @Test
    void rechazaNombreDeAutorVacio() {
        ServicioAutores servicioAutores = new ServicioAutores(autores, libros);

        assertThrows(DatoInvalidoException.class,
                () -> servicioAutores.crear(new Autor(0, "  ", "Hondurena")));
    }

    @Test
    void isbnActualDelMismoLibroSePermiteAlActualizar() {
        Libro libro = servicio.crear(new Libro(0, "isbn-1", "Titulo", 2000, 10, 1));
        libro.setTitulo("Titulo actualizado");

        servicio.actualizar(libro);

        assertEquals("Titulo actualizado", servicio.buscarPorId(libro.getId()).orElseThrow().getTitulo());
    }

    private static class AutoresEnMemoria implements AutorDAO {
        private final List<Autor> autores = new ArrayList<>();

        @Override
        public void insertar(Autor autor) {
            autor.setId(autores.size() + 1);
            autores.add(autor);
        }

        @Override
        public Optional<Autor> buscarPorId(int id) {
            return autores.stream().filter(autor -> autor.getId() == id).findFirst();
        }

        @Override
        public List<Autor> listar() {
            return new ArrayList<>(autores);
        }

        @Override
        public void actualizar(Autor autor) {
            int indice = indiceAutor(autor.getId());
            if (indice >= 0) {
                autores.set(indice, autor);
            }
        }

        @Override
        public void eliminar(int id) {
            autores.removeIf(autor -> autor.getId() == id);
        }

        private int indiceAutor(int id) {
            for (int i = 0; i < autores.size(); i++) {
                if (autores.get(i).getId() == id) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static class LibrosEnMemoria implements LibroDAO {
        private final List<Libro> libros = new ArrayList<>();

        @Override
        public void insertar(Libro libro) {
            libro.setId(libros.size() + 1);
            libros.add(libro);
        }

        @Override
        public Optional<Libro> buscarPorId(int id) {
            return libros.stream().filter(libro -> libro.getId() == id).findFirst();
        }

        @Override
        public List<Libro> listar() {
            return new ArrayList<>(libros);
        }

        @Override
        public List<Libro> listarPorAutor(int autorId) {
            return libros.stream().filter(libro -> libro.getAutorId() == autorId).toList();
        }

        @Override
        public void actualizar(Libro libro) {
            int indice = indiceLibro(libro.getId());
            if (indice >= 0) {
                libros.set(indice, libro);
            }
        }

        @Override
        public void eliminar(int id) {
            libros.removeIf(libro -> libro.getId() == id);
        }

        private int indiceLibro(int id) {
            for (int i = 0; i < libros.size(); i++) {
                if (libros.get(i).getId() == id) {
                    return i;
                }
            }
            return -1;
        }
    }
}
