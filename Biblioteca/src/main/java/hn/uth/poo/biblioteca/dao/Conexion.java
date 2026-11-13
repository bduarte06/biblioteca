package hn.uth.poo.biblioteca.dao;

import hn.uth.poo.biblioteca.excepciones.DatosException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexion {
    private static final String RUTA_BASE = System.getProperty("biblioteca.db.path", "biblioteca.db");
    private static final String URL = "jdbc:sqlite:" + RUTA_BASE;

    private Conexion() {
    }

    public static Connection obtenerConexion() {
        try {
            Connection conexion = DriverManager.getConnection(URL);
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.execute("PRAGMA foreign_keys = ON");
                sentencia.execute("""
                        CREATE TABLE IF NOT EXISTS autores (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nombre TEXT NOT NULL,
                            nacionalidad TEXT
                        )
                        """);
                sentencia.execute("""
                        CREATE TABLE IF NOT EXISTS libros (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            isbn TEXT NOT NULL UNIQUE,
                            titulo TEXT NOT NULL,
                            anio INTEGER,
                            precio REAL NOT NULL,
                            autor_id INTEGER NOT NULL REFERENCES autores(id)
                        )
                        """);
            } catch (SQLException e) {
                conexion.close();
                throw e;
            }
            return conexion;
        } catch (SQLException e) {
            throw new DatosException("No se pudo conectar o preparar la base de datos SQLite.", e);
        }
    }
}
