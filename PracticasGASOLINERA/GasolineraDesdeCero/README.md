# Gasolinera desde cero

Práctica de 2.º DAM de Acceso a Datos. Parte de la aplicación de clientes y pagos (CSV y JSON) y amplía el modelo para gestionar la tienda y la actividad diaria de las estaciones.

Esta es la **fase 1**: diseño relacional, clases del dominio y registro automático de cambios de precio en MySQL. Las consultas y las operaciones de persistencia desde Java se dejan para la fase siguiente.

## Requisitos

- Java 21.
- MySQL 8.0.16 o posterior, porque el esquema usa restricciones `CHECK`.
- Un cliente MySQL que admita `DELIMITER` para ejecutar los scripts.
- IntelliJ IDEA es opcional. Todavía no hay `pom.xml` ni dependencias Maven, porque en esta fase no se ejecuta JDBC desde Java.

## Organización del proyecto

- `src/`: aplicación original en el paquete por defecto (`Main`, `Gasolinera`, `Cliente`, `Pago`, interfaces y almacenamientos CSV/JSON).
- `src/gasolinera/dominio/`: modelo nuevo de la ampliación. Estaciones (`Estacion`, `ConfiguracionFacturacion`, `Surtidor`, `TipoCombustible`, `EstadoServicio`, `DemostracionEstaciones`) y personal (`Empleado`, `Funcion`, `Cajero`, `Expendedor`, `ResponsableTienda`, `JefeEstacion`, `TipoFuncion`, `AsignacionFuncion`, `CondicionFuncion`, `CondicionCajero`, `CondicionExpendedor`, `CondicionResponsableTienda`, `CondicionJefeEstacion`, `DemostracionPersonal`), además de `Validaciones`.
- `src/gasolinera/persistencia/`: espacio reservado para la fase JDBC. Todavía no existe.
- `sql/`: scripts MySQL.

## Cómo se gestiona «El encargo»

| Requisito del encargo | Cómo se gestiona | Estado |
| --- | --- | --- |
| Trabajar sobre el proyecto actual | Se conservan `Main`, `Gasolinera`, `Cliente`, `Pago` y los almacenamientos CSV y JSON. No se han borrado ni reescrito. | Cumplido |
| La persistencia MySQL debe convivir con CSV y JSON | MySQL será una implementación más detrás de las interfaces existentes. CSV y JSON siguen funcionando y **no se sincronizan** entre sí. | Cumplido en el diseño; implementación en fase 2 |
| Revisar las interfaces existentes | `I_AlmacenamientoCliente` e `I_AlmacenamientoPago` **no se modifican**. Las futuras clases MySQL las implementarán tal cual. Si más adelante hiciera falta ampliarlas, se justificará y se probará en todas las implementaciones. | Revisado, sin cambios |
| Separar modelo, lógica y almacenamiento | `Gasolinera` coordina la aplicación a través de las interfaces. Las clases de `gasolinera.dominio` no leen ficheros ni ejecutan SQL. Los almacenamientos solo se encargan de guardar y consultar. | Cumplido |
| Registro automático de cambios de precio | Se hará con un **trigger en MySQL**, no en Java, en `02_triggers.sql` (apartado específico del enunciado). | Pendiente de su apartado |
| No ejecutar consultas JDBC todavía | No se añade driver, ni `ConexionMySQL`, ni métodos de inserción/consulta desde Java. | Cumplido |

## Persistencia actual y futura

- `Main` usa JSON por defecto (`clientes.json` y `pagos.json`). Las implementaciones CSV siguen disponibles.
- `MigraCSVToJson` copia los datos de CSV a JSON sin borrar los originales; exige que los JSON de destino estén vacíos.
- En la fase 2 se creará `gasolinera.persistencia` con `ConexionMySQL` y las implementaciones de las interfaces. `Main` elegirá qué implementación usar.
- La configuración de la conexión se hará con las variables de entorno `GASOLINERA_MYSQL_URL`, `GASOLINERA_MYSQL_USER` y `GASOLINERA_MYSQL_PASSWORD`. No se guardarán credenciales en el código ni en el repositorio.

