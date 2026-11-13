package hn.uth.poo.biblioteca.dao.impl;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.Conexion;
import hn.uth.poo.biblioteca.excepciones.DatosException;
import hn.uth.poo.biblioteca.modelo.Autor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorDAOImpl implements AutorDAO {
    private static final String COLUMNAS = "id, nombre, nacionalidad";

    @Override
    public void insertar(Autor autor) {
        String sql = "INSERT INTO autores (nombre, nacionalidad) VALUES (?, ?)";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, autor.getNombre());
            sentencia.setString(2, autor.getNacionalidad());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    autor.setId(claves.getInt(1));
                } else {
                    throw new SQLException("SQLite no devolvio el id generado para el autor.");
                }
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo insertar el autor.", e);
        }
    }

    @Override
    public Optional<Autor> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM autores WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("No se pudo buscar el autor.", e);
        }
    }

    @Override
    public List<Autor> listar() {
        String sql = "SELECT " + COLUMNAS + " FROM autores ORDER BY nombre, id";
        List<Autor> autores = new ArrayList<>();
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                autores.add(mapear(resultado));
            }
            return autores;
        } catch (SQLException e) {
            throw new DatosException("No se pudieron listar los autores.", e);
        }
    }

    @Override
    public void actualizar(Autor autor) {
        String sql = "UPDATE autores SET nombre = ?, nacionalidad = ? WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, autor.getNombre());
            sentencia.setString(2, autor.getNacionalidad());
            sentencia.setInt(3, autor.getId());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("No se pudo actualizar el autor.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM autores WHERE id = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("No se pudo eliminar el autor.", e);
        }
    }

    private Autor mapear(ResultSet resultado) throws SQLException {
        return new Autor(resultado.getInt("id"), resultado.getString("nombre"),
                resultado.getString("nacionalidad"));
    }
}
