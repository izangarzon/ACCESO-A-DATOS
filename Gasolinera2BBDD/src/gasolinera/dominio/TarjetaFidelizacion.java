package gasolinera.dominio;

import java.time.LocalDate;
import java.util.Objects;

public class TarjetaFidelizacion {

    private final int id;
    private final String numero;
    private Cliente cliente;
    private final LocalDate emitidaEn;
    private LocalDate caducaEn;
    private int puntos;
    private boolean activa;

    public TarjetaFidelizacion(int id, String numero, Cliente cliente, LocalDate emitidaEn,
                               LocalDate caducaEn, int puntos, boolean activa) {
        if (id <= 0 || puntos < 0) throw new IllegalArgumentException("ID positivo y puntos no negativos requeridos.");
        this.id = id;
        this.numero = Producto.validarTexto(numero, "El número de tarjeta");
        this.emitidaEn = Objects.requireNonNull(emitidaEn, "La fecha de emisión no puede ser null.");
        this.puntos = puntos;
        this.activa = activa;
        setCaducaEn(caducaEn);
        asignarA(cliente);
    }

    public void asignarA(Cliente nuevoCliente) {
        if (cliente == nuevoCliente) return;
        if (cliente != null) cliente.quitarTarjeta(this);
        cliente = nuevoCliente;
        if (nuevoCliente != null) nuevoCliente.agregarTarjeta(this);
    }

    public void sumarPuntos(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("No se pueden sumar puntos negativos.");
        puntos = Math.addExact(puntos, cantidad);
    }

    public int getId() { return id; }
    public String getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getEmitidaEn() { return emitidaEn; }
    public LocalDate getCaducaEn() { return caducaEn; }
    public void setCaducaEn(LocalDate caducaEn) {
        if (caducaEn != null && caducaEn.isBefore(emitidaEn)) {
            throw new IllegalArgumentException("La caducidad no puede ser anterior a la emisión.");
        }
        this.caducaEn = caducaEn;
    }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) {
        if (puntos < 0) throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
        this.puntos = puntos;
    }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
