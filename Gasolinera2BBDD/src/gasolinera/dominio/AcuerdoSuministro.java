package gasolinera.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AcuerdoSuministro {

    private final int id;
    private final String codigo;
    private final Proveedor proveedor;
    private final Estacion estacion;
    private final LocalDate desde;
    private LocalDate hasta;
    private EstadoAcuerdo estado;
    private final List<CondicionSuministro> condiciones = new ArrayList<>();

    public AcuerdoSuministro(int id, String codigo, Proveedor proveedor, Estacion estacion,
                             LocalDate desde, LocalDate hasta, EstadoAcuerdo estado) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor no puede ser null.");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.desde = Objects.requireNonNull(desde, "La fecha inicial no puede ser null.");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser null.");
        setHasta(hasta);
    }

    public void agregarCondicion(CondicionSuministro condicion) {
        Objects.requireNonNull(condicion, "La condición no puede ser null.");
        if (condicion.getAcuerdo() != this) {
            throw new IllegalArgumentException("La condición debe pertenecer a este acuerdo.");
        }
        if (condiciones.stream().anyMatch(actual -> actual.getProducto().getId() == condicion.getProducto().getId())) {
            throw new IllegalArgumentException("El producto ya está incluido en este acuerdo.");
        }
        condiciones.add(condicion);
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        return estado == EstadoAcuerdo.ACTIVO && !fecha.isBefore(desde) && (hasta == null || !fecha.isAfter(hasta));
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public Proveedor getProveedor() { return proveedor; }
    public Estacion getEstacion() { return estacion; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) {
        if (hasta != null && hasta.isBefore(desde)) throw new IllegalArgumentException("La fecha final no puede preceder a la inicial.");
        this.hasta = hasta;
    }
    public EstadoAcuerdo getEstado() { return estado; }
    public void setEstado(EstadoAcuerdo estado) { this.estado = Objects.requireNonNull(estado); }
    public List<CondicionSuministro> getCondiciones() { return List.copyOf(condiciones); }
}
