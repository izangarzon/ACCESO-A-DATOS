-- Pruebas de comprobación del esquema, datos de ejemplo y trigger.
-- Ejecutar después de 01_esquema.sql, 02_triggers.sql y 03_datos.sql.
-- Las consultas no intentan garantizar reglas transaccionales que el esquema
-- todavía no protege automáticamente.
USE gasolinera_mysql;

-- 1. Estaciones, configuración y surtidores relacionados.
SELECT
    e.codigo AS estacion,
    e.nombre,
    cf.serie_factura,
    s.codigo AS surtidor,
    p.codigo AS producto_granel
FROM estacion e
JOIN configuracion_facturacion cf ON cf.estacion_id = e.id
LEFT JOIN surtidor s ON s.estacion_id = e.id
LEFT JOIN producto p ON p.id = s.producto_granel_id
ORDER BY e.codigo, s.codigo;
-- Esperado: cada estación tiene su configuración y surtidores asociados.

-- 2. Clientes y tarjetas: distingue clientes sin tarjeta de tarjetas sin titular.
SELECT
    c.codigo AS cliente,
    c.nombre,
    t.numero AS tarjeta
FROM cliente c
LEFT JOIN tarjeta_fidelizacion t ON t.cliente_id = c.id
ORDER BY c.codigo, t.numero;
-- Esperado: CLI-001 tiene tarjeta; CLI-002 y CLI-003 aparecen sin tarjeta.

SELECT numero, cliente_id, activa
FROM tarjeta_fidelizacion
WHERE cliente_id IS NULL;
-- Esperado: TARJ-0002 aparece sin titular.

-- 3. Asignaciones y supervisión históricas del personal.
SELECT
    e.codigo AS empleado,
    f.codigo AS funcion,
    es.codigo AS estacion,
    af.desde,
    af.hasta,
    supervisor.codigo AS supervisor,
    supervisor_funcion.codigo AS funcion_supervisor,
    supervisor_estacion.codigo AS estacion_supervisor
FROM asignacion_funcion af
JOIN empleado e ON e.id = af.empleado_id
JOIN funcion f ON f.id = af.funcion_id
JOIN estacion es ON es.id = af.estacion_id
LEFT JOIN asignacion_funcion sup ON sup.id = af.supervisor_asignacion_id
LEFT JOIN empleado supervisor ON supervisor.id = sup.empleado_id
LEFT JOIN funcion supervisor_funcion ON supervisor_funcion.id = sup.funcion_id
LEFT JOIN estacion supervisor_estacion ON supervisor_estacion.id = sup.estacion_id
ORDER BY e.codigo, es.codigo, af.desde;
-- Esperado: aparecen funciones simultáneas, períodos cerrados y relaciones
-- de supervisión; EMP-004 no aparece porque no tiene asignaciones.

-- 4. Productos, tipos y acuerdos con proveedores.
SELECT
    p.codigo AS producto,
    p.tipo,
    pr.codigo AS proveedor,
    es.codigo AS estacion,
    das.precio_acordado,
    das.cantidad_minima
FROM detalle_acuerdo_suministro das
JOIN acuerdo_suministro a ON a.id = das.acuerdo_id
JOIN proveedor pr ON pr.id = a.proveedor_id
JOIN estacion es ON es.id = a.estacion_id
JOIN producto p ON p.id = das.producto_id
ORDER BY p.codigo, pr.codigo, es.codigo;
-- Esperado: hay acuerdos para todos los productos y proveedores; aparecen
-- asociaciones de varios proveedores con varios productos.

-- 5. Campañas con y sin inscripciones.
SELECT
    c.codigo AS campania,
    c.nombre,
    COUNT(ic.id) AS numero_inscripciones
FROM campania c
LEFT JOIN inscripcion_campania ic ON ic.campania_id = c.id
GROUP BY c.id, c.codigo, c.nombre
ORDER BY c.codigo;
-- Esperado: CAM-VERANO-26 tiene inscripciones y CAM-INVIERNO-26 tiene cero.

-- 6. Incidencias asignadas y sin asignar.
SELECT
    i.codigo,
    es.codigo AS estacion,
    e.codigo AS empleado_asignado,
    s.codigo AS surtidor_asignado,
    i.estado
FROM incidencia i
JOIN estacion es ON es.id = i.estacion_id
LEFT JOIN empleado e ON e.id = i.empleado_id
LEFT JOIN surtidor s ON s.id = i.surtidor_id
ORDER BY i.codigo;
-- Esperado: INC-0001 tiene asignaciones e INC-0002 aparece sin empleado ni surtidor.

