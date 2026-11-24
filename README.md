# Acceso a Datos: gestión de gasolinera

Proyecto Java de prácticas de 2.º de DAM. Conserva una aplicación sencilla de gestión de clientes y pagos mediante ficheros CSV o JSON e incorpora un modelo de dominio para la ampliación de una gasolinera, scripts de esquema y datos para MySQL y demostraciones de conexión JDBC.

## Requisitos de software

- **Java 21** (JDK). El `pom.xml` fija `maven.compiler.release` en 21.
- **Apache Maven** para resolver las dependencias y compilar. El proyecto no fija una versión de Maven.
- **MySQL 8.0.16 o posterior** para ejecutar el esquema: a partir de esa versión MySQL aplica las restricciones `CHECK` utilizadas.
- Un cliente MySQL, por ejemplo MySQL Workbench, para ejecutar los scripts. `02_triggers.sql` utiliza `DELIMITER`.
- IntelliJ IDEA es opcional; las clases con `main` también se pueden ejecutar desde una terminal.

## Dependencias Java

Maven obtiene las dependencias declaradas en `Gasolinera2BBDD/pom.xml`:

| Dependencia | Versión | Uso |
|---|---:|---|
| `org.xerial:sqlite-jdbc` | `3.49.1.0` | Driver JDBC de SQLite. |
| `com.mysql:mysql-connector-j` | `9.2.0` | Driver JDBC de MySQL. |

El `pom.xml` también fija `maven-compiler-plugin` en `3.13.0`. Para compilar desde la carpeta `Gasolinera2BBDD`:

```text
mvn clean compile
```

## Organización del proyecto

- `Gasolinera2BBDD/src/`: fuentes Java.
- Las clases originales de la aplicación (`Main`, `Gasolinera`, `Cliente`, `Pago`, interfaces y almacenamientos) están en el paquete por defecto.
- `gasolinera.dominio`: modelo ampliado de estaciones, surtidores, empleados y funciones, clientes y tarjetas, productos, proveedores, campañas, ventas, pagos, devoluciones e incidencias. Incluye `ProductoEnvasado`, `ProductoGranel` y `DemostracionVentaProductos`.
- `Gasolinera2BBDD/sql/`: esquema, trigger, datos de ejemplo y comprobaciones de MySQL.
- Los ficheros de la aplicación (`clientes.csv`, `pagos.csv`, `clientes.json`, `pagos.json`) se resuelven desde el directorio de trabajo de ejecución.

## Persistencia actual: CSV y JSON

La aplicación de consola de `Main` utiliza actualmente `AlmacenamientoClienteJson` y `AlmacenamientoPagoJson`, con `clientes.json` y `pagos.json`. Permite dar de alta, listar y buscar clientes, registrar pagos y consultarlos.

También están disponibles las implementaciones CSV (`AlmacenamientoClienteCsv` y `AlmacenamientoPagoCsv`) mediante las mismas interfaces de almacenamiento. `MigraCSVToJson` permite copiar clientes y pagos de los ficheros CSV a los JSON: requiere los CSV y que los JSON de destino estén vacíos; no elimina los originales. El menú ofrece esta conversión como opción 6 al iniciarse.

CSV y JSON son persistencias locales independientes; **la aplicación no cambia automáticamente entre ellas ni sincroniza sus datos**. No se debe cambiar manualmente su estructura. Los almacenamientos actuales interpretan formatos sencillos y pueden tener problemas con comas, comillas o caracteres de escape dentro de los campos.

## Preparar MySQL y ejecutar los scripts

1. Instala y arranca un servidor MySQL 8.0.16 o posterior.
2. Desde un cliente con permisos para crear la base de datos, selecciona y ejecuta, en este orden, los archivos de `Gasolinera2BBDD/sql/`:
   1. `01_esquema.sql`: crea `gasolinera_mysql` con codificación `utf8mb4` y las tablas InnoDB.
   2. `02_triggers.sql`: crea el trigger del historial de cambios de precio.
   3. `03_datos.sql`: carga los datos de ejemplo. Ejecútalo sobre tablas vacías; no está pensado para repetirse sobre datos ya cargados.
   4. `04_comprobaciones.sql`: ejecuta consultas y pruebas de las restricciones, precios, trigger y datos.

Los scripts no desactivan las claves foráneas. Ejecútalos individualmente en MySQL Workbench o en un cliente que admita sus instrucciones. El script de datos incluye cambios de precio para probar el trigger, así como una actualización revertida con `ROLLBACK`.

## Configuración JDBC

`ConexionMySQL` es una demostración que abre una conexión y muestra metadatos; todavía no hay una implementación JDBC de las interfaces de almacenamiento ni integración de MySQL con `Main`. Para conectarla a la base creada por los scripts, configura las siguientes variables de entorno en IntelliJ (Run/Debug Configuration) o en el entorno desde el que ejecutes la clase:

- `GASOLINERA_MYSQL_URL`: `jdbc:mysql://localhost:3306/gasolinera_mysql`
- `GASOLINERA_MYSQL_USER`: usuario MySQL con permisos sobre esa base.
- `GASOLINERA_MYSQL_PASSWORD`: contraseña de ese usuario, configurada localmente.

No guardes contraseñas en el código ni en el repositorio. La URL y el usuario tienen valores predeterminados en la clase, pero la URL predeterminada apunta a `ra2_gasolinera`, no a la base `gasolinera_mysql` que crea `01_esquema.sql`; establece `GASOLINERA_MYSQL_URL` para usar el esquema de esta práctica.

`ConexionSQLite` demuestra una conexión al fichero `gasolinera.sqlite` del directorio de trabajo; admite una ruta alternativa como argumento y activa las claves foráneas en esa conexión. `MetadatosSQLite` muestra metadatos de otra conexión SQLite. No hay un esquema SQL SQLite equivalente ni almacenamientos JDBC implementados en esta ampliación.

## Demostración de productos

`gasolinera.dominio.DemostracionVentaProductos` muestra la compra de unidades envasadas y de una cantidad decimal a granel, calcula los importes con `BigDecimal` y redondeo monetario `HALF_UP` a dos decimales, y enseña los errores para cantidades o precisión no válidas. No accede a bases de datos ni a los ficheros CSV/JSON.

En IntelliJ, ejecuta el método `main` de esa clase. Desde una terminal, después de compilar y situado en `Gasolinera2BBDD`:

```text
java -cp target/classes gasolinera.dominio.DemostracionVentaProductos
```

## Funcionalidades implementadas y pendientes

**Implementado:** aplicación de clientes y pagos con almacenamientos CSV y JSON; conversión CSV a JSON; modelo de dominio Java para la ampliación; comportamiento polimórfico y validaciones de productos envasados y a granel; scripts MySQL de esquema, historial de precios, datos de ejemplo y comprobaciones; demostraciones de conexión JDBC para MySQL y SQLite.

**Pendiente:** implementar almacenamientos/DAO JDBC y conectarlos a la aplicación; integrar las ventas, productos y demás entidades del nuevo modelo con persistencia; trasladar los datos existentes de CSV/JSON a MySQL; implementar en transacciones y lógica de aplicación las reglas que no garantizan por sí solas las restricciones SQL. La conexión y los scripts MySQL requieren validación contra una instancia real del servidor.