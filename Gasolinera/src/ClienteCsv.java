import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ClienteCsv implements InterfaceCliente {

    private final Path archivo;

    //Constructor
    public ClienteCsv(String nombreArchivo) {
        archivo = Path.of(nombreArchivo);
        crearArchivo();
    }

    // Crea el archivo
    private void crearArchivo() {
        try {
            if (!Files.exists(archivo)) {
                Files.createFile(archivo);
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
        }
    }

    @Override
    public void guardarCliente(String nombre, String telefono, String matricula) {
        // Comprobamos si la matrícula ya está registrada
        if (existeMatricula(matricula)) {
            System.out.println("La matrícula ya está registrada.");
            return;
        }

        int nuevoId = 1;

        try {

            // Si existe el archivo, buscamos el ID más alto
            if (Files.exists(archivo)) {
                List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

                for (String linea : lineas) {

                    if (!linea.isBlank()) {

                        String[] datos = linea.split(";");

                        int id = Integer.parseInt(datos[0]);

                        if (id >= nuevoId) {
                            nuevoId = id + 1;
                        }
                    }
                }
            }

            // Creamos el cliente con el nuevo ID y la matricula en mayusculas
            matricula = matricula.toUpperCase();
            Cliente cliente = new Cliente(nuevoId, nombre, telefono, matricula);

            // Creamos la línea que se guardará en el CSV
            String linea = cliente.getId() + ";" + cliente.getNombre() + ";" + cliente.getTelefono() + ";" + cliente.getMatricula();

            // Guardamos el cliente
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            // Mostramos el resultado
            System.out.println("Cliente guardado correctamente.");
            System.out.println("El identificador del cliente es: " + nuevoId);

        } catch (IOException e) {

            System.out.println("Error al guardar el cliente.");
        }
    }


    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];

                    Cliente cliente = new Cliente(id, nombre, telefono, matricula);
                    clientes.add(cliente);

                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer los clientes");
        }

        return clientes;
    }

    @Override
    public List<Cliente> buscar(String busqueda) {
        List<Cliente> clientes = new ArrayList<>();

        String minusculas = busqueda.toLowerCase();

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];

                    Cliente cliente = new Cliente(id, nombre, telefono, matricula);


                    if (cliente.getNombre()
                            .toLowerCase()
                            .contains(minusculas)
                            || cliente.getTelefono()
                            .toLowerCase()
                            .contains(minusculas)
                            || cliente.getMatricula()
                            .toLowerCase()
                            .contains(minusculas)) {

                        clientes.add(cliente);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al buscar cliente");
        }
        return clientes;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        List<Cliente> clientes = obtenerTodos();

        for (Cliente cliente : clientes) {
            if (cliente.getMatricula().equalsIgnoreCase(matricula)) {
                return true;
            }
        }
        return false;
    }

    public Cliente buscarPorId(int idBuscado) {

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");

                    int id = Integer.parseInt(datos[0]);

                    if (id == idBuscado) {
                        String nombre = datos[1];String telefono = datos[2];String matricula = datos[3];

                        return new Cliente(id, nombre, telefono, matricula);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al buscar cliente");
        }
        return null;
    }
}
