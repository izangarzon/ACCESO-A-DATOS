-- Datos de demostración para gasolinera_mysql.
-- Ejecutar después de 01_esquema.sql y 02_triggers.sql, con las tablas vacías.
-- Los códigos usados en las subconsultas son claves de negocio UNIQUE y evitan
-- depender de los valores técnicos AUTO_INCREMENT.
USE gasolinera_mysql;

-- Estaciones y configuración: dos estaciones, cada una con sus datos fiscales.
INSERT INTO estacion (codigo, nombre, direccion, localidad, activa) VALUES
    ('EST-MAD-01', 'Gasolinera Centro', 'Calle Mayor 10', 'Madrid', TRUE),
    ('EST-ALC-01', 'Gasolinera Alcalá', 'Avenida Europa 25', 'Alcalá de Henares', TRUE);

INSERT INTO configuracion_facturacion
    (estacion_id, razon_social, nif, direccion_fiscal, email_facturacion, serie_factura, siguiente_numero)
VALUES
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     'Gasolinera Centro S.L.', 'B12345678', 'Calle Mayor 10, Madrid',
     'facturas.centro@example.es', 'MAD-2026', 2),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     'Gasolinera Alcalá S.L.', 'B87654321', 'Avenida Europa 25, Alcalá de Henares',
     'facturas.alcala@example.es', 'ALC-2026', 2);

-- Productos: tres combustibles a granel y dos productos envasados.
INSERT INTO producto
    (codigo, nombre, tipo, codigo_barras, unidad_medida, contenido_envase, activo)
VALUES
    ('COM-G95', 'Gasolina 95', 'GRANEL', NULL, 'LITRO', NULL, TRUE),
    ('COM-G98', 'Gasolina 98', 'GRANEL', NULL, 'LITRO', NULL, TRUE),
    ('COM-DIE', 'Diésel', 'GRANEL', NULL, 'LITRO', NULL, TRUE),
    ('ENV-ACE1', 'Aceite motor 1L', 'ENVASADO', '8410000000001', 'UNIDAD', 1.000, TRUE),
    ('ENV-ADBL5', 'AdBlue 5L', 'ENVASADO', '8410000000002', 'UNIDAD', 5.000, TRUE);

-- Surtidores: todos pertenecen a una estación y dispensan productos a granel.
INSERT INTO surtidor (estacion_id, codigo, producto_granel_id, activo) VALUES
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), 'S-MAD-01',
     (SELECT id FROM producto WHERE codigo = 'COM-G95'), TRUE),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), 'S-MAD-02',
     (SELECT id FROM producto WHERE codigo = 'COM-G98'), TRUE),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), 'S-MAD-03',
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'), TRUE),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), 'S-ALC-01',
     (SELECT id FROM producto WHERE codigo = 'COM-G95'), TRUE),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), 'S-ALC-02',
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'), TRUE);

-- Empleados: EMP-004 queda deliberadamente sin asignaciones funcionales.
INSERT INTO empleado (codigo, dni, nombre, telefono, email, activo) VALUES
    ('EMP-001', '11111111A', 'Ana Supervisora', '600000001', 'ana@example.es', TRUE),
    ('EMP-002', '22222222B', 'Bruno Cajero', '600000002', 'bruno@example.es', TRUE),
    ('EMP-003', '33333333C', 'Carla Encargada', '600000003', 'carla@example.es', TRUE),
    ('EMP-004', '44444444D', 'Diego Sin Funcion', '600000004', 'diego@example.es', TRUE),
    ('EMP-005', '55555555E', 'Elena Reincorporada', '600000005', 'elena@example.es', TRUE);

INSERT INTO funcion (codigo, nombre, descripcion) VALUES
    ('FUNC-RESP', 'Responsable', 'Supervisión de la estación.'),
    ('FUNC-CAJA', 'Cajero', 'Atención en caja y cobro.'),
    ('FUNC-PLAYA', 'Empleado de pista', 'Atención en pista y surtidores.'),
    ('FUNC-REP', 'Reposición', 'Reposición de productos envasados.');

-- Funciones iniciales: EMP-001 tiene dos funciones simultáneas y sirve de supervisor
-- en ambas estaciones. EMP-002 también acumula dos funciones.
INSERT INTO asignacion_funcion
    (empleado_id, funcion_id, estacion_id, supervisor_asignacion_id, desde, hasta)