## Scripts SQL

Se ejecutan en este orden sobre un entorno vacío:

1. `sql/01_esquema.sql`: crea `gasolinera_mysql`, las tablas de estaciones (`estacion`, `configuracion_facturacion`, `surtidor`) y las de personal (`empleado`, `funcion`, `asignacion_funcion`, `condicion_cajero`, `condicion_expendedor`, `condicion_responsable_tienda`, `condicion_jefe_estacion`). **Ya creado; todavía no ejecutado contra MySQL.**
2. `sql/02_triggers.sql`: trigger del historial de precios. Pendiente.
3. `sql/03_datos.sql`: datos de ejemplo. Pendiente.
4. `sql/04_comprobaciones.sql`: consultas y pruebas de restricciones. Pendiente.

Los scripts no desactivan claves foráneas.

## Compilación y demostración

En IntelliJ se puede ejecutar `Main`, `gasolinera.dominio.DemostracionEstaciones` o `gasolinera.dominio.DemostracionPersonal`. Desde una terminal, situados en esta carpeta:

```text
javac -encoding UTF-8 -d out src/*.java src/gasolinera/dominio/*.java
java -cp out gasolinera.dominio.DemostracionEstaciones
java -cp out gasolinera.dominio.DemostracionPersonal
```

La demostración de estaciones crea dos estaciones con su ficha de facturación y sus surtidores, reserva números de ticket y comprueba que se rechazan un número visible repetido, la retirada del último surtidor y una segunda ficha de facturación. La de personal se describe más abajo.

## El personal y la organización del trabajo

### Tablas SQL añadidas

| Tabla | Columnas | Para qué sirve |
| --- | --- | --- |
| `empleado` | `id` (PK), `codigo` (único), `dni` (único), `nombre`, `telefono`, `email`, `fecha_incorporacion`, `estacion_id` (FK), `supervisor_id` (FK a `empleado`) | Identidad, antigüedad, adscripción actual y supervisor actual. |
| `funcion` | `id` (PK), `codigo` (único), `nombre` (único), `descripcion`, `tipo` | Catálogo de los cuatro tipos de función. `tipo` decide qué tabla de condiciones corresponde. |
| `asignacion_funcion` | `id` (PK), `empleado_id` (FK), `funcion_id` (FK), `desde`, `hasta` | Historial de periodos. `hasta = NULL` significa que sigue vigente. |
| `condicion_cajero` | `id` (PK), `asignacion_id` (FK única), `limite_devoluciones` | Límite autorizado de devoluciones de ese periodo. |
| `condicion_expendedor` | `id` (PK), `asignacion_id` (FK única), `numero_acreditacion`, `caducidad_acreditacion` | Acreditación para manipular combustibles. |
| `condicion_responsable_tienda` | `id` (PK), `asignacion_id` (FK única), `presupuesto_compras` | Presupuesto autorizado de compras. |
| `condicion_jefe_estacion` | `id` (PK), `asignacion_id` (FK única), `nivel_autorizacion` | Nivel para operaciones excepcionales. |

Todas son InnoDB con `utf8mb4`, PK `INT AUTO_INCREMENT` y `ON UPDATE RESTRICT ON DELETE RESTRICT` en las claves foráneas.

### Decisiones de diseño y justificación

