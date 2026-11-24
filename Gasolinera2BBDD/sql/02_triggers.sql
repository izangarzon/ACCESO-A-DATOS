USE gasolinera_mysql;

-- El esquema aprobado no guarda precio_venta en producto: el precio vigente
-- está en precio_producto.precio. Este trigger registra únicamente sus cambios.
-- Al ser InnoDB tanto precio_producto como historial_precio, la inserción
-- del historial forma parte de la misma transacción y se revierte con ROLLBACK.
DELIMITER $$

CREATE TRIGGER trg_precio_producto_historial
AFTER UPDATE ON precio_producto
FOR EACH ROW
BEGIN
    IF NOT (OLD.precio <=> NEW.precio) THEN
        INSERT INTO historial_precio (
            estacion_id,
            producto_id,
            precio_anterior,
            precio_nuevo,
            cambiado_en,
            empleado_id
        )
        VALUES (
            NEW.estacion_id,
            NEW.producto_id,
            OLD.precio,
            NEW.precio,
            CURRENT_TIMESTAMP,
            NEW.actualizado_por
        );
    END IF;
END$$

DELIMITER ;