VALUES
    ((SELECT id FROM empleado WHERE codigo = 'EMP-001'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-RESP'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     NULL, '2024-01-01 08:00:00', NULL),
    ((SELECT id FROM empleado WHERE codigo = 'EMP-001'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-PLAYA'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     NULL, '2024-01-01 08:00:00', NULL),
    ((SELECT id FROM empleado WHERE codigo = 'EMP-001'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-RESP'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     NULL, '2024-02-01 08:00:00', NULL);

INSERT INTO asignacion_funcion
    (empleado_id, funcion_id, estacion_id, supervisor_asignacion_id, desde, hasta)
VALUES
    ((SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-CAJA'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-001' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-MAD-01' AND af.hasta IS NULL),
     '2024-03-01 08:00:00', NULL),
    ((SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-REP'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-001' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-MAD-01' AND af.hasta IS NULL),
     '2024-03-01 08:00:00', NULL),
    ((SELECT id FROM empleado WHERE codigo = 'EMP-003'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-CAJA'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-001' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-ALC-01' AND af.hasta IS NULL),
     '2024-03-01 08:00:00', NULL),
    ((SELECT id FROM empleado WHERE codigo = 'EMP-003'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-RESP'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-001' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-ALC-01' AND af.hasta IS NULL),
     '2025-01-01 08:00:00', NULL);

-- EMP-005 recupera la misma función después de un período sin ella.
-- La asignación antigua queda cerrada; la nueva es una fila histórica distinta.
-- En esta estación, EMP-003 supervisa sus asignaciones.
INSERT INTO asignacion_funcion
    (empleado_id, funcion_id, estacion_id, supervisor_asignacion_id, desde, hasta)
VALUES
    ((SELECT id FROM empleado WHERE codigo = 'EMP-005'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-PLAYA'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-003' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-ALC-01' AND af.hasta IS NULL),
     '2024-04-01 08:00:00', '2025-01-01 08:00:00');

INSERT INTO asignacion_funcion
    (empleado_id, funcion_id, estacion_id, supervisor_asignacion_id, desde, hasta)
VALUES
    ((SELECT id FROM empleado WHERE codigo = 'EMP-005'),
     (SELECT id FROM funcion WHERE codigo = 'FUNC-PLAYA'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT af.id FROM asignacion_funcion af
        JOIN empleado e ON e.id = af.empleado_id
        JOIN funcion f ON f.id = af.funcion_id
        JOIN estacion es ON es.id = af.estacion_id
      WHERE e.codigo = 'EMP-003' AND f.codigo = 'FUNC-RESP'
        AND es.codigo = 'EST-ALC-01' AND af.hasta IS NULL),
     '2025-06-01 08:00:00', NULL);

-- Clientes: CLI-001 tiene tarjeta; CLI-002 no tiene tarjeta; CLI-003 también queda sin tarjeta.
INSERT INTO cliente (codigo, nombre, telefono, email, matricula, activo) VALUES
    ('CLI-001', 'Laura Cliente', '610000001', 'laura@example.es', '1234ABC', TRUE),
    ('CLI-002', 'Marcos Cliente', '610000002', 'marcos@example.es', '5678DEF', TRUE),
    ('CLI-003', 'Nuria Cliente', '610000003', 'nuria@example.es', NULL, TRUE);

-- Una tarjeta asignada y otra emitida sin titular.
INSERT INTO tarjeta_fidelizacion
    (numero, cliente_id, emitida_en, caduca_en, puntos, activa)
VALUES
    ('TARJ-0001', (SELECT id FROM cliente WHERE codigo = 'CLI-001'),
     '2025-01-01', '2030-12-31', 120, TRUE),
    ('TARJ-0002', NULL, '2025-06-01', '2030-12-31', 0, TRUE);

-- Proveedores.
INSERT INTO proveedor (codigo, nif, nombre, telefono, email, activo) VALUES
    ('PROV-001', 'A12345678', 'Suministros Energéticos', '910000001', 'ventas@energia.example.es', TRUE),
    ('PROV-002', 'A87654321', 'Distribuciones Centro', '910000002', 'pedidos@distribuciones.example.es', TRUE);

-- Acuerdos: cada proveedor tiene más de un producto y hay productos suministrados
-- por ambos proveedores. No hay proveedores ni productos sin detalle de acuerdo.
INSERT INTO acuerdo_suministro
    (codigo, proveedor_id, estacion_id, desde, hasta, estado)
VALUES
    ('AC-001', (SELECT id FROM proveedor WHERE codigo = 'PROV-001'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), '2025-01-01', NULL, 'ACTIVO'),
    ('AC-002', (SELECT id FROM proveedor WHERE codigo = 'PROV-002'),
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), '2025-01-01', NULL, 'ACTIVO'),
    ('AC-003', (SELECT id FROM proveedor WHERE codigo = 'PROV-001'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), '2025-02-01', NULL, 'ACTIVO'),
    ('AC-004', (SELECT id FROM proveedor WHERE codigo = 'PROV-002'),
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), '2025-02-01', NULL, 'ACTIVO');

INSERT INTO detalle_acuerdo_suministro
    (acuerdo_id, producto_id, precio_acordado, cantidad_minima)
VALUES
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-001'),
     (SELECT id FROM producto WHERE codigo = 'COM-G95'), 1.2000, 1000.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-001'),
     (SELECT id FROM producto WHERE codigo = 'COM-G98'), 1.3500, 500.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-001'),
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'), 1.1000, 1000.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-002'),
     (SELECT id FROM producto WHERE codigo = 'COM-G95'), 1.2200, 500.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-002'),
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'), 1.1200, 500.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-002'),
     (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), 8.0000, 20.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-003'),
     (SELECT id FROM producto WHERE codigo = 'COM-G95'), 1.2100, 1000.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-003'),
     (SELECT id FROM producto WHERE codigo = 'COM-G98'), 1.3600, 500.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-004'),
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'), 1.1300, 1000.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-004'),
     (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), 8.1000, 20.000),
    ((SELECT id FROM acuerdo_suministro WHERE codigo = 'AC-004'),
     (SELECT id FROM producto WHERE codigo = 'ENV-ADBL5'), 3.0000, 20.000);

-- Campañas: una con inscripciones y otra deliberadamente sin inscritos.
INSERT INTO campania
    (codigo, nombre, desde, hasta, tipo_descuento, valor_descuento, activa)
VALUES
    ('CAM-VERANO-26', 'Descuento de verano', '2026-01-01', '2026-12-31', 'PORCENTAJE', 10.0000, TRUE),
    ('CAM-INVIERNO-26', 'Promoción de invierno', '2026-11-01', '2026-12-31', 'IMPORTE', 2.0000, TRUE);

INSERT INTO inscripcion_campania (campania_id, cliente_id, inscrita_en, estado) VALUES
    ((SELECT id FROM campania WHERE codigo = 'CAM-VERANO-26'),
     (SELECT id FROM cliente WHERE codigo = 'CLI-001'), '2026-01-10 09:00:00', 'ACTIVA'),
    ((SELECT id FROM campania WHERE codigo = 'CAM-VERANO-26'),
     (SELECT id FROM cliente WHERE codigo = 'CLI-002'), '2026-01-12 11:30:00', 'ACTIVA');

-- Precios iniciales por producto y estación. INSERT no debe generar historial.
INSERT INTO precio_producto
    (estacion_id, producto_id, precio, actualizado_en, actualizado_por)
VALUES
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), (SELECT id FROM producto WHERE codigo = 'COM-G95'), 1.5000, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), (SELECT id FROM producto WHERE codigo = 'COM-G98'), 1.6500, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), (SELECT id FROM producto WHERE codigo = 'COM-DIE'), 1.4000, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), 12.0000, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'), (SELECT id FROM producto WHERE codigo = 'ENV-ADBL5'), 6.0000, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), (SELECT id FROM producto WHERE codigo = 'COM-G95'), 1.4800, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), (SELECT id FROM producto WHERE codigo = 'COM-G98'), 1.6300, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), (SELECT id FROM producto WHERE codigo = 'COM-DIE'), 1.4200, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), 11.5000, '2026-11-01 08:00:00', NULL),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'), (SELECT id FROM producto WHERE codigo = 'ENV-ADBL5'), 5.8000, '2026-11-01 08:00:00', NULL);

