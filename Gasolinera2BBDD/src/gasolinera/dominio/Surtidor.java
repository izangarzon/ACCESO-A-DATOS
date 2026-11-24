package gasolinera.dominio;

import java.util.Objects;

public class Surtidor {

    private final int id;
    private final Estacion estacion;
    private final String codigo;
    private final ProductoGranel producto;
    private boolean activo;

    public Surtidor(int id, Estacion estacion, String codigo, ProductoGranel producto, boolean activo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.producto = Objects.requireNonNull(producto, "El producto a granel no puede ser null.");
        this.activo = activo;
    }

    public int getId() { return id; }
    public Estacion getEstacion() { return estacion; }
    public String getCodigo() { return codigo; }
    public ProductoGranel getProducto() { return producto; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
