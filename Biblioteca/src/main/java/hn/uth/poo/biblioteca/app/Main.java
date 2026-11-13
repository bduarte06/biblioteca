package hn.uth.poo.biblioteca.app;

import hn.uth.poo.biblioteca.dao.AutorDAO;
import hn.uth.poo.biblioteca.dao.LibroDAO;
import hn.uth.poo.biblioteca.dao.impl.AutorDAOImpl;
import hn.uth.poo.biblioteca.dao.impl.LibroDAOImpl;
import hn.uth.poo.biblioteca.modelo.Autor;
import hn.uth.poo.biblioteca.modelo.Libro;
import hn.uth.poo.biblioteca.servicio.ServicioAutores;
import hn.uth.poo.biblioteca.servicio.ServicioLibros;

import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private final Scanner scanner;
    private final ServicioAutores servicioAutores;
    private final ServicioLibros servicioLibros;

    private Main(Scanner scanner, ServicioAutores servicioAutores, ServicioLibros servicioLibros) {
        this.scanner = scanner;
        this.servicioAutores = servicioAutores;
        this.servicioLibros = servicioLibros;
    }

    public static void main(String[] args) {
        AutorDAO autorDAO = new AutorDAOImpl();
        LibroDAO libroDAO = new LibroDAOImpl();
        ServicioAutores servicioAutores = new ServicioAutores(autorDAO, libroDAO);
        ServicioLibros servicioLibros = new ServicioLibros(libroDAO, autorDAO);

        try (Scanner scanner = new Scanner(System.in)) {
            new Main(scanner, servicioAutores, servicioLibros).ejecutar();
        }
    }

    private void ejecutar() {
        while (true) {
            imprimir("\n biblioteca ");
            imprimir("1. gestionar autores");
            imprimir("2. gestionar libros");
            imprimir("0. salir");
            escribir("opcion: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String opcion = scanner.nextLine().trim();
            if (opcion.equals("0")) {
                imprimir("hasta pronto.");
                return;
            }
            try {
                switch (opcion) {
                    case "1" -> gestionarAutores();
                    case "2" -> gestionarLibros();
                    default -> imprimir("opcion no valida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void gestionarAutores() {
        while (true) {
            imprimir("\n autores ");
            imprimir("1. crear  2. listar  3. buscar por id  4. actualizar  5. eliminar  0. volver");
            escribir("opcion: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String opcion = scanner.nextLine().trim();
            if (opcion.equals("0")) {
                return;
            }
            try {
                switch (opcion) {
                    case "1" -> crearAutor();
                    case "2" -> servicioAutores.listar().forEach(autor -> imprimir(autor.toString()));
                    case "3" -> buscarAutor();
                    case "4" -> actualizarAutor();
                    case "5" -> eliminarAutor();
                    default -> imprimir("opcion no valida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void gestionarLibros() {
        while (true) {
            imprimir("\n libros ");
            imprimir("1. crear  2. listar  3. buscar por id  4. actualizar  5. eliminar");
            imprimir("6. listar por autor  0. volver");
            escribir("opcion: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String opcion = scanner.nextLine().trim();
            if (opcion.equals("0")) {
                return;
            }
            try {
                switch (opcion) {
                    case "1" -> crearLibro();
                    case "2" -> servicioLibros.listar().forEach(libro -> imprimir(libro.toString()));
                    case "3" -> buscarLibro();
                    case "4" -> actualizarLibro();
                    case "5" -> eliminarLibro();
                    case "6" -> listarLibrosPorAutor();
                    default -> imprimir("opcion no valida.");
                }
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    private void crearAutor() {
        Autor autor = new Autor(0, leerTexto("nombre: "), leerTextoOpcional("nacionalidad (opcional): "));
        servicioAutores.crear(autor);
        imprimir("autor creado: " + autor);
    }

    private void buscarAutor() {
        int id = leerEntero("id del autor: ");
        Optional<Autor> autor = servicioAutores.buscarPorId(id);
        imprimir(autor.map(Object::toString).orElse("no se encontro el autor."));
    }

    private void actualizarAutor() {
        int id = leerEntero("id del autor: ");
        Autor autor = new Autor(id, leerTexto("nombre: "), leerTextoOpcional("nacionalidad (opcional): "));
        servicioAutores.actualizar(autor);
        imprimir("autor actualizado: " + autor);
    }

    private void eliminarAutor() {
        int id = leerEntero("id del autor: ");
        servicioAutores.eliminar(id);
        imprimir("autor eliminado.");
    }

    private void crearLibro() {
        Libro libro = leerDatosLibro(0);
        servicioLibros.crear(libro);
        imprimir("libro creado: " + libro);
    }

    private void buscarLibro() {
        int id = leerEntero("id del libro: ");
        Optional<Libro> libro = servicioLibros.buscarPorId(id);
        imprimir(libro.map(Object::toString).orElse("no se encontro el libro."));
    }

    private void actualizarLibro() {
        int id = leerEntero("id del libro: ");
        Libro libro = leerDatosLibro(id);
        servicioLibros.actualizar(libro);
        imprimir("libro actualizado: " + libro);
    }

    private void eliminarLibro() {
        int id = leerEntero("id del libro: ");
        servicioLibros.eliminar(id);
        imprimir("libro eliminado.");
    }

    private void listarLibrosPorAutor() {
        int autorId = leerEntero("id del autor: ");
        servicioLibros.listarPorAutor(autorId).forEach(libro -> imprimir(libro.toString()));
    }

    private Libro leerDatosLibro(int id) {
        String isbn = leerTexto("isbn: ").trim();
        String titulo = leerTexto("titulo: ");
        Integer anio = leerAnio();
        double precio = leerDecimal("precio: ");
        int autorId = leerEntero("id del autor: ");
        return new Libro(id, isbn, titulo, anio, precio, autorId);
    }

    private String leerTexto(String mensaje) {
        escribir(mensaje);
        return scanner.nextLine();
    }

    private String leerTextoOpcional(String mensaje) {
        escribir(mensaje);
        String valor = scanner.nextLine().trim();
        return valor.isEmpty() ? null : valor;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            escribir(mensaje);
            String valor = scanner.nextLine().trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                imprimir("ingrese un numero entero valido.");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            escribir(mensaje);
            String valor = scanner.nextLine().trim();
            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                imprimir("ingrese un numero valido (use punto para los decimales).");
            }
        }
    }

    private Integer leerAnio() {
        while (true) {
            escribir("ano de publicacion (enter si se desconoce): ");
            String valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                return null;
            }
            try {
                return Integer.valueOf(valor);
            } catch (NumberFormatException e) {
                imprimir("el ano debe ser un numero entero.");
            }
        }
    }

    private void mostrarError(RuntimeException e) {
        String mensaje = e.getMessage();
        imprimir("error: " + (mensaje == null ? "no se pudo completar la operacion." : mensaje));
    }

    private void imprimir(String texto) {
        System.out.println(texto.toLowerCase(Locale.ROOT));
    }

    private void escribir(String texto) {
        System.out.print(texto.toLowerCase(Locale.ROOT));
    }
}
