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
    public void guardarCliente(Cliente cliente) {
        String linea = cliente.getId() + ";" + cliente.getNombre() + ";" + cliente.getTelefono() + ";" + cliente.getMatricula();
        try {
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error al guardar el cliente");
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
    public List<Cliente> buscarPorNombre(String nombre) {
        List<Cliente> clientes = new ArrayList<>();

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");
                    int id = Integer.parseInt(datos[0]);
                    String nombree = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];

                    Cliente cliente = new Cliente(id, nombree, telefono, matricula);

                    if (cliente.getNombre().equalsIgnoreCase(nombre)) {
                        clientes.add(cliente);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al buscar cliente");
        }
        return List.of();
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

}
