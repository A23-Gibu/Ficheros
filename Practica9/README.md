# GestorProductosRA1 - Persistencia y Gestión de Catálogo

Aplicación de consola en Java desarrollada como proyecto integrador para la unidad de trabajo **UT1 (Manejo de Ficheros en Java)** del ciclo formativo de grado superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

El programa gestiona un inventario estructurado de productos aplicando persistencia en ficheros de texto plano secuenciales (`CSV`, `TSV` y exportación a `JSON`), combinando las APIs clásicas de entrada/salida (`java.io`) y la API moderna NIO.2 (`java.nio.file`).

---

## 📋 Descripción de Formatos y Decisiones de Diseño

### 1. Formato CSV (`datos/productos.csv`)
- **Estructura:** Fichero delimitado por caracteres donde la primera línea actúa como cabecera obligatoria (`ID;NOMBRE;PRECIO`) y el resto de líneas representan registros individuales.
- **Delimitador:** Se emplea punto y coma (`;`). Es el estándar preferido en entornos hispanohablantes y europeos para evitar colisiones con la coma decimal utilizada en cantidades monetarias y números en punto flotante (`29,99`).
- **Lectura e integridad:** El procesado se realiza mediante `split(";", -1)` para preservar valores vacíos terminales y no perder la correspondencia posicional de columnas.

### 2. Formato TSV (`datos/productos.tsv`)
- **Estructura y conversión:** Utiliza el tabulador (`\t`) como delimitador entre campos. La conversión conserva íntegramente la cabecera y el contenido de los registros.

### 3. Tratamiento de Campos con Delimitador Incrustado
Si el nombre de un producto contiene el delimitador activo (por ejemplo, registrar un producto con `;` en su nombre o con tabulaciones `\t`), un `split()` elemental desalinearía las columnas convirtiendo 3 campos en 4 o más.
- **Decisión adoptada:** La aplicación valida preventivamente en las operaciones de alta y modificación que el nombre introducido no contenga el delimitador del archivo, interrumpiendo la operación e informando al usuario antes de comprometer el fichero en disco.

### 4. Modificación y Eliminación en Ficheros Secuenciales
A diferencia de los ficheros de acceso aleatorio binario (`RandomAccessFile`), en un fichero de texto con líneas de longitud variable no es posible sobreescribir un registro intermedio sin corromper el contenido posterior.
- **Patrón aplicado:** Se implementa el patrón **Leer → Procesar en Memoria → Reescribir**:
  1. Carga de líneas en una colección `List<String>`.
  2. Búsqueda y modificación/filtrado del registro en memoria.
  3. Reescritura íntegra del archivo abriendo el `FileWriter` en modo de sobreescritura (`append = false`).

---

## ⚙️ Arquitectura y Clases Empleadas

- **`Producto`:** Modelo de dominio con validaciones de negocio en constructor (ID > 0, nombre no vacío, precio >= 0).
- **`ProductoInvalidoException`:** Excepción comprobada (`checked exception`) personalizada para aislar fallos de validación del dominio.
- **`GestorProductosRA1`:** Controlador de consola interactivo y pasarela de operaciones I/O.
- **APIs de E/S combinadas:**
  - `java.io.File`, `java.io.FileReader`, `java.io.FileWriter`: Acceso y flujos hacia disco.
  - `java.io.BufferedReader`, `java.io.BufferedWriter`: Reducción de llamadas al sistema mediante buffers en memoria RAM.
  - `java.nio.file.Path`, `java.nio.file.Paths`, `java.nio.file.Files`: Creación de directorios multinivel (`createDirectories`), copias de seguridad atómicas (`Files.copy`) y recorrido funcional de directorios (`Files.list`).
  - Gestión rigurosa con `try-with-resources` para el cierre garantizado de flujos y captura ordenada de excepciones (`FileNotFoundException` previo a `IOException`).

---

## 🛠️ Instrucciones de Compilación y Ejecución

### Requisitos
- JDK 21 o superior instalado y configurado en el `JAVA_HOME`.
- Apache Maven o compilador estándar de Java (`javac`).

