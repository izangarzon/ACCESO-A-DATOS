-- Esquema relacional de la gasolinera.
-- Apartado: "Las estaciones y su puesta en servicio".
-- Requiere MySQL 8.0.16 o posterior para que se apliquen las restricciones CHECK.
-- Todas las tablas usan InnoDB y una clave primaria formada por un único entero autoincremental.

CREATE DATABASE IF NOT EXISTS gasolinera_mysql
    CHARACTER SET utf8mb4;

USE gasolinera_mysql;

-- Estaciones de servicio.
-- codigo es la referencia de negocio única; id es la clave técnica.
CREATE TABLE estacion (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL COMMENT 'Código interno único en la empresa.',
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre comercial.',
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_apertura DATE NOT NULL,
    CONSTRAINT pk_estacion PRIMARY KEY (id),
    CONSTRAINT uq_estacion_codigo UNIQUE (codigo)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Estaciones de servicio.';

-- Configuración de emisión de tickets de cada estación (relación 1:1).
-- UNIQUE(estacion_id) impide que una ficha se comparta entre estaciones.
-- No incluye datos fiscales de la empresa: el enunciado los considera comunes a varias estaciones.
CREATE TABLE configuracion_facturacion (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL COMMENT 'Código único que usa soporte para identificar la ficha.',
    estacion_id INT NOT NULL,
    serie_tickets VARCHAR(20) NOT NULL,
    siguiente_numero INT NOT NULL DEFAULT 1 COMMENT 'Primer número que debe utilizar la estación.',
    texto_pie VARCHAR(255) NOT NULL COMMENT 'Texto al pie de los justificantes.',
    CONSTRAINT pk_configuracion_facturacion PRIMARY KEY (id),
    CONSTRAINT uq_configuracion_facturacion_codigo UNIQUE (codigo),
    CONSTRAINT uq_configuracion_facturacion_estacion UNIQUE (estacion_id),
    CONSTRAINT ck_configuracion_siguiente_numero CHECK (siguiente_numero > 0),
    CONSTRAINT fk_configuracion_facturacion_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Ficha de configuración de facturación, una por estación.';

-- Surtidores: siempre pertenecen a una estación, incluso si están averiados.
-- codigo_equipo es único en la empresa; numero_visible solo es único dentro de la estación.
CREATE TABLE surtidor (
    id INT NOT NULL AUTO_INCREMENT,
    estacion_id INT NOT NULL,
    codigo_equipo VARCHAR(20) NOT NULL COMMENT 'Código de equipo único en toda la empresa.',
    numero_visible INT NOT NULL COMMENT 'Número visible dentro de la estación.',
    tipo_combustible VARCHAR(20) NOT NULL,
    estado_servicio VARCHAR(20) NOT NULL DEFAULT 'OPERATIVO',
    CONSTRAINT pk_surtidor PRIMARY KEY (id),
    CONSTRAINT uq_surtidor_codigo_equipo UNIQUE (codigo_equipo),
    CONSTRAINT uq_surtidor_estacion_numero UNIQUE (estacion_id, numero_visible),
    CONSTRAINT ck_surtidor_numero_visible CHECK (numero_visible > 0),
    CONSTRAINT ck_surtidor_tipo_combustible CHECK (tipo_combustible IN ('GASOLINA_95', 'GASOLINA_98', 'DIESEL')),
    CONSTRAINT ck_surtidor_estado_servicio CHECK (estado_servicio IN ('OPERATIVO', 'AVERIADO', 'MANTENIMIENTO')),
    CONSTRAINT fk_surtidor_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Surtidores, siempre adscritos a una estación.';

-- Empleados. La estación y el supervisor son la situación actual, no un histórico.
-- El CHECK exige al menos un medio de contacto: teléfono o email.
CREATE TABLE empleado (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL COMMENT 'Número interno único en la empresa.',
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    fecha_incorporacion DATE NOT NULL,
    estacion_id INT NOT NULL,
    supervisor_id INT NULL,
    CONSTRAINT pk_empleado PRIMARY KEY (id),
    CONSTRAINT uq_empleado_codigo UNIQUE (codigo),
    CONSTRAINT uq_empleado_dni UNIQUE (dni),
    CONSTRAINT ck_empleado_contacto CHECK (telefono IS NOT NULL OR email IS NOT NULL),
    CONSTRAINT ck_empleado_no_se_supervisa CHECK (supervisor_id IS NULL OR supervisor_id <> id),
    CONSTRAINT fk_empleado_estacion FOREIGN KEY (estacion_id)
        REFERENCES estacion (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_empleado_supervisor FOREIGN KEY (supervisor_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Personal con adscripción y supervisor actuales.';

-- Catálogo de funciones. tipo indica qué tabla de condiciones corresponde a cada asignación.
CREATE TABLE funcion (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(255) NULL,
    tipo VARCHAR(25) NOT NULL,
    CONSTRAINT pk_funcion PRIMARY KEY (id),
    CONSTRAINT uq_funcion_codigo UNIQUE (codigo),
    CONSTRAINT uq_funcion_nombre UNIQUE (nombre),
    CONSTRAINT ck_funcion_tipo CHECK (tipo IN ('CAJERO', 'EXPENDEDOR', 'RESPONSABLE_TIENDA', 'JEFE_ESTACION'))
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Catálogo de funciones laborales.';

-- Historial de periodos de una función. No guarda estación ni supervisor:
-- la estación es un dato del empleado y el supervisor es una relación distinta.
CREATE TABLE asignacion_funcion (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    funcion_id INT NOT NULL,
    desde DATETIME NOT NULL,
    hasta DATETIME NULL COMMENT 'NULL mientras el periodo sigue vigente.',
    CONSTRAINT pk_asignacion_funcion PRIMARY KEY (id),
    CONSTRAINT ck_asignacion_funcion_fechas CHECK (hasta IS NULL OR hasta > desde),
    CONSTRAINT fk_asignacion_funcion_empleado FOREIGN KEY (empleado_id)
        REFERENCES empleado (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_asignacion_funcion_funcion FOREIGN KEY (funcion_id)
        REFERENCES funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Periodos en los que un empleado desempeña una función.';

-- Condiciones específicas de cada tipo de función, una fila por periodo.
CREATE TABLE condicion_cajero (
    id INT NOT NULL AUTO_INCREMENT,
    asignacion_id INT NOT NULL,
    limite_devoluciones DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_condicion_cajero PRIMARY KEY (id),
    CONSTRAINT uq_condicion_cajero_asignacion UNIQUE (asignacion_id),
    CONSTRAINT ck_condicion_cajero_limite CHECK (limite_devoluciones > 0),
    CONSTRAINT fk_condicion_cajero_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Límite autorizado de devoluciones en un periodo de cajero.';

CREATE TABLE condicion_expendedor (
    id INT NOT NULL AUTO_INCREMENT,
    asignacion_id INT NOT NULL,
    numero_acreditacion VARCHAR(30) NOT NULL,
    caducidad_acreditacion DATE NOT NULL,
    CONSTRAINT pk_condicion_expendedor PRIMARY KEY (id),
    CONSTRAINT uq_condicion_expendedor_asignacion UNIQUE (asignacion_id),
    CONSTRAINT fk_condicion_expendedor_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Acreditación para manipular combustibles de un periodo de expendedor.';

CREATE TABLE condicion_responsable_tienda (
    id INT NOT NULL AUTO_INCREMENT,
    asignacion_id INT NOT NULL,
    presupuesto_compras DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_condicion_responsable_tienda PRIMARY KEY (id),
    CONSTRAINT uq_condicion_responsable_tienda_asignacion UNIQUE (asignacion_id),
    CONSTRAINT ck_condicion_responsable_presupuesto CHECK (presupuesto_compras >= 0),
    CONSTRAINT fk_condicion_responsable_tienda_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Presupuesto autorizado de compras en un periodo de responsable de tienda.';

CREATE TABLE condicion_jefe_estacion (
    id INT NOT NULL AUTO_INCREMENT,
    asignacion_id INT NOT NULL,
    nivel_autorizacion INT NOT NULL,
    CONSTRAINT pk_condicion_jefe_estacion PRIMARY KEY (id),
    CONSTRAINT uq_condicion_jefe_asignacion UNIQUE (asignacion_id),
    CONSTRAINT ck_condicion_jefe_nivel CHECK (nivel_autorizacion > 0),
    CONSTRAINT fk_condicion_jefe_asignacion FOREIGN KEY (asignacion_id)
        REFERENCES asignacion_funcion (id) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4
  COMMENT = 'Nivel para operaciones excepcionales en un periodo de jefe de estación.';

-- Reglas que NO quedan garantizadas únicamente por restricciones SQL:
-- 1. Alta conjunta estación + ficha. MySQL no permite que ambas tablas tengan entre sí
--    una FK obligatoria sin caer en referencias circulares imposibles de cargar.
--    La aplicación insertará las dos filas en una sola transacción (fase JDBC).
-- 2. Toda estación debe tener al menos un surtidor y no se puede retirar el último.
--    Se comprobará en la aplicación dentro de la misma transacción. En el modelo Java,
--    Estacion.quitarSurtidor ya impide dejar la estación sin equipos.
-- 3. Periodos solapados de una misma función para un empleado. El CHECK de fechas
--    solo compara cada fila por separado. Empleado.asignarFuncion lo valida en Java
--    y la fase JDBC deberá repetirlo dentro de la transacción.
-- 4. Ciclos de supervisión. El CHECK impide supervisarse a uno mismo, pero no detecta
--    ciclos indirectos. Empleado.setSupervisor recorre la cadena y los rechaza.
-- 5. Cada asignación debe tener la fila de condición de su tipo. No se puede exigir
--    con FK entre varias tablas; la aplicación lo garantizará al dar de alta.
