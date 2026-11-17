# UT1: Gestor de Alumnos en Fichero de Texto (Ejercicio 8)



Aplicación de consola en Java que implementa la persistencia de datos en ficheros secuenciales de texto plano (`alumnos.txt`), utilizando la API clásica de I/O (`BufferedReader` y `BufferedWriter`).



---



## 📋 Funcionalidades



El programa ofrece un menú interactivo por consola con las siguientes opciones:



1. **Alta de alumno:** Permite registrar un alumno (nombre y clase) al final del archivo sin sobreescribir los datos existentes (modo \*append\* con `FileWriter(file, true)`).

2. **Mostrar alumnos:** Lee secuencialmente el archivo línea a línea con `BufferedReader` y muestra el listado completo en consola.

3. **Buscar alumno:** Realiza una búsqueda secuencial por coincidencia de texto e indica la línea donde se encuentra.

4. **Total de alumnos:** Cuenta el número total de registros almacenados en el archivo.

0. **Salir:** Cierra los flujos de entrada y finaliza la aplicación.



---



## 🛠️ Tecnologías y Buenas Prácticas



- **Lenguaje:** Java 21 (JDK 21)

- **Construcción:** Apache Maven

- **Entrada/Salida:**

&#x20; - `BufferedReader` / `BufferedWriter` para optimizar el acceso a disco mediante buffers de memoria.

&#x20; - Gestión automática de flujos con `try-with-resources`.

&#x20; - Tratamiento estricto de excepciones jerárquicas (`FileNotFoundException` y `IOException`).

&#x20; - Salto de línea portable mediante `bw.newLine()`.



---



## 🚀 Ejecución



### Desde NetBeans:

1. Abre el proyecto en el IDE.

2. Haz clic derecho sobre la clase `Ejercicio8Ficheros.java` y selecciona \*\*Run File\*\* (o pulsa `Shift + F6`).



### Desde consola (Maven):

```bash

mvn compile exec:java -Dexec.mainClass="edu.ficheros.ejercicio8ficheros.Ejercicio8Ficheros"

