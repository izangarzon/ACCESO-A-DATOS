package gasolinera.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class InscripcionCampania {

    private final int id;
    private final Campania campania;
    private final Cliente cliente;
    private final LocalDateTime inscritaEn;
    private EstadoInscripcion estado;

    public InscripcionCampania(int id, Campania campania, Cliente cliente,
                               LocalDateTime inscritaEn, EstadoInscripcion estado) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.campania = Objects.requireNonNull(campania, "La campaña no puede ser null.");
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser null.");
        this.inscritaEn = Objects.requireNonNull(inscritaEn, "La fecha de inscripción no puede ser null.");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser null.");
        campania.agregarInscripcion(this);
    }

    public int getId() { return id; }
    public Campania getCampania() { return campania; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getInscritaEn() { return inscritaEn; }
    public EstadoInscripcion getEstado() { return estado; }
    public void setEstado(EstadoInscripcion estado) { this.estado = Objects.requireNonNull(estado); }
}
