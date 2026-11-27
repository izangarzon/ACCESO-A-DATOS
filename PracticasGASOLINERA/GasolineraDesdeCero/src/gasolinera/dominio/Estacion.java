package gasolinera.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Estación de servicio con su ficha de facturación y sus surtidores. */
public class Estacion {

    private final int id;
    private final String codigo;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDate fechaApertura;
    private ConfiguracionFacturacion configuracionFacturacion;
    private final List<Surtidor> surtidores = new ArrayList<>();

    public Estacion(int id, String codigo, String nombre, String direccion,
                    String telefono, LocalDate fechaApertura) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.codigo = Validaciones.textoObligatorio(codigo, "El código");
        setNombre(nombre);
        setDireccion(direccion);
        setTelefono(telefono);
        setFechaApertura(fechaApertura);
    }

    /** Asocia la ficha de facturación. Una estación solo puede tener una. */
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

    /** Añade un surtidor. El número visible no puede repetirse dentro de la estación. */
    public void agregarSurtidor(Surtidor surtidor) {
        Objects.requireNonNull(surtidor, "El surtidor no puede ser null.");
        if (surtidor.getEstacion() != this) {
            throw new IllegalArgumentException("El surtidor debe pertenecer a esta estación.");
        }
        if (surtidores.stream().anyMatch(actual -> actual.getNumeroVisible() == surtidor.getNumeroVisible())) {
            throw new IllegalArgumentException("Ya existe un surtidor con ese número visible en la estación.");
        }
        surtidores.add(surtidor);
    }

    /** No permite retirar el último surtidor de la estación. */
    public void quitarSurtidor(Surtidor surtidor) {
        if (!surtidores.contains(surtidor)) {
            throw new IllegalArgumentException("El surtidor no pertenece a esta estación.");
        }
        if (surtidores.size() == 1) {
            throw new IllegalStateException("La estación debe conservar al menos un surtidor.");
        }
        surtidores.remove(surtidor);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = Validaciones.textoObligatorio(direccion, "La dirección");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = Validaciones.textoObligatorio(telefono, "El teléfono");
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = Objects.requireNonNull(fechaApertura, "La fecha de apertura no puede ser null.");
    }

    public ConfiguracionFacturacion getConfiguracionFacturacion() {
        return configuracionFacturacion;
    }

    public List<Surtidor> getSurtidores() {
        return List.copyOf(surtidores);
    }
}
