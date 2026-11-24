package gasolinera.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Campania {

    private final int id;
    private final String codigo;
    private String nombre;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final Descuento descuento;
    private boolean activa;
    private final List<InscripcionCampania> inscripciones = new ArrayList<>();

    public Campania(int id, String codigo, String nombre, LocalDate desde, LocalDate hasta,
                    Descuento descuento, boolean activa) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        setNombre(nombre);
        this.desde = Objects.requireNonNull(desde, "La fecha inicial no puede ser null.");
        this.hasta = Objects.requireNonNull(hasta, "La fecha final no puede ser null.");
        if (hasta.isBefore(desde)) throw new IllegalArgumentException("La fecha final no puede preceder a la inicial.");
        this.descuento = Objects.requireNonNull(descuento, "El descuento no puede ser null.");
        this.activa = activa;
    }

    void agregarInscripcion(InscripcionCampania inscripcion) {
        if (inscripcion.getCampania() != this) {
            throw new IllegalArgumentException("La inscripción debe pertenecer a esta campaña.");
        }
        if (inscripciones.stream().anyMatch(actual -> actual.getCliente().getId() == inscripcion.getCliente().getId())) {
            throw new IllegalArgumentException("El cliente ya está inscrito en esta campaña.");
        }
        inscripciones.add(inscripcion);
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        return activa && !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public Descuento getDescuento() { return descuento; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public List<InscripcionCampania> getInscripciones() { return List.copyOf(inscripciones); }
}
