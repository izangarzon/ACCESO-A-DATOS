# **ACCESO A DATOS**

#### 

##### **GASOLINERA**

##### 

##### **JDK**



El proyecto ha sido desarrollado utilizando \*\*JDK 21\*\*.



##### **Compilación y ejecución**



Se puede ejecutar directamente desde IntelliJ IDEA mediante la clase "Main".



##### **Ficheros**



La aplicación utiliza dos ficheros CSV, situados en la carpeta del proyecto:



\* "clientes.csv"

\* "pagos.csv"



###### **clientes.csv**



Formato:



id, nombre, telefono, matricula

&#x20;

Ejemplo:



1,Izan,654456654,7465JCN



###### **pagos.csv**



Formato:



idPago, idCliente, fecha, importe, litros, combustible



Ejemplo:



1,1,2026/09/30,25.50,10.50,Gasolina 95



Los ficheros se crean automáticamente si no existen.



##### **Validación de datos**



La aplicación realiza diferentes validaciones antes de almacenar la información.



###### **Clientes:**

El nombre solamente puede contener letras y espacios.

El teléfono debe contener exactamente 9 números.

La matrícula debe tener 4 números y 3 letras.

No se permiten matrículas duplicadas.



###### **Pagos:**

La fecha debe tener un formato válido.

Si la fecha se deja vacía, se utiliza la fecha actual.

El importe debe ser mayor que 0.

Los litros deben ser mayores que 0.

Importe y litros permiten como máximo dos decimales.

Se permite utilizar . o , como separador decimal.

El combustible debe pertenecer a una de las opciones disponibles.



##### **Decisiones de diseño**



He planteado las clases de manera que haya las principales de Cliente y Pago que van a ser los objetos con sus constructores y poco mas. Después he creado las interfaces que he hecho dos para tener divididos los métodos que tienen que hacer con los pagos y los clientes. Una vez tengo estas interfaces creo las clases PagoCSV y ClienteCSV en la cual voy a realizar los overrides de dichos métodos contenidos en las interfaces. Se juntan estos métodos con una clase Gasolinera desde la cual voy a restringir algunas cosas de la entrada por el scanner y la cual va a ser la única a la que tenga que acudir la ultima clase que es el main donde estará el menú con sus casos y sus llamadas a esta clase para poder realizar todas las funciones requeridas.

En caso de un cambio de formato se tendrían que modificar principalmente las clases PagoCSV y ClienteCSV.

