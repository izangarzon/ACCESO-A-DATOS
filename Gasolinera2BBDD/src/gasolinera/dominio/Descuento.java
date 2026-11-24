package gasolinera.dominio;

import java.math.BigDecimal;

public sealed interface Descuento permits DescuentoPorcentaje, DescuentoImporte {

    TipoDescuento getTipo();

    BigDecimal calcular(BigDecimal base);
}