-- Ventas: tickets identificados y anónimos; una venta de estación Alcalá es factura.
INSERT INTO venta
    (estacion_id, empleado_id, cliente_id, codigo, fecha, tipo_documento,
     serie_factura, numero_factura, subtotal, descuento, total)
VALUES
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     (SELECT id FROM cliente WHERE codigo = 'CLI-001'),
     'MAD-T-0001', '2026-11-20 10:00:00', 'TICKET', NULL, NULL, 56.00, 0.00, 56.00),
    ((SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     (SELECT id FROM cliente WHERE codigo = 'CLI-001'),
     'MAD-T-0002', '2026-11-20 11:00:00', 'TICKET', NULL, NULL, 51.00, 3.30, 47.70),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-003'),
     (SELECT id FROM cliente WHERE codigo = 'CLI-002'),
     'ALC-F-0001', '2026-11-20 12:00:00', 'FACTURA', 'ALC-2026', 1, 48.40, 0.00, 48.40),
    ((SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-003'),
     NULL,
     'ALC-T-0001', '2026-11-20 13:00:00', 'TICKET', NULL, NULL, 33.70, 0.00, 33.70);

-- Diez líneas repartidas entre cuatro tickets. Los precios son instantáneas de venta.
INSERT INTO linea_venta
    (venta_id, numero_linea, producto_id, surtidor_id, codigo_producto_aplicado,
     nombre_producto_aplicado, cantidad, precio_unitario_aplicado, descuento, total_linea)
VALUES
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'), 1,
     (SELECT id FROM producto WHERE codigo = 'COM-G95'),
     (SELECT id FROM surtidor WHERE codigo = 'S-MAD-01'),
     'COM-G95', 'Gasolina 95', 20.000, 1.5000, 0.00, 30.00),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'), 2,
     (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), NULL,
     'ENV-ACE1', 'Aceite motor 1L', 1.000, 12.0000, 0.00, 12.00),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'), 3,
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'),
     (SELECT id FROM surtidor WHERE codigo = 'S-MAD-03'),
     'COM-DIE', 'Diésel', 10.000, 1.4000, 0.00, 14.00),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0002'), 1,
     (SELECT id FROM producto WHERE codigo = 'COM-G98'),
     (SELECT id FROM surtidor WHERE codigo = 'S-MAD-02'),
     'COM-G98', 'Gasolina 98', 20.000, 1.6500, 3.30, 29.70),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0002'), 2,
     (SELECT id FROM producto WHERE codigo = 'ENV-ADBL5'), NULL,
     'ENV-ADBL5', 'AdBlue 5L', 1.000, 6.0000, 0.00, 6.00),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0002'), 3,
     (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), NULL,
     'ENV-ACE1', 'Aceite motor 1L', 1.000, 12.0000, 0.00, 12.00),
    ((SELECT id FROM venta WHERE codigo = 'ALC-F-0001'), 1,
     (SELECT id FROM producto WHERE codigo = 'COM-DIE'),
     (SELECT id FROM surtidor WHERE codigo = 'S-ALC-02'),
     'COM-DIE', 'Diésel', 30.000, 1.4200, 0.00, 42.60),
    ((SELECT id FROM venta WHERE codigo = 'ALC-F-0001'), 2,
     (SELECT id FROM producto WHERE codigo = 'ENV-ADBL5'), NULL,
     'ENV-ADBL5', 'AdBlue 5L', 1.000, 5.8000, 0.00, 5.80),
    ((SELECT id FROM venta WHERE codigo = 'ALC-T-0001'), 1,
     (SELECT id FROM producto WHERE codigo = 'COM-G95'),
     (SELECT id FROM surtidor WHERE codigo = 'S-ALC-01'),
     'COM-G95', 'Gasolina 95', 15.000, 1.4800, 0.00, 22.20),
    ((SELECT id FROM venta WHERE codigo = 'ALC-T-0001'), 2,
     (SELECT id FROM producto WHERE codigo = 'ENV-ACE1'), NULL,
     'ENV-ACE1', 'Aceite motor 1L', 1.000, 11.5000, 0.00, 11.50);

