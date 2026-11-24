package gasolinera.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Estacion {

    private final int id;
    private final String codigo;
    private String nombre;
    private String direccion;
    private String localidad;
    private boolean activa;
    private ConfiguracionFacturacion configuracionFacturacion;
    private final List<Surtidor> surtidores = new ArrayList<>();

    public Estacion(int id, String codigo, String nombre, String direccion, String localidad, boolean activa) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        setNombre(nombre);
        setDireccion(direccion);
        setLocalidad(localidad);
        this.activa = activa;
    }

    public void configurarFacturacion(ConfiguracionFacturacion configuracion) {
        Objects.requireNonNull(configuracion, "La configuración no puede ser null.");
        if (configuracion.getEstacion() != this) {
            throw new IllegalArgumentException("La configuración debe pertenecer a esta estación.");
        }
        if (configuracionFacturacion != null && configuracionFacturacion != configuracion) {
            throw new IllegalStateException("La estación ya tiene una configuración de facturación.");
        }
        this.configuracionFacturacion = configuracion;
    }

    public void agregarSurtidor(Surtidor surtidor) {
        Objects.requireNonNull(surtidor, "El surtidor no puede ser null.");
        if (surtidor.getEstacion() != this) {
            throw new IllegalArgumentException("El surtidor debe pertenecer a esta estación.");
        }
        if (surtidores.stream().anyMatch(actual -> actual.getCodigo().equals(surtidor.getCodigo()))) {
            throw new IllegalArgumentException("Ya existe un surtidor con ese código en la estación.");
        }
        surtidores.add(surtidor);
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = Producto.validarTexto(direccion, "La dirección"); }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = Producto.validarTexto(localidad, "La localidad"); }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public ConfiguracionFacturacion getConfiguracionFacturacion() { return configuracionFacturacion; }
    public List<Surtidor> getSurtidores() { return List.copyOf(surtidores); }
}
