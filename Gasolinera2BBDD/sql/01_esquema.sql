-- Esquema relacional de la gasolinera.
-- Requiere MySQL 8.0.16 o posterior para que se apliquen las restricciones CHECK.
CREATE DATABASE IF NOT EXISTS gasolinera_mysql
    CHARACTER SET utf8mb4;

USE gasolinera_mysql;

-- Estaciones
-- El código es un identificador comercial, independiente de la PK técnica.
CREATE TABLE estacion (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activa por defecto al dar de alta la estación.',
    CONSTRAINT pk_estacion PRIMARY KEY (id),
    CONSTRAINT uq_estacion_codigo UNIQUE (codigo)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Estaciones de servicio.';

-- Empleados
CREATE TABLE empleado (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el empleado.',
    CONSTRAINT pk_empleado PRIMARY KEY (id),
    CONSTRAINT uq_empleado_codigo UNIQUE (codigo),
    CONSTRAINT uq_empleado_dni UNIQUE (dni)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Personal de la empresa.';

-- Clientes
-- cliente_id en tarjeta_fidelizacion será nullable para admitir tarjetas sin asignar.
CREATE TABLE cliente (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    matricula VARCHAR(10) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el cliente.',
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uq_cliente_codigo UNIQUE (codigo),
    CONSTRAINT uq_cliente_matricula UNIQUE (matricula)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Clientes registrados; una venta puede no tener cliente.';

-- Catálogo de productos unificado. El tipo distingue productos envasados y a granel.
CREATE TABLE producto (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    tipo VARCHAR(12) NOT NULL,
    codigo_barras VARCHAR(30) NULL,
    unidad_medida VARCHAR(12) NOT NULL,
    contenido_envase DECIMAL(12,3) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el producto.',
    CONSTRAINT pk_producto PRIMARY KEY (id),
    CONSTRAINT uq_producto_codigo UNIQUE (codigo),
    CONSTRAINT uq_producto_codigo_barras UNIQUE (codigo_barras),
    CONSTRAINT ck_producto_tipo CHECK (tipo IN ('ENVASADO', 'GRANEL')),
    CONSTRAINT ck_producto_contenido CHECK (
        (tipo = 'ENVASADO' AND contenido_envase IS NOT NULL AND contenido_envase > 0)
        OR (tipo = 'GRANEL' AND contenido_envase IS NULL)
    )
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Productos envasados o vendidos a granel.';

-- Funciones laborales disponibles para asignar al personal.
CREATE TABLE funcion (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(255) NULL,
    CONSTRAINT pk_funcion PRIMARY KEY (id),
    CONSTRAINT uq_funcion_codigo UNIQUE (codigo),
    CONSTRAINT uq_funcion_nombre UNIQUE (nombre)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Catálogo de funciones del personal.';

-- Proveedores
CREATE TABLE proveedor (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nif VARCHAR(20) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el proveedor.',
    CONSTRAINT pk_proveedor PRIMARY KEY (id),
    CONSTRAINT uq_proveedor_codigo UNIQUE (codigo),
    CONSTRAINT uq_proveedor_nif UNIQUE (nif)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Proveedores de productos.';

-- Medios de pago
CREATE TABLE medio_pago (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el medio de pago.',
    CONSTRAINT pk_medio_pago PRIMARY KEY (id),
    CONSTRAINT uq_medio_pago_codigo UNIQUE (codigo),
    CONSTRAINT uq_medio_pago_nombre UNIQUE (nombre)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Medios de pago admitidos en ventas y reembolsos.';

-- Campañas comerciales
CREATE TABLE campania (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    desde DATE NOT NULL,
    hasta DATE NOT NULL,
    tipo_descuento VARCHAR(12) NOT NULL,
    valor_descuento DECIMAL(12,4) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activa por defecto al dar de alta la campaña.',
    CONSTRAINT pk_campania PRIMARY KEY (id),
    CONSTRAINT uq_campania_codigo UNIQUE (codigo),
    CONSTRAINT ck_campania_tipo_descuento CHECK (tipo_descuento IN ('PORCENTAJE', 'IMPORTE')),
    CONSTRAINT ck_campania_valor_descuento CHECK (valor_descuento > 0),
    CONSTRAINT ck_campania_fechas CHECK (hasta >= desde)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Campañas promocionales y su período de validez.';

-- Configuración de facturación: UNIQUE(estacion_id) limita a una configuración por estación.
CREATE TABLE configuracion_facturacion (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    nif VARCHAR(20) NOT NULL,
    direccion_fiscal VARCHAR(200) NOT NULL,
    email_facturacion VARCHAR(150) NULL,
    serie_factura VARCHAR(20) NOT NULL,
    siguiente_numero INT NOT NULL DEFAULT 1 COMMENT 'La primera factura usa el número 1.',
    CONSTRAINT pk_configuracion_facturacion PRIMARY KEY (id),
    CONSTRAINT uq_configuracion_facturacion_estacion UNIQUE (estacion_id),
    CONSTRAINT ck_configuracion_siguiente_numero CHECK (siguiente_numero > 0),
    CONSTRAINT fk_configuracion_facturacion_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Datos fiscales y numeración de facturas de cada estación.';

-- Tarjetas sin cliente asignado: cliente_id puede permanecer NULL.
CREATE TABLE tarjeta_fidelizacion (
    id INT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(30) NOT NULL,
    cliente_id INT NULL,
    emitida_en DATE NOT NULL DEFAULT (CURRENT_DATE) COMMENT 'Fecha actual si no se indica fecha de emisión.',
    caduca_en DATE NULL,
    puntos INT NOT NULL DEFAULT 0 COMMENT 'Una tarjeta nueva comienza con cero puntos.',
    activa BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activa por defecto al emitir la tarjeta.',
    CONSTRAINT pk_tarjeta_fidelizacion PRIMARY KEY (id),
    CONSTRAINT uq_tarjeta_fidelizacion_numero UNIQUE (numero),
    CONSTRAINT ck_tarjeta_puntos CHECK (puntos >= 0),
    CONSTRAINT ck_tarjeta_fechas CHECK (caduca_en IS NULL OR caduca_en >= emitida_en),
    CONSTRAINT fk_tarjeta_fidelizacion_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Tarjetas de fidelización, asignadas o todavía sin asignar.';

-- Surtidores: cada surtidor se vincula a una estación y al producto que dispensa.
CREATE TABLE surtidor (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    producto_granel_id INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Activo por defecto al dar de alta el surtidor.',
    CONSTRAINT pk_surtidor PRIMARY KEY (id),
    CONSTRAINT uq_surtidor_estacion_codigo UNIQUE (estacion_id, codigo),
    CONSTRAINT fk_surtidor_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_surtidor_producto FOREIGN KEY (producto_granel_id)
        REFERENCES producto (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Surtidores identificados dentro de cada estación.';

-- Asignaciones con vigencia histórica. No se exige que todo empleado tenga asignación.
-- supervisor_asignacion_id referencia la asignación del empleado supervisor.
CREATE TABLE asignacion_funcion (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    funcion_id INT NOT NULL,
    estacion_id INT NOT NULL,
    supervisor_asignacion_id INT NULL,
    desde DATETIME NOT NULL,
    hasta DATETIME NULL,
    CONSTRAINT pk_asignacion_funcion PRIMARY KEY (id),
    CONSTRAINT uq_asignacion_funcion_vigencia UNIQUE (empleado_id, funcion_id, estacion_id, desde),
    CONSTRAINT ck_asignacion_funcion_fechas CHECK (hasta IS NULL OR hasta > desde),
    CONSTRAINT ck_asignacion_funcion_supervisor CHECK (
        supervisor_asignacion_id IS NULL OR supervisor_asignacion_id <> id
    ),
    CONSTRAINT fk_asignacion_funcion_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_asignacion_funcion_funcion FOREIGN KEY (funcion_id)
        REFERENCES funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_asignacion_funcion_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_asignacion_funcion_supervisor FOREIGN KEY (supervisor_asignacion_id)
        REFERENCES asignacion_funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Historial de funciones por empleado, estación y período.';

-- Acuerdos de suministro entre un proveedor y una estación.
CREATE TABLE acuerdo_suministro (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    proveedor_id INT NOT NULL,
    estacion_id INT NOT NULL,
    desde DATE NOT NULL,
    hasta DATE NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' COMMENT 'Un acuerdo nuevo comienza activo.',
    CONSTRAINT pk_acuerdo_suministro PRIMARY KEY (id),
    CONSTRAINT uq_acuerdo_suministro_codigo UNIQUE (codigo),
    CONSTRAINT ck_acuerdo_suministro_estado CHECK (estado IN ('ACTIVO', 'SUSPENDIDO', 'FINALIZADO')),
    CONSTRAINT ck_acuerdo_suministro_fechas CHECK (hasta IS NULL OR hasta >= desde),
    CONSTRAINT fk_acuerdo_suministro_proveedor FOREIGN KEY (proveedor_id)
        REFERENCES proveedor (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_acuerdo_suministro_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Acuerdos de suministro por proveedor y estación.';

-- Productos y condiciones pactadas en cada acuerdo.
CREATE TABLE detalle_acuerdo_suministro (
    id INT NOT NULL AUTO_INCREMENT,
    acuerdo_id INT NOT NULL,
    producto_id INT NOT NULL,
    precio_acordado DECIMAL(12,4) NOT NULL,
    cantidad_minima DECIMAL(12,3) NOT NULL DEFAULT 0 COMMENT 'Sin mínimo especificado equivale a cero.',
    CONSTRAINT pk_detalle_acuerdo_suministro PRIMARY KEY (id),
    CONSTRAINT uq_detalle_acuerdo_producto UNIQUE (acuerdo_id, producto_id),
    CONSTRAINT ck_detalle_acuerdo_precio CHECK (precio_acordado >= 0),
    CONSTRAINT ck_detalle_acuerdo_cantidad CHECK (cantidad_minima >= 0),
    CONSTRAINT fk_detalle_acuerdo_suministro_acuerdo FOREIGN KEY (acuerdo_id)
        REFERENCES acuerdo_suministro (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_acuerdo_suministro_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Productos, precios y cantidades mínimas pactados en acuerdos.';

-- Inscripciones de clientes en campañas.
CREATE TABLE inscripcion_campania (
    id INT NOT NULL AUTO_INCREMENT,
    campania_id INT NOT NULL,
    cliente_id INT NOT NULL,
    inscrita_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Registra la fecha y hora de inscripción.',
    estado VARCHAR(12) NOT NULL DEFAULT 'ACTIVA' COMMENT 'Una inscripción nueva comienza activa.',
    CONSTRAINT pk_inscripcion_campania PRIMARY KEY (id),
    CONSTRAINT uq_inscripcion_campania_cliente UNIQUE (campania_id, cliente_id),
    CONSTRAINT ck_inscripcion_campania_estado CHECK (estado IN ('ACTIVA', 'CANCELADA')),
    CONSTRAINT fk_inscripcion_campania_campania FOREIGN KEY (campania_id)
        REFERENCES campania (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_inscripcion_campania_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Relación N:M entre clientes y campañas con fecha y estado.';

-- Precio vigente por producto y estación.
CREATE TABLE precio_producto (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    producto_id INT NOT NULL,
    precio DECIMAL(12,4) NOT NULL,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora iniciales del precio.',
    actualizado_por INT NULL,
    CONSTRAINT pk_precio_producto PRIMARY KEY (id),
    CONSTRAINT uq_precio_producto_estacion_producto UNIQUE (estacion_id, producto_id),
    CONSTRAINT ck_precio_producto_precio CHECK (precio >= 0),
    CONSTRAINT fk_precio_producto_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_precio_producto_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_precio_producto_empleado FOREIGN KEY (actualizado_por)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Precio actualmente aplicable a un producto en una estación.';

-- Historial de variaciones. La primera alta puede no tener precio anterior.
CREATE TABLE historial_precio (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    producto_id INT NOT NULL,
    precio_anterior DECIMAL(12,4) NULL,
    precio_nuevo DECIMAL(12,4) NOT NULL,
    cambiado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora inicial del registro.',
    empleado_id INT NULL,
    CONSTRAINT pk_historial_precio PRIMARY KEY (id),
    CONSTRAINT ck_historial_precio_nuevo CHECK (precio_nuevo >= 0),
    CONSTRAINT fk_historial_precio_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_historial_precio_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_historial_precio_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Historial de cambios de precio; la captura automática requerirá un trigger.';

-- Cabecera de venta. cliente_id nullable permite ventas anónimas.
CREATE TABLE venta (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    empleado_id INT NOT NULL,
    cliente_id INT NULL,
    codigo VARCHAR(30) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de registro de la venta.',
    tipo_documento VARCHAR(12) NOT NULL DEFAULT 'TICKET' COMMENT 'Una venta nueva se registra como ticket.',
    serie_factura VARCHAR(20) NULL,
    numero_factura INT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Sin descuento especificado, el descuento es cero.',
    total DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_venta PRIMARY KEY (id),
    CONSTRAINT uq_venta_estacion_codigo UNIQUE (estacion_id, codigo),
    CONSTRAINT uq_venta_factura UNIQUE (estacion_id, serie_factura, numero_factura),
    CONSTRAINT ck_venta_tipo_documento CHECK (tipo_documento IN ('TICKET', 'FACTURA')),
    CONSTRAINT ck_venta_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_venta_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_venta_total CHECK (total >= 0),
    CONSTRAINT ck_venta_datos_factura CHECK (
        (serie_factura IS NULL AND numero_factura IS NULL)
        OR (serie_factura IS NOT NULL AND numero_factura IS NOT NULL)
    ),
    CONSTRAINT fk_venta_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_venta_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Cabecera de ventas, facturadas o anónimas.';

-- Campañas aplicadas a una venta; descuento_aplicado conserva el valor histórico.
CREATE TABLE venta_campania (
    id INT NOT NULL AUTO_INCREMENT,
    venta_id INT NOT NULL,
    campania_id INT NOT NULL,
    descuento_aplicado DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_venta_campania PRIMARY KEY (id),
    CONSTRAINT uq_venta_campania UNIQUE (venta_id, campania_id),
    CONSTRAINT ck_venta_campania_descuento CHECK (descuento_aplicado >= 0),
    CONSTRAINT fk_venta_campania_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_venta_campania_campania FOREIGN KEY (campania_id)
        REFERENCES campania (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Campañas aplicadas y descuento registrado para cada venta.';

-- Líneas de venta: precio, código y nombre conservan los datos aplicados en ese momento.
CREATE TABLE linea_venta (
    id INT NOT NULL AUTO_INCREMENT,
    venta_id INT NOT NULL,
    numero_linea INT NOT NULL,
    producto_id INT NOT NULL,
    surtidor_id INT NULL,
    codigo_producto_aplicado VARCHAR(30) NOT NULL,
    nombre_producto_aplicado VARCHAR(120) NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,
    precio_unitario_aplicado DECIMAL(12,4) NOT NULL,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT 'Sin descuento indicado, la línea no tiene descuento.',
    total_linea DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_linea_venta PRIMARY KEY (id),
    CONSTRAINT uq_linea_venta_numero UNIQUE (venta_id, numero_linea),
    CONSTRAINT ck_linea_venta_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_linea_venta_precio CHECK (precio_unitario_aplicado >= 0),
    CONSTRAINT ck_linea_venta_descuento CHECK (descuento >= 0),
    CONSTRAINT ck_linea_venta_total CHECK (total_linea >= 0),
    CONSTRAINT fk_linea_venta_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_linea_venta_producto FOREIGN KEY (producto_id)
        REFERENCES producto (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_linea_venta_surtidor FOREIGN KEY (surtidor_id)
        REFERENCES surtidor (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Detalle vendido, incluyendo el precio efectivamente aplicado.';

-- Una venta puede dividirse en varios pagos, incluso con el mismo medio.
CREATE TABLE pago_venta (
    id INT NOT NULL AUTO_INCREMENT,
    venta_id INT NOT NULL,
    medio_pago_id INT NOT NULL,
    importe DECIMAL(12,2) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del pago.',
    referencia VARCHAR(100) NULL,
    CONSTRAINT pk_pago_venta PRIMARY KEY (id),
    CONSTRAINT ck_pago_venta_importe CHECK (importe > 0),
    CONSTRAINT fk_pago_venta_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pago_venta_medio_pago FOREIGN KEY (medio_pago_id)
        REFERENCES medio_pago (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Pagos parciales o completos asociados a una venta.';

-- Devoluciones vinculadas a la venta original.
CREATE TABLE devolucion (
    id INT NOT NULL AUTO_INCREMENT,
    venta_id INT NOT NULL,
    empleado_id INT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la devolución.',
    motivo VARCHAR(255) NULL,
    total DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_devolucion PRIMARY KEY (id),
    CONSTRAINT uq_devolucion_codigo UNIQUE (codigo),
    CONSTRAINT ck_devolucion_total CHECK (total >= 0),
    CONSTRAINT fk_devolucion_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_devolucion_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Cabecera de devolución asociada a una venta original.';

-- Una misma línea de venta puede aparecer en devoluciones distintas para permitir
-- devoluciones parciales sucesivas.
CREATE TABLE linea_devolucion (
    id INT NOT NULL AUTO_INCREMENT,
    devolucion_id INT NOT NULL,
    linea_venta_id INT NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,
    precio_unitario_reembolsado DECIMAL(12,4) NOT NULL,
    importe DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_linea_devolucion PRIMARY KEY (id),
    CONSTRAINT uq_linea_devolucion_en_devolucion UNIQUE (devolucion_id, linea_venta_id),
    CONSTRAINT ck_linea_devolucion_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_linea_devolucion_precio CHECK (precio_unitario_reembolsado >= 0),
    CONSTRAINT ck_linea_devolucion_importe CHECK (importe >= 0),
    CONSTRAINT fk_linea_devolucion_devolucion FOREIGN KEY (devolucion_id)
        REFERENCES devolucion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_linea_devolucion_linea_venta FOREIGN KEY (linea_venta_id)
        REFERENCES linea_venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Líneas e importes devueltos en cada operación de devolución.';

-- Un reembolso puede dividirse entre varios medios de pago.
CREATE TABLE reembolso_devolucion (
    id INT NOT NULL AUTO_INCREMENT,
    devolucion_id INT NOT NULL,
    medio_pago_id INT NOT NULL,
    importe DECIMAL(12,2) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del reembolso.',
    referencia VARCHAR(100) NULL,
    CONSTRAINT pk_reembolso_devolucion PRIMARY KEY (id),
    CONSTRAINT ck_reembolso_devolucion_importe CHECK (importe > 0),
    CONSTRAINT fk_reembolso_devolucion_devolucion FOREIGN KEY (devolucion_id)
        REFERENCES devolucion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_reembolso_devolucion_medio_pago FOREIGN KEY (medio_pago_id)
        REFERENCES medio_pago (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Importes reembolsados por devolución y medio de pago.';

-- Incidencias: sus asociaciones con empleado, surtidor o venta son opcionales.
CREATE TABLE incidencia (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    estacion_id INT NOT NULL,
    empleado_id INT NULL,
    surtidor_id INT NULL,
    venta_id INT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de apertura de la incidencia.',
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'ABIERTA' COMMENT 'Una incidencia nueva comienza abierta.',
    resuelta_en DATETIME NULL,
    CONSTRAINT pk_incidencia PRIMARY KEY (id),
    CONSTRAINT uq_incidencia_codigo UNIQUE (codigo),
    CONSTRAINT ck_incidencia_estado CHECK (estado IN ('ABIERTA', 'EN_CURSO', 'RESUELTA', 'CERRADA')),
    CONSTRAINT ck_incidencia_resuelta CHECK (resuelta_en IS NULL OR resuelta_en >= fecha),
    CONSTRAINT fk_incidencia_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_incidencia_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_incidencia_surtidor FOREIGN KEY (surtidor_id)
        REFERENCES surtidor (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_incidencia_venta FOREIGN KEY (venta_id)
        REFERENCES venta (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Incidencias operativas asociadas opcionalmente a empleado, surtidor o venta.';