### Ejecución con Maven (NetBeans / Consola)
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la clase principal
mvn exec:java -Dexec.mainClass="edu.ficheros.gestorproductos.GestorProductosRA1"
```

### Ejecución Manual desde Terminal
```bash
# Crear directorio de binarios y compilar
javac -d bin src/main/java/edu/ficheros/gestorproductos/*.java

# Ejecutar la aplicación
java -cp bin edu.ficheros.gestorproductos.GestorProductosRA1
```

---

## 🧪 Tabla de Pruebas Realizadas

| N.º | Caso de Prueba | Acción / Entrada | Resultado Esperado | Resultado Obtenido | Estado |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **01** | **Arranque inicial en frío** | Ejecutar aplicación sin directorios `datos/` ni `backups/`. | Crear carpetas y generar `productos.csv` únicamente con la cabecera `ID;NOMBRE;PRECIO`. | Directorios generados automáticamente; archivo creado con cabecera intacta. | ✅ Superado |
| **02** | **Listar catálogo vacío** | Opción `1` con fichero sin registros de datos. | Aviso en consola indicando que no hay productos válidos registrados. | Muestra: `Total de productos válidos registrados: 0`. | ✅ Superado |
| **03** | **Alta de producto válida** | Opción `2`:<br>- ID: `1`<br>- Nombre: `Teclado`<br>- Precio: `29.99` | Inserción en modo *append* sin alterar la cabecera. | Registro añadido al final: `1;Teclado;29.99`. | ✅ Superado |
| **04** | **Alta con ID duplicado** | Opción `2`:<br>- ID: `1`<br>- Nombre: `Ratón`<br>- Precio: `15.00` | Detección de duplicado, abortar inserción e informar sin modificar el fichero. | Mensaje de restricción: *"Ya existe un producto con el ID 1"*. Inserción cancelada. | ✅ Superado |
| **05** | **Validación de negocio (Excepción)** | Opción `2`:<br>- ID: `-3`<br>- Nombre: ` ` (vacío)<br>- Precio: `-10.5` | Disparo controlado de `ProductoInvalidoException` capturada en el menú. | Error descriptivo mostrado por consola; el programa continúa en ejecución sin cerrarse. | ✅ Superado |
| **06** | **Búsqueda por ID existente** | Opción `3`: ID `1`. | Localización secuencial y retorno del objeto `Producto`. | Muestra datos formateados: `ID: 1 \| Nombre: Teclado \| Precio: 29.99 €`. | ✅ Superado |
| **07** | **Búsqueda por ID no existente** | Opción `3`: ID `99`. | Retorno de `null` y mensaje informativo al usuario. | Muestra: *"No se encontró ningún producto con ID: 99"*. | ✅ Superado |
| **08** | **Modificación de producto** | Opción `4` sobre ID `1`:<br>- Nuevo nombre: `Teclado Mecánico`<br>- Nuevo precio: `45.50` | Reescribir el fichero completo conservando cabecera y actualizando la línea. | Línea actualizada en disco a `1;Teclado Mecánico;45.5`. Resto de líneas intactas. | ✅ Superado |
| **09** | **Eliminación de producto** | Opción `5` sobre ID `1`. | Filtrar la línea coincidente y reescribir el fichero. | El registro desaparece de `productos.csv`. La cabecera permanece intacta. | ✅ Superado |
| **10** | **Conversión CSV a TSV** | Opción `6` con varios productos existentes. | Generación de `datos/productos.tsv` sustituyendo `;` por `\t`. | Archivo generado correctamente con separadores por tabulador verificables. | ✅ Superado |
| **11** | **Conversión TSV a CSV** | Opción `7` leyendo `datos/productos.tsv`. | Reconstrucción íntegra de `datos/productos.csv`. | Archivo CSV actualizado manteniendo los campos de origen. | ✅ Superado |
| **12** | **Exportación a formato JSON** | Opción `8`. | Creación de `datos/productos.json` con estructura de array de objetos. | Fichero JSON generado con formato clave-valor legible. | ✅ Superado |
| **13** | **Copia de seguridad (Backup)** | Opción `9`. | Copia íntegra hacia `backups/productos_YYYYMMDD_HHMMSS.csv` vía `Files.copy`. | Copia generada con marca de tiempo precisa en la carpeta designada. | ✅ Superado |
| **14** | **Listado de copias de seguridad** | Opción `10`. | Listado no recursivo de ficheros en `backups/` indicando su tamaño en bytes. | Muestra el listado de copias de seguridad procesadas mediante `Files.list()`. | ✅ Superado |
| **15** | **Robustez frente a entradas inválidas** | Introducir texto alfabético (`"hola"`) en el menú numérico. | Captura de `NumberFormatException` sin caída del proceso principal. | Notificación de error y retorno al menú interactivo. | ✅ Superado |

---

## 👤 Autor
- **Adrián M. G.** – 2.º DAM (Desarrollo de Aplicaciones Multiplataforma)