-- 7. Ventas identificadas/anónimas y sus cantidades. Las sumas se calculan
-- en subconsultas para no multiplicar líneas al unir pagos.
SELECT
    v.codigo,
    es.codigo AS estacion,
    c.codigo AS cliente,
    v.tipo_documento,
    v.subtotal,
    v.descuento,
    v.total,
    (SELECT COUNT(*) FROM linea_venta lv WHERE lv.venta_id = v.id) AS lineas,
    (SELECT COALESCE(SUM(lv.total_linea), 0)
       FROM linea_venta lv WHERE lv.venta_id = v.id) AS suma_lineas,
    (SELECT COALESCE(SUM(pv.importe), 0)
       FROM pago_venta pv WHERE pv.venta_id = v.id) AS suma_pagos
FROM venta v
JOIN estacion es ON es.id = v.estacion_id
LEFT JOIN cliente c ON c.id = v.cliente_id
ORDER BY v.codigo;
-- Esperado en los datos de ejemplo: cuatro ventas, incluida una anónima,
-- diez líneas y pagos por el total de cada venta. Esta consulta informa de
-- los importes, pero el esquema no garantiza por sí solo sus sumas.

-- 8. Verifica específicamente que existe al menos una venta con varios pagos.
SELECT v.codigo, COUNT(pv.id) AS numero_pagos, SUM(pv.importe) AS importe_pagado
FROM venta v
JOIN pago_venta pv ON pv.venta_id = v.id
GROUP BY v.id, v.codigo
HAVING COUNT(pv.id) > 1;
-- Esperado: MAD-T-0001 aparece con dos pagos.

-- 9. Cantidades devueltas por línea. La consulta muestra la suma para revisión;
-- la base no impide automáticamente que la cantidad acumulada exceda la vendida.
SELECT
    v.codigo AS venta,
    lv.numero_linea,
    p.codigo AS producto,
    lv.cantidad AS cantidad_vendida,
    COALESCE(SUM(ld.cantidad), 0) AS cantidad_devuelta,
    lv.cantidad - COALESCE(SUM(ld.cantidad), 0) AS cantidad_pendiente
FROM linea_venta lv
JOIN venta v ON v.id = lv.venta_id
JOIN producto p ON p.id = lv.producto_id
LEFT JOIN linea_devolucion ld ON ld.linea_venta_id = lv.id
GROUP BY v.codigo, lv.id, lv.numero_linea, p.codigo, lv.cantidad
ORDER BY v.codigo, lv.numero_linea;
-- Esperado: MAD-T-0001, línea 1 muestra 20 vendidas y 5 devueltas en dos
-- devoluciones sucesivas; la cantidad pendiente es 15.

-- 10. Intentos de violación UNIQUE/FK se ejecutan en llamadas independientes
-- con manejadores locales. La violación esperada se informa y no aborta el
-- resto del archivo.
DELIMITER $$

DROP PROCEDURE IF EXISTS probar_codigo_comercial_duplicado$$
CREATE PROCEDURE probar_codigo_comercial_duplicado()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO estacion (codigo, nombre, direccion, localidad)
    VALUES ('EST-MAD-01', 'Duplicado de prueba', 'Dirección de prueba', 'Madrid');

    IF v_fallo THEN
        SELECT 'OK: el código comercial duplicado fue rechazado.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó un código comercial duplicado.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_clave_foranea_inexistente$$
CREATE PROCEDURE probar_clave_foranea_inexistente()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO configuracion_facturacion
        (estacion_id, razon_social, nif, direccion_fiscal, serie_factura)
    VALUES
        (-2147483648, 'Prueba FK', 'X00000000', 'Dirección de prueba', 'PRUEBA-FK');

    IF v_fallo THEN
        SELECT 'OK: la estación inexistente fue rechazada por la FK.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó una estación inexistente.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_asociacion_acuerdo_duplicada$$
