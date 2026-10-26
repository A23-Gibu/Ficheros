# GestorProductosRA1 - Persistencia y Gestión de Catálogo

Aplicación de consola en Java desarrollada para la unidad de trabajo **UT1 (Manejo de Ficheros en Java)** del módulo Acceso a Datos / Programación (2.º DAM).

---

## 📋 Descripción de Formatos y Decisión Técnica sobre Delimitadores

### 1. Formatos Empleados
- **CSV (`productos.csv`):** Formato estructurado delimitado por punto y coma (`;`), estándar comúnmente empleado en entornos europeos para evitar conflictos con la coma decimal.
- **TSV (`productos.tsv`):** Formato estructurado delimitado por el tabulador (`\t`).
- **JSON (`productos.json`):** Formato de objetos clave-valor estándar estructurado en array.

### 2. Tratamiento de Campos con Delimitador Incrustado
Si el nombre de un producto contiene el delimitador activo (por ejemplo, escribir un punto y coma `;` en un CSV, o pulsar Tabulación `\t` en un TSV), el método `split(";", -1)` partiría la línea en 4 campos en vez de 3.
- **Decisión de diseño:** La aplicación valida en el alta y modificación que la cadena de texto no contenga el delimitador que corrompería las columnas, bloqueando la inserción antes de escribir en disco. En entornos de producción mayores se requeriría encomillado según RFC 4180 (`"Nombre;Compuesto"`) o librerías como OpenCSV.

---

## 🛠️ Instrucciones de Compilación y Ejecución

### Requisitos
- JDK 21 instalado (compatible con JDK 17+).
- Maven o compilador directo `javac`.

### Compilación y Ejecución con Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="edu.ficheros.gestorproductos.GestorProductosRA1"
