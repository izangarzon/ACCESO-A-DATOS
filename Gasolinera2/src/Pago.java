import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {

    // Atributos
    private final int id;
    private final Cliente cliente;
    private LocalDate fecha;
    private BigDecimal importe;
    private BigDecimal litros;
    private String combustible;


    //Constructor
    public Pago(int id, Cliente cliente, LocalDate fecha, BigDecimal importe, BigDecimal litros, String combustible) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }

    //Getters
    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getCombustible() {
        return combustible;
    }

    public BigDecimal getLitros() {
        return litros;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    //Setters
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public void setLitros(BigDecimal litros) {
        this.litros = litros;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

}