CREATE PROCEDURE probar_asociacion_acuerdo_duplicada()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO detalle_acuerdo_suministro (acuerdo_id, producto_id, precio_acordado)
    VALUES (
        (SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-001'),
        (SELECT id FROM producto WHERE codigo = 'COM-G95'),
        1.2500
    );

    IF v_fallo THEN
        SELECT 'OK: repetir el producto en el mismo acuerdo fue rechazado.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó dos veces el producto en el mismo acuerdo.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_inscripcion_duplicada$$
CREATE PROCEDURE probar_inscripcion_duplicada()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO inscripcion_campania (campania_id, cliente_id)
    VALUES (
        (SELECT id FROM campania WHERE codigo = 'CAM-VERANO-26'),
        (SELECT id FROM cliente WHERE codigo = 'CLI-001')
    );

    IF v_fallo THEN
        SELECT 'OK: la inscripción repetida fue rechazada.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó una inscripción repetida.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_numero_linea_duplicado$$
CREATE PROCEDURE probar_numero_linea_duplicado()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO linea_venta
        (venta_id, numero_linea, producto_id, codigo_producto_aplicado,
         nombre_producto_aplicado, cantidad, precio_unitario_aplicado, total_linea)
    VALUES (
        (SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
        1,
        (SELECT id FROM producto WHERE codigo = 'COM-G95'),
        'COM-G95', 'Gasolina 95', 1.000, 1.5000, 1.50
    );

    IF v_fallo THEN
        SELECT 'OK: el número de línea repetido en la venta fue rechazado.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó un número de línea repetido.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_cantidad_invalida$$
CREATE PROCEDURE probar_cantidad_invalida()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    INSERT INTO linea_venta
        (venta_id, numero_linea, producto_id, codigo_producto_aplicado,
         nombre_producto_aplicado, cantidad, precio_unitario_aplicado, total_linea)
    VALUES (
        (SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
        99,
        (SELECT id FROM producto WHERE codigo = 'COM-G95'),
        'COM-G95', 'Gasolina 95', 0, 1.5000, 0
    );

    IF v_fallo THEN
        SELECT 'OK: la cantidad cero fue rechazada por CHECK.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó una cantidad cero.' AS resultado;
    END IF;
END$$

DROP PROCEDURE IF EXISTS probar_precio_negativo$$
CREATE PROCEDURE probar_precio_negativo()
BEGIN
    DECLARE v_fallo BOOLEAN DEFAULT FALSE;
    DECLARE v_sqlstate CHAR(5) DEFAULT '00000';
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        SET v_fallo = TRUE;
        GET DIAGNOSTICS CONDITION 1 v_sqlstate = RETURNED_SQLSTATE;
    END;

    UPDATE precio_producto
    SET precio = -1.0000
    WHERE estacion_id = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01')
      AND producto_id = (SELECT id FROM producto WHERE codigo = 'COM-G95');

    IF v_fallo THEN
        SELECT 'OK: el precio negativo fue rechazado por CHECK.' AS resultado,
               v_sqlstate AS sqlstate_obtenido;
    ELSE
        SELECT 'ERROR: se aceptó un precio negativo.' AS resultado;
    END IF;
END$$

DELIMITER ;

CALL probar_codigo_comercial_duplicado();
CALL probar_clave_foranea_inexistente();
CALL probar_asociacion_acuerdo_duplicada();
CALL probar_inscripcion_duplicada();
CALL probar_numero_linea_duplicado();
CALL probar_cantidad_invalida();
CALL probar_precio_negativo();

DROP PROCEDURE IF EXISTS probar_codigo_comercial_duplicado;
DROP PROCEDURE IF EXISTS probar_clave_foranea_inexistente;
DROP PROCEDURE IF EXISTS probar_asociacion_acuerdo_duplicada;
DROP PROCEDURE IF EXISTS probar_inscripcion_duplicada;
DROP PROCEDURE IF EXISTS probar_numero_linea_duplicado;
DROP PROCEDURE IF EXISTS probar_cantidad_invalida;
DROP PROCEDURE IF EXISTS probar_precio_negativo;

-- 11. Cantidades y precios deben ser positivos/no negativos según los CHECK.
SELECT
    (SELECT COUNT(*) FROM linea_venta WHERE cantidad <= 0) AS lineas_cantidad_invalida,
    (SELECT COUNT(*) FROM linea_venta WHERE precio_unitario_aplicado < 0) AS lineas_precio_invalido,
    (SELECT COUNT(*) FROM precio_producto WHERE precio < 0) AS precios_vigentes_invalidos,
    (SELECT COUNT(*) FROM detalle_acuerdo_suministro
      WHERE precio_acordado < 0 OR cantidad_minima < 0) AS acuerdos_invalidos;
-- Esperado: todos los contadores son cero. La suma de cantidades devueltas se
-- revisa en la consulta anterior y requiere lógica transaccional para garantizarse.

-- 12. Historial de precios del producto de demostración.
SELECT
    es.codigo AS estacion,
    p.codigo AS producto,
    hp.precio_anterior,
    hp.precio_nuevo,
    hp.cambiado_en,
    e.codigo AS empleado
FROM historial_precio hp
JOIN estacion es ON es.id = hp.estacion_id
JOIN producto p ON p.id = hp.producto_id
LEFT JOIN empleado e ON e.id = hp.empleado_id
WHERE es.codigo = 'EST-MAD-01'
  AND p.codigo = 'COM-G95'
ORDER BY hp.cambiado_en, hp.id;
-- Esperado tras 03_datos.sql: el cambio efectivo 1.5000 -> 1.5500 aparece;
-- la actualización revertida no permanece en el historial.

-- 13. El mismo precio no debe generar historial adicional.
SET @estacion_prueba = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01');
SET @producto_prueba = (SELECT id FROM producto WHERE codigo = 'COM-G95');
SET @precio_prueba = (
    SELECT precio FROM precio_producto
    WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
);
SET @historial_antes_mismo_precio = (
    SELECT COUNT(*) FROM historial_precio
    WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
);

UPDATE precio_producto
SET precio = @precio_prueba
WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba;

SELECT
    @historial_antes_mismo_precio AS historial_antes,
    (SELECT COUNT(*) FROM historial_precio
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS historial_despues,
    CASE
        WHEN @historial_antes_mismo_precio = (
            SELECT COUNT(*) FROM historial_precio
            WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
        ) THEN 'OK: no se añadió historial'
        ELSE 'ERROR: cambió el historial'
    END AS resultado;
-- Esperado: historial_antes e historial_despues coinciden.

-- 14. Modificar el campo descriptivo real (producto.nombre) no debe alterar
-- el historial porque el trigger observa precio_producto.precio.
SET @historial_antes_nombre = (
    SELECT COUNT(*) FROM historial_precio
    WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
);
SET @nombre_original = (
    SELECT nombre FROM producto WHERE id = @producto_prueba
);

UPDATE producto
SET nombre = CONCAT(@nombre_original, ' (prueba)')
WHERE id = @producto_prueba;

SELECT
    @historial_antes_nombre AS historial_antes,
    (SELECT COUNT(*) FROM historial_precio
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS historial_despues,
    CASE
        WHEN @historial_antes_nombre = (
            SELECT COUNT(*) FROM historial_precio
            WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
        ) THEN 'OK: cambiar el nombre no añadió historial'
        ELSE 'ERROR: cambiar el nombre alteró el historial'
    END AS resultado;

UPDATE producto
SET nombre = @nombre_original
WHERE id = @producto_prueba;
-- Esperado: mismo número de registros antes y después; se restaura el nombre.

-- 15. ROLLBACK debe revertir tanto el precio vigente como el historial generado.
SET @precio_antes_rollback = (
    SELECT precio FROM precio_producto
    WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
);
SET @historial_antes_rollback = (
    SELECT COUNT(*) FROM historial_precio
    WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
);

START TRANSACTION;
UPDATE precio_producto
SET precio = @precio_antes_rollback + 0.0100
WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba;

SELECT
    @historial_antes_rollback AS historial_antes,
    (SELECT COUNT(*) FROM historial_precio
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS historial_durante,
    (SELECT precio FROM precio_producto
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS precio_durante;
-- Esperado antes del ROLLBACK: historial_durante = historial_antes + 1 y
-- precio_durante = precio_antes_rollback + 0.0100.
ROLLBACK;

SELECT
    @precio_antes_rollback AS precio_antes,
    (SELECT precio FROM precio_producto
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS precio_despues,
    @historial_antes_rollback AS historial_antes,
    (SELECT COUNT(*) FROM historial_precio
      WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba) AS historial_despues,
    CASE
        WHEN @precio_antes_rollback = (
            SELECT precio FROM precio_producto
            WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
        )
        AND @historial_antes_rollback = (
            SELECT COUNT(*) FROM historial_precio
            WHERE estacion_id = @estacion_prueba AND producto_id = @producto_prueba
        ) THEN 'OK: ROLLBACK revirtió precio e historial'
        ELSE 'ERROR: el ROLLBACK no revirtió ambos cambios'
    END AS resultado;
-- Esperado: precio e historial quedan exactamente como antes de la transacción.

-- 16. Las ventas antiguas conservan el precio registrado en cada línea, aunque
-- el precio vigente haya cambiado posteriormente.
SELECT
    v.codigo AS venta,
    p.codigo AS producto,
    lv.precio_unitario_aplicado AS precio_conservado_en_venta,
    pp.precio AS precio_vigente,
    CASE
        WHEN lv.precio_unitario_aplicado = pp.precio
            THEN 'Coincide actualmente'
        ELSE 'Precio histórico conservado'
    END AS comprobacion
FROM linea_venta lv
JOIN venta v ON v.id = lv.venta_id
JOIN producto p ON p.id = lv.producto_id
JOIN precio_producto pp
  ON pp.estacion_id = v.estacion_id
 AND pp.producto_id = lv.producto_id
WHERE p.codigo = 'COM-G95'
ORDER BY v.codigo, lv.numero_linea;
-- Esperado: precio_unitario_aplicado se mantiene inalterado; la diferencia con
-- precio_vigente se informa, no se trata como error.

-- Recordatorio: este esquema no garantiza mediante CHECK/FK las sumas entre
-- líneas, pagos, devoluciones ni el ajuste a la estación de un surtidor. Esas
-- reglas deben implementarse en la siguiente fase mediante transacciones y
-- lógica adicional, con bloqueos adecuados cuando haya operaciones concurrentes.