- **Un solo `empleado` para toda la vida laboral.** El código, el DNI y la fecha de incorporación no cambian. La estación actual va en `empleado`, pero cada venta futura guardará su propia estación, así que cambiar de adscripción no reescribe el historial de ventas.
- **Al menos un medio de contacto.** Siguiendo tu criterio, `empleado` exige teléfono **o** email con un `CHECK`; `Empleado.setContacto(telefono, email)` aplica la misma regla en Java. Los dos no son obligatorios, pero no pueden faltar los dos.
- **Funciones como jerarquía Java.** `Funcion` es abstracta y guarda los datos comunes; `Cajero`, `Expendedor`, `ResponsableTienda` y `JefeEstacion` son subclases que devuelven su `TipoFuncion`. Así se pueden añadir funciones nuevas creando otra subclase, otro valor del enum y otra tabla de condiciones, sin meter campos vacíos en una asignación genérica.
- **Condiciones como jerarquía paralela.** `CondicionFuncion` es abstracta (`getId`, `getTipo`, `getResumen`) y cada subclase guarda solo su información: límite, acreditación, presupuesto o nivel. `Funcion.admiteCondicion(...)` impide, por ejemplo, asignar una condición de jefe a un cajero.
- **Una tabla de condiciones por tipo en SQL.** Cada asignación tiene como mucho una fila en su tabla correspondiente (`UNIQUE(asignacion_id)`). Es más clara que una tabla con muchas columnas opcionales y encaja con la jerarquía Java.
- **Periodos independientes.** `AsignacionFuncion` guarda desde, hasta y condición. Una persona puede tener varias funciones vigentes a la vez, abandonar una y recuperarla después con otra condición: se crea otro periodo y el antiguo queda intacto. También puede no tener ninguna función vigente, durante una incorporación o un cambio de responsabilidades.
- **Supervisión separada de las funciones.** `empleado.supervisor_id` es una FK a `empleado`, sin histórico en esta fase. No está en `asignacion_funcion`: supervisar no equivale a ser jefe, y la supervisión actual no cambia cuando cambia una función.
- **Sin solapamientos de la misma función.** `Empleado.asignarFuncion(...)` usa `AsignacionFuncion.seSolapaCon(...)`, que solo compara periodos del mismo `TipoFuncion`. Funciones distintas sí pueden solaparse.
- **Sin ciclos de supervisión.** `Empleado.setSupervisor(...)` rechaza la auto-supervisión y recorre la cadena de supervisores para rechazar ciclos indirectos.

### Reglas que MySQL no garantiza por sí solo

1. Que no existan periodos solapados de la misma función: el `CHECK` compara fechas de una fila, no de varias. Lo valida Java y se repetirá en la fase JDBC dentro de la transacción.
2. Que no haya ciclos indirectos de supervisión: el `CHECK` solo impide supervisarse a uno mismo.
3. Que cada asignación tenga la fila de condición de su tipo: no se puede exigir con FK entre cuatro tablas.

### Verificación de la demostración

`DemostracionPersonal` crea estaciones, catálogo de funciones y cinco empleados. Ana ejerce de cajera y expendedora a la vez; Luis fue jefe, pasó a responsable de tienda y volvió a ser jefe con otro nivel; Marta es cajera; un empleado no tiene funciones y otro tiene supervisor. Se comprueba que se rechazan cinco operaciones: un periodo de cajero solapado, supervisarse a sí mismo, un ciclo indirecto de supervisión, un empleado sin contacto y una condición que no corresponde a su función. Compila con Java 21 y termina sin errores.

## Comprobación de que CSV y JSON siguen funcionando

Se ha ejecutado un programa temporal **fuera del proyecto** que trabaja sobre copias de los ficheros en una carpeta temporal:

- CSV: lee 5 clientes y 4 pagos; la búsqueda de «Alvaro» devuelve 1 resultado; la matrícula `1234ABC` existe; el cliente con id 4 es `maria`.
- JSON: los ficheros del proyecto están vacíos. Sobre copias vacías se da de alta 1 cliente y 1 pago con importe 12.34, y se vuelven a leer correctamente.
- Los hashes SHA256 de `clientes.csv`, `pagos.csv`, `clientes.json` y `pagos.json` del proyecto son idénticos antes y después de la comprobación.

También se ha compilado todo el proyecto con Java 21 y se han ejecutado `DemostracionEstaciones` y `DemostracionPersonal` sin errores.

## Pendiente

- Resto de apartados del modelo relacional y de las clases Java.
- Ejecutar `01_esquema.sql` en MySQL para comprobar que las restricciones funcionan en la Base de Datos real.
- `02_triggers.sql`, `03_datos.sql` y `04_comprobaciones.sql`.
- Implementación de las reglas que no garantizan por sí solas las restricciones SQL (alta conjunta, último surtidor, etc.).
- Persistencia MySQL en la fase 2: driver, conexión e implementaciones de las interfaces.