-- Campaña del 10 % aplicada a la línea de gasolina: 33,00 - 3,30 = 29,70.
INSERT INTO venta_campania (venta_id, campania_id, descuento_aplicado) VALUES
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0002'),
     (SELECT id FROM campania WHERE codigo = 'CAM-VERANO-26'), 3.30);

-- Pagos: la venta MAD-T-0001 se divide entre dos medios; el resto queda completamente pagado.
INSERT INTO medio_pago (codigo, nombre, activo) VALUES
    ('EFECTIVO', 'Efectivo', TRUE),
    ('TARJETA', 'Tarjeta bancaria', TRUE),
    ('MOVIL', 'Pago móvil', TRUE);

INSERT INTO pago_venta (venta_id, medio_pago_id, importe, fecha, referencia) VALUES
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
     (SELECT id FROM medio_pago WHERE codigo = 'EFECTIVO'), 30.00, '2026-11-20 10:01:00', NULL),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
     (SELECT id FROM medio_pago WHERE codigo = 'TARJETA'), 26.00, '2026-11-20 10:02:00', 'TPV-MAD-0001'),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0002'),
     (SELECT id FROM medio_pago WHERE codigo = 'TARJETA'), 47.70, '2026-11-20 11:01:00', 'TPV-MAD-0002'),
    ((SELECT id FROM venta WHERE codigo = 'ALC-F-0001'),
     (SELECT id FROM medio_pago WHERE codigo = 'TARJETA'), 48.40, '2026-11-20 12:01:00', 'TPV-ALC-0001'),
    ((SELECT id FROM venta WHERE codigo = 'ALC-T-0001'),
     (SELECT id FROM medio_pago WHERE codigo = 'EFECTIVO'), 33.70, '2026-11-20 13:01:00', NULL);

-- Incidencias: una asignada con surtidor y otra sin empleado ni surtidor asignados.
INSERT INTO incidencia
    (codigo, estacion_id, empleado_id, surtidor_id, venta_id, fecha, descripcion, estado, resuelta_en)
VALUES
    ('INC-0001',
     (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     (SELECT id FROM surtidor WHERE codigo = 'S-MAD-01'),
     (SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
     '2026-11-20 10:15:00', 'Se detecta una incidencia en el terminal del surtidor.', 'RESUELTA',
     '2026-11-20 10:45:00'),
    ('INC-0002',
     (SELECT id FROM estacion WHERE codigo = 'EST-ALC-01'),
     NULL, NULL, NULL,
     '2026-11-20 14:00:00', 'Aviso pendiente de asignación de responsable.', 'ABIERTA', NULL);

-- Devoluciones parciales sucesivas de la línea 1 de MAD-T-0001 (20 litros en origen):
-- primero 2 litros y después 3; la suma devuelta (5) no supera la venta.
INSERT INTO devolucion (venta_id, empleado_id, codigo, fecha, motivo, total) VALUES
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     'DEV-0001', '2026-11-21 09:00:00', 'Devolución parcial inicial', 3.00),
    ((SELECT id FROM venta WHERE codigo = 'MAD-T-0001'),
     (SELECT id FROM empleado WHERE codigo = 'EMP-002'),
     'DEV-0002', '2026-11-22 09:00:00', 'Devolución parcial posterior', 4.50);

INSERT INTO linea_devolucion
    (devolucion_id, linea_venta_id, cantidad, precio_unitario_reembolsado, importe)
VALUES
    ((SELECT id FROM devolucion WHERE codigo = 'DEV-0001'),
     (SELECT lv.id FROM linea_venta lv
        JOIN venta v ON v.id = lv.venta_id
      WHERE v.codigo = 'MAD-T-0001' AND lv.numero_linea = 1),
     2.000, 1.5000, 3.00),
    ((SELECT id FROM devolucion WHERE codigo = 'DEV-0002'),
     (SELECT lv.id FROM linea_venta lv
        JOIN venta v ON v.id = lv.venta_id
      WHERE v.codigo = 'MAD-T-0001' AND lv.numero_linea = 1),
     3.000, 1.5000, 4.50);

INSERT INTO reembolso_devolucion
    (devolucion_id, medio_pago_id, importe, fecha, referencia)
VALUES
    ((SELECT id FROM devolucion WHERE codigo = 'DEV-0001'),
     (SELECT id FROM medio_pago WHERE codigo = 'EFECTIVO'), 3.00, '2026-11-21 09:01:00', NULL),
    ((SELECT id FROM devolucion WHERE codigo = 'DEV-0002'),
     (SELECT id FROM medio_pago WHERE codigo = 'EFECTIVO'), 4.50, '2026-11-22 09:01:00', NULL);

-- Demostración del trigger: una actualización revertida no conserva ni el cambio
-- de precio ni su registro de historial, porque ambas tablas usan InnoDB.
START TRANSACTION;
UPDATE precio_producto
SET precio = 1.5700
WHERE estacion_id = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01')
  AND producto_id = (SELECT id FROM producto WHERE codigo = 'COM-G95');
ROLLBACK;

-- Actualizar solo el empleado que registra el cambio no debe generar historial.
UPDATE precio_producto
SET actualizado_por = (SELECT id FROM empleado WHERE codigo = 'EMP-001')
WHERE estacion_id = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01')
  AND producto_id = (SELECT id FROM producto WHERE codigo = 'COM-G95');

-- Modificar realmente el precio crea una fila en historial_precio.
UPDATE precio_producto
SET precio = 1.5500,
    actualizado_en = '2026-11-24 09:00:00',
    actualizado_por = (SELECT id FROM empleado WHERE codigo = 'EMP-001')
WHERE estacion_id = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01')
  AND producto_id = (SELECT id FROM producto WHERE codigo = 'COM-G95');

-- Un UPDATE que deja el mismo precio no debe añadir otra fila al historial.
UPDATE precio_producto
SET precio = 1.5500
WHERE estacion_id = (SELECT id FROM estacion WHERE codigo = 'EST-MAD-01')
  AND producto_id = (SELECT id FROM producto WHERE codigo = 'COM-G95');
