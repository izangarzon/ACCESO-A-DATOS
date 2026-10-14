\# ACCESO A DATOS



\## GASOLINERA



\### JDK



El proyecto ha sido desarrollado utilizando \*\*JDK 21\*\*.



\### Compilación y ejecución



Se puede ejecutar desde IntelliJ IDEA mediante la clase `Main`.



\### Ficheros



La aplicación permite almacenar los datos en archivos CSV o JSON, situados en la carpeta del proyecto.



\*\*Archivos CSV:\*\*

\- `clientes.csv`

\- `pagos.csv`



\*\*Archivos JSON:\*\*

\- `clientes.json`

\- `pagos.json`



Los archivos se crean automáticamente cuando es necesario.



\### Formato de los datos



\*\*clientes.csv\*\*



Formato: `id,nombre,telefono,matricula`



Ejemplo:



`1,Izan,654456654,7465JCN`



\*\*pagos.csv\*\*



Formato: `idPago,idCliente,fecha,importe,litros,combustible`



Ejemplo:



`1,1,2026/09/30,25.50,10.50,Gasolina 95`



Los archivos JSON almacenan los clientes y los pagos en sus respectivos arrays.



\### Migración de CSV a JSON



La clase `MigraCSVToJson` permite trasladar los datos de los archivos CSV a JSON.



Antes de realizar la migración, se comprueba que existan los archivos CSV y que los archivos JSON de destino estén vacíos. Los archivos CSV originales se mantienen.



\### Validación de datos



\*\*Clientes:\*\*

\- El nombre solo puede contener letras y espacios.

\- El teléfono debe contener exactamente 9 números.

\- La matrícula debe tener 4 números y 3 letras.

\- No se permiten matrículas duplicadas.



\*\*Pagos:\*\*

\- La fecha debe tener un formato válido. Si se deja vacía, se utiliza la fecha actual.

\- El importe y los litros deben ser mayores que 0 y admitir como máximo dos decimales.

\- Se permite utilizar `.` o `,` como separador decimal.

\- El combustible debe pertenecer a las opciones disponibles.

\- El cliente asociado al pago debe existir.



\### Decisiones de diseño



Se han separado las responsabilidades mediante las clases `Cliente`, `Pago` y `Gasolinera`, junto con interfaces y clases específicas para el almacenamiento.



Las interfaces permiten utilizar distintas implementaciones de almacenamiento, como CSV y JSON, sin modificar toda la lógica de la aplicación.



Los clientes y los pagos tienen identificadores independientes, y los pagos se relacionan con los clientes mediante su identificador.



Esta estructura facilita el mantenimiento del código y la incorporación de nuevos formatos de almacenamiento.

