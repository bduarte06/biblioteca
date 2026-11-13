package hn.uth.poo.biblioteca.dao;

import hn.uth.poo.biblioteca.dao.impl.AutorDAOImpl;
import hn.uth.poo.biblioteca.dao.impl.LibroDAOImpl;
import hn.uth.poo.biblioteca.excepciones.DatosException;
import hn.uth.poo.biblioteca.modelo.Autor;
import hn.uth.poo.biblioteca.modelo.Libro;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaoSqliteTest {
    private static AutorDAO autores;
    private static LibroDAO libros;
    private static Path archivoBase;
    private static Path directorioTemporal;

    @BeforeAll
    static void prepararBaseTemporal() throws Exception {
        directorioTemporal = Files.createTempDirectory("biblioteca-test");
        archivoBase = directorioTemporal.resolve("biblioteca.db");
        System.setProperty("biblioteca.db.path", archivoBase.toString());
        autores = new AutorDAOImpl();
        libros = new LibroDAOImpl();
    }

    @AfterAll
    static void eliminarBaseTemporal() throws Exception {
        System.clearProperty("biblioteca.db.path");
        Files.deleteIfExists(archivoBase.resolveSibling("biblioteca.db-shm"));
        Files.deleteIfExists(archivoBase.resolveSibling("biblioteca.db-wal"));
        Files.deleteIfExists(archivoBase);
        Files.deleteIfExists(directorioTemporal);
    }

    @BeforeEach
    void limpiarDatos() throws Exception {
        try (Connection conexion = Conexion.obtenerConexion();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("DELETE FROM libros");
            sentencia.executeUpdate("DELETE FROM autores");
        }
    }

    @Test
    void insertarGeneraIdsYMapeaRelaciones() {
        Autor autor = new Autor(0, "Autora de prueba", "Hondurena");
        autores.insertar(autor);
        Libro libro = new Libro(0, "9780000000001", "Libro de prueba", 2020, 0, autor.getId());
        libros.insertar(libro);

        assertTrue(autor.getId() > 0);
        assertTrue(libro.getId() > 0);
        assertEquals(autor.getId(), autores.buscarPorId(autor.getId()).orElseThrow().getId());
        assertEquals(libro.getTitulo(), libros.buscarPorId(libro.getId()).orElseThrow().getTitulo());
        assertEquals(libro.getId(), libros.listarPorAutor(autor.getId()).get(0).getId());
        assertEquals(1, libros.listar().size());
    }

    @Test
    void cadaConexionActivaClavesForaneasYConservaCausaSql() {
        try (Connection conexion = Conexion.obtenerConexion();
             Statement sentencia = conexion.createStatement();
             var resultado = sentencia.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(resultado.next());
            assertEquals(1, resultado.getInt(1));
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        DatosException error = assertThrows(DatosException.class,
                () -> libros.insertar(new Libro(0, "9780000000002", "Sin autor", null, 10, 9999)));
        assertInstanceOf(SQLException.class, error.getCause());
    }
}
