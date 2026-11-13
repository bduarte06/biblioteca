package hn.uth.poo.biblioteca.dao.impl;

import hn.uth.poo.biblioteca.dao.Conexion;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.excepciones.DatosException;
import hn.uth.poo.biblioteca.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAOImpl implements LibroDAO {
    private static final String COLUMNAS = "id, isbn, titulo, anio, precio, autor_id";

    @Override
    public void insertar(Libro libro) {
        String sql = "INSERT INTO libros (isbn, titulo, anio, precio, autor_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, libro.getIsbn());
            sentencia.setString(2, libro.getTitulo());
            if (libro.getAnio() == null) {
                sentencia.setNull(3, java.sql.Types.INTEGER);
            } else {
                sentencia.setInt(3, libro.getAnio());
            }
            sentencia.setDouble(4, libro.getPrecio());
            sentencia.setInt(5, libro.getAutorId());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    libro.setId(claves.getInt(1));
                } else {
                    throw new SQLException("SQLite no devolvio el id generado para el libro.");
                }
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo insertar el libro. Verifique que el ISBN no este registrado.",
                    e);
        }
    }

    @Override
    public Optional<Libro> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM libros WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo buscar el libro.", e);
        }
    }

    @Override
    public List<Libro> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM libros ORDER BY titulo, id", null);
    }

    @Override
    public List<Libro> listarPorAutor(int autorId) {
        return consultar("SELECT " + COLUMNAS + " FROM libros WHERE autor_id = ? ORDER BY titulo, id", autorId);
    }

    private List<Libro> consultar(String sql, Integer autorId) {
        List<Libro> libros = new ArrayList<>();
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (autorId != null) {
                sentencia.setInt(1, autorId);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    libros.add(mapear(resultado));
                }
            }
            return libros;
        } catch (SQLException e) {
            throw new DatosException("No se pudieron listar los libros.", e);
        }
    }

    @Override
    public void actualizar(Libro libro) {
        String sql = "UPDATE libros SET isbn = ?, titulo = ?, anio = ?, precio = ?, autor_id = ? WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, libro.getIsbn());
            sentencia.setString(2, libro.getTitulo());
            if (libro.getAnio() == null) {
                sentencia.setNull(3, java.sql.Types.INTEGER);
            } else {
                sentencia.setInt(3, libro.getAnio());
            }
            sentencia.setDouble(4, libro.getPrecio());
            sentencia.setInt(5, libro.getAutorId());
            sentencia.setInt(6, libro.getId());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("No se pudo actualizar el libro. Verifique ISBN y autor.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("No se pudo eliminar el libro.", e);
        }
    }

    private Libro mapear(ResultSet resultado) throws SQLException {
        int anioLeido = resultado.getInt("anio");
        Integer anio = resultado.wasNull() ? null : anioLeido;
        return new Libro(resultado.getInt("id"), resultado.getString("isbn"), resultado.getString("titulo"),
                anio, resultado.getDouble("precio"), resultado.getInt("autor_id"));
    }
}
