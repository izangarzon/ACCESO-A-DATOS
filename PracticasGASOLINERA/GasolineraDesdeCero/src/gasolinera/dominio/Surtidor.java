package gasolinera.dominio;

import java.util.Objects;

/** Surtidor de una estación: código de equipo, número visible, combustible y estado. */
public class Surtidor {

    private final int id;
    private final Estacion estacion;
    private final String codigoEquipo;
    private final int numeroVisible;
    private TipoCombustible tipoCombustible;
    private EstadoServicio estadoServicio;

    public Surtidor(int id, Estacion estacion, String codigoEquipo, int numeroVisible,
                    TipoCombustible tipoCombustible, EstadoServicio estadoServicio) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.codigoEquipo = Validaciones.textoObligatorio(codigoEquipo, "El código de equipo");
        this.numeroVisible = Validaciones.enteroPositivo(numeroVisible, "El número visible");
        setTipoCombustible(tipoCombustible);
        setEstadoServicio(estadoServicio);
    }

    public int getId() {
        return id;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = Objects.requireNonNull(tipoCombustible, "El tipo de combustible no puede ser null.");
    }

    public EstadoServicio getEstadoServicio() {
        return estadoServicio;
    }

    public void setEstadoServicio(EstadoServicio estadoServicio) {
        this.estadoServicio = Objects.requireNonNull(estadoServicio, "El estado de servicio no puede ser null.");
    }
}
