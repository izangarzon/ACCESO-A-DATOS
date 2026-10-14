# CAMBIOS.md

## 1. Versión inicial

Se ha partido de la versión inicial de la Práctica 1, en la que los datos de los clientes y los pagos se almacenaban en archivos CSV.

Esta versión permitía registrar clientes, consultar sus datos y gestionar los pagos de los repostajes.

## 2. Cambios realizados

### Almacenamiento de datos

Se han creado las clases `AlmacenamientoClienteJson` y `AlmacenamientoPagoJson` para incorporar el almacenamiento en formato JSON.

Se han mantenido las interfaces de almacenamiento para separar las operaciones de gestión de datos de la forma en que se guardan.

### Gestión de clientes y pagos

Se ha adaptado el programa para trabajar con los nuevos archivos JSON, manteniendo las operaciones de registro y consulta de clientes y pagos.

También se ha tenido en cuenta la relación entre los pagos y los clientes mediante sus identificadores.

### Migración de CSV a JSON

Se ha creado la clase `MigraCSVToJson` para trasladar los datos almacenados en CSV a archivos JSON.

El objetivo es conservar la información de los clientes y los pagos al cambiar el formato de almacenamiento, sin modificar los archivos CSV originales.

### Mejoras en la lectura de archivos

Se ha utilizado `BufferedReader` para leer los archivos línea a línea y facilitar el procesamiento de los datos.

También se han añadido comprobaciones para detectar datos incorrectos, como pagos asociados a clientes que no existen.

## 3. Decisiones de diseño

- **Uso de interfaces:** permite separar las operaciones de almacenamiento de su implementación concreta.
- **Separación por clases:** las clases de almacenamiento se encargan de guardar y consultar datos, mientras que `Gasolinera` gestiona las operaciones de la aplicación.
- **Identificadores independientes:** los clientes y los pagos tienen sus propios identificadores para poder distinguir cada registro.
- **Conservación de los CSV:** los archivos originales se mantienen para evitar perder los datos durante la migración.

## 4. Pruebas realizadas

Se han realizado comprobaciones del registro y la consulta de clientes y pagos, así como de la lectura de los archivos.

También se ha comprobado el comportamiento del programa ante algunos errores de datos y se continuará verificando que la información se conserve correctamente después de cerrar y volver a ejecutar la aplicación.