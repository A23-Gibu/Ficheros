package edu.ad.ficheros.ejercicio9ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Aplicación de consola para la gestión de productos en formatos CSV, TSV y JSON.
 * Integra la creación portable de carpetas y ficheros (java.nio.file), lectura y
 * escritura bufferizada secuencial (java.io), patrón leer-modificar-reescribir,
 * copias atómicas con timestamp, listado de directorios y control riguroso de excepciones.
 * 
 * @author adria
 */
public class Ejercicio9Ficheros {

    // =========================================================================
    // CONSTANTES DE RUTAS Y DELIMITADORES
    // =========================================================================
    private static final String DIR_DATOS = "datos";
    private static final String DIR_BACKUPS = "backups";
    
    // File.separator asegura compatibilidad multiplataforma (\ en Windows, / en Linux/Mac)
    private static final String RUTA_CSV = DIR_DATOS + File.separator + "productos.csv";
    private static final String RUTA_TSV = DIR_DATOS + File.separator + "productos.tsv";
    private static final String RUTA_JSON = DIR_DATOS + File.separator + "productos.json";
    
    private static final String CABECERA_CSV = "ID;NOMBRE;PRECIO";
    private static final String CABECERA_TSV = "ID\tNOMBRE\tPRECIO";

    // Instancia persistente del fichero base y del Scanner
    private static final File archivoCsv = new File(RUTA_CSV);
    private static final Scanner teclado = new Scanner(System.in);

    // =========================================================================
    // PUNTO DE ENTRADA (MÉTODO MAIN)
    // =========================================================================
    public static void main(String[] args) {
        // Aseguramos existencia de carpetas y fichero con cabecera al arrancar
        prepararEntorno();

        int opcion = -1;
        do {
            mostrarMenu();
            try {
                // Leemos con nextLine() y parseamos a int para evitar el problema
                // del salto de línea colgado que deja nextInt() en el buffer
                String input = teclado.nextLine();
                opcion = Integer.parseInt(input.trim());

                switch (opcion) {
                    case 1 -> listarProductos();
                    case 2 -> menuAnadirProducto();
                    case 3 -> menuBuscarProducto();
                    case 4 -> menuModificarProducto();
                    case 5 -> menuEliminarProducto();
                    case 6 -> convertirCsvATsv();
                    case 7 -> convertirTsvACsv();
                    case 8 -> exportarAJson();
                    case 9 -> importarDesdeJson();
                    case 10 -> crearBackup();
                    case 11 -> listarBackups();
                    case 0 -> System.out.println("\nSaliendo del gestor de productos. ¡Hasta pronto!");
                    default -> System.err.println("Opción fuera de rango. Seleccione una opción del 0 al 11.");
                }
            } catch (NumberFormatException e) {
                // El main no termina abruptamente si el usuario introduce texto en vez de números
                System.err.println("Entrada inválida: Debe introducir un número correspondiente a una opción.");
            } catch (Exception e) {
                // Captura general de contingencia para mantener el menú siempre vivo
                System.err.println("Se produjo un error no controlado: " + e.getMessage());
            }
            System.out.println();
        } while (opcion != 0);

        teclado.close();
    }

    private static void mostrarMenu() {
        System.out.println("==================================================");
        System.out.println("       GESTOR DE PRODUCTOS - EVALUACIÓN RA1       ");
        System.out.println("==================================================");
        System.out.println("1.  Listar todos los productos");
        System.out.println("2.  Añadir un nuevo producto");
        System.out.println("3.  Buscar producto por ID");
        System.out.println("4.  Modificar producto existente");
        System.out.println("5.  Eliminar producto existente");
        System.out.println("6.  Convertir CSV a TSV (tabuladores)");
        System.out.println("7.  Convertir TSV a CSV (puntos y coma)");
        System.out.println("8.  Exportar catálogo a formato JSON");
        System.out.println("9.  Importar productos desde formato JSON");
        System.out.println("10. Generar Copia de Seguridad (Backup con timestamp)");
        System.out.println("11. Listar copias de seguridad existentes");
        System.out.println("0.  Salir");
        System.out.print("Seleccione una opción: ");
    }

    // =========================================================================
    // 1. PREPARACIÓN DEL ENTORNO (DIRECTORIOS Y FICHERO INICIAL)
    // =========================================================================
    /**
     * Comprueba y crea los directorios datos/ y backups/ usando Files.createDirectories.
     * Crea datos/productos.csv con su cabecera si y solo si no existía previamente.
     */
    private static void prepararEntorno() {
        try {
            Path dirDatos = Paths.get(DIR_DATOS);
            Path dirBackups = Paths.get(DIR_BACKUPS);

            // Files.createDirectories crea toda la jerarquía de directorios faltante
            // y no lanza error si ya existían previamente
            if (!Files.exists(dirDatos)) {
                Files.createDirectories(dirDatos);
            }
            if (!Files.exists(dirBackups)) {
                Files.createDirectories(dirBackups);
            }

            // Comprobación crítica: si el fichero ya existe, no creamos un FileWriter sin append
            // porque truncaría los datos a cero bytes
            if (!archivoCsv.exists()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv))) {
                    bw.write(CABECERA_CSV);
                    bw.newLine(); // Salto de línea adaptado al sistema operativo
                }
                System.out.println("[INFO] Sistema inicializado: creado " + RUTA_CSV + " con cabecera.");
            }
        } catch (IOException e) {
            System.err.println("Error crítico al inicializar el sistema de ficheros: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. LECTURA Y LISTADO DE PRODUCTOS (READ)
    // =========================================================================
    /**
     * Lee secuencialmente el archivo CSV con BufferedReader, omite la cabecera,
     * procesa tokens con split(";", -1), valida tipos e imprime en consola.
     */
    public static void listarProductos() {
        if (!archivoCsv.exists()) {
            System.err.println("Error: El fichero " + RUTA_CSV + " no existe.");
            return;
        }

        // try-with-resources garantiza el cierre del flujo ante cualquier error
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            // Saltamos la línea de cabecera
            String linea = br.readLine();
            if (linea == null) {
                System.out.println("El fichero se encuentra completamente vacío.");
                return;
            }

            int contador = 0;
            System.out.println("\n----------------- LISTADO DE PRODUCTOS -----------------");
            // readLine() devuelve null cuando llega al EOF
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                // split con -1 evita que Java descarte campos vacíos al final de la línea
                String[] campos = linea.split(";", -1);
                if (campos.length < 3) {
                    System.err.println("[ADVERTENCIA] Registro omitido por columnas insuficientes: " + linea);
                    continue;
                }

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String nombre = campos[1].trim();
                    double precio = Double.parseDouble(campos[2].trim());

                    Producto p = new Producto(id, nombre, precio);
                    System.out.println(p); // Invoca a p.toString() automáticamente
                    contador++;
                } catch (NumberFormatException ex) {
                    System.err.println("[ADVERTENCIA] Error numérico en línea [" + linea + "]: " + ex.getMessage());
                } catch (ProductoInvalidoException ex) {
                    System.err.println("[ADVERTENCIA] Validación de datos fallida en [" + linea + "]: " + ex.getMessage());
                }
            }
            System.out.println("--------------------------------------------------------");
            System.out.println("Total de productos válidos registrados: " + contador);
        } catch (FileNotFoundException e) {
            // FileNotFoundException debe ir ANTES de IOException al ser su subclase
            System.err.println("Fichero no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida durante la lectura: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. BÚSQUEDA DE REGISTROS (MÉTODO PURO SIN IMPRESIÓN INTERNA)
    // =========================================================================
    /**
     * Localiza un producto por su ID leyendo secuencialmente el archivo.
     * 
     * @param idBuscado Clave primaria a localizar.
     * @return Instancia del Producto coincidente, o null si no se localiza.
     */
    public static Producto buscarProductoPorId(int idBuscado) {
        if (!archivoCsv.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            String linea = br.readLine(); // Descartar cabecera
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(";", -1);
                if (campos.length >= 3) {
                    try {
                        int id = Integer.parseInt(campos[0].trim());
                        if (id == idBuscado) {
                            String nombre = campos[1].trim();
                            double precio = Double.parseDouble(campos[2].trim());
                            return new Producto(id, nombre, precio);
                        }
                    } catch (NumberFormatException | ProductoInvalidoException ignored) {
                        // Ignoramos filas con errores de formato para continuar la búsqueda
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al acceder al fichero durante la búsqueda: " + e.getMessage());
        }
        return null;
    }

    private static void menuBuscarProducto() {
        try {
            System.out.print("Introduzca el ID del producto a buscar: ");
            int id = Integer.parseInt(teclado.nextLine().trim());
            Producto encontrado = buscarProductoPorId(id);
            if (encontrado != null) {
                System.out.println("\n[RESULTADO DE LA BÚSQUEDA]");
                System.out.println(encontrado);
            } else {
                System.out.println("No se encontró ningún producto con ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.err.println("El identificador introducido no es un número entero válido.");
        }
    }

    // =========================================================================
    // 4. ALTA DE PRODUCTOS (CREATE - MODO APPEND)
    // =========================================================================
    /**
     * Solicita datos por teclado, valida unicidad de clave y escribe la nueva
     * línea al final del archivo activando el modo append (true).
     */
    private static void menuAnadirProducto() {
        try {
            System.out.print("Introduce el ID del producto: ");
            int id = Integer.parseInt(teclado.nextLine().trim());

            // Validación de unicidad de clave primaria
            if (buscarProductoPorId(id) != null) {
                System.err.println("Restricción violada: Ya existe un producto con el ID " + id + ". Inserción cancelada.");
                return;
            }

            System.out.print("Introduce el nombre del producto: ");
            String nombre = teclado.nextLine();

            // Validación de delimitadores para preservar la integridad del formato tabular
            if (nombre.contains(";")) {
                System.err.println("Error: El nombre no puede contener el carácter ';' reservado como delimitador.");
                return;
            }

            System.out.print("Introduce el precio del producto: ");
            // Soportamos coma decimal reemplazándola por punto
            double precio = Double.parseDouble(teclado.nextLine().trim().replace(',', '.'));

            // El constructor de Producto validará id > 0, precio >= 0 y nombre no vacío
            Producto nuevo = new Producto(id, nombre, precio);

            // FileWriter(..., true) abre el flujo en modo append sin truncar el archivo existente
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, true))) {
                bw.write(nuevo.toDelimitedString(";"));
                bw.newLine(); // Indispensable para separar registros contiguos
                System.out.println("Producto añadido con éxito en " + RUTA_CSV);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error de formato: El ID y el precio deben ser valores numéricos válidos.");
        } catch (ProductoInvalidoException e) {
            System.err.println("Validación fallida: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al escribir en disco: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. MODIFICACIÓN DE PRODUCTOS (UPDATE - LEER, MODIFICAR Y REESCRIBIR)
    // =========================================================================
    /**
     * En ficheros secuenciales no es posible sobreescribir líneas intermedias directamente.
     * Patrón aplicado:
     * 1. Cargar todas las líneas en memoria (List<String>).
     * 2. Localizar y reemplazar la línea que coincide con el ID.
     * 3. Reescribir el fichero completo sin append (append = false).
     */
    private static void menuModificarProducto() {
        try {
            System.out.print("Introduce el ID del producto a modificar: ");
            int idObjetivo = Integer.parseInt(teclado.nextLine().trim());

            Producto existente = buscarProductoPorId(idObjetivo);
            if (existente == null) {
                System.err.println("Operación abortada: No existe ningún producto con el ID " + idObjetivo);
                return;
            }

            System.out.println("Producto actual: " + existente);
            System.out.print("Introduce el nuevo nombre: ");
            String nuevoNombre = teclado.nextLine();
            if (nuevoNombre.contains(";")) {
                System.err.println("Error: El nombre no puede contener ';'.");
                return;
            }

            System.out.print("Introduce el nuevo precio: ");
            double nuevoPrecio = Double.parseDouble(teclado.nextLine().trim().replace(',', '.'));

            Producto actualizado = new Producto(idObjetivo, nuevoNombre, nuevoPrecio);

            // Fase 1: Leer todas las líneas existentes
            List<String> lineas = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    lineas.add(linea);
                }
            }

            // Fase 2: Localizar y modificar en memoria (empezando en 1 para saltar cabecera)
            boolean cambiado = false;
            for (int i = 1; i < lineas.size(); i++) {
                String[] campos = lineas.get(i).split(";", -1);
                if (campos.length > 0 && Integer.parseInt(campos[0].trim()) == idObjetivo) {
                    lineas.set(i, actualizado.toDelimitedString(";"));
                    cambiado = true;
                    break;
                }
            }

            // Fase 3: Reescribir el fichero completo (modo sobrescritura)
            if (cambiado) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, false))) {
                    for (String l : lineas) {
                        bw.write(l);
                        bw.newLine();
                    }
                }
                System.out.println("Producto con ID " + idObjetivo + " modificado y guardado con éxito.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error numérico: ID o precio introducido no válido.");
        } catch (ProductoInvalidoException e) {
            System.err.println("Validación fallida: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al reescribir el archivo: " + e.getMessage());
        }
    }

    // =========================================================================
    // 6. ELIMINACIÓN DE PRODUCTOS (DELETE - LEER, FILTRAR Y REESCRIBIR)
    // =========================================================================
    /**
     * Similar a la modificación:
     * 1. Cargar las líneas.
     * 2. Filtrar y omitir la línea del ID solicitado.
     * 3. Reescribir el archivo sin la línea eliminada.
     */
    private static void menuEliminarProducto() {
        try {
            System.out.print("Introduce el ID del producto a eliminar: ");
            int idEliminar = Integer.parseInt(teclado.nextLine().trim());

            if (buscarProductoPorId(idEliminar) == null) {
                System.err.println("Operación abortada: No existe ningún producto con el ID " + idEliminar);
                return;
            }

            List<String> lineasFiltradas = new ArrayList<>();
            boolean eliminado = false;

            try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
                String linea = br.readLine();
                if (linea != null) {
                    lineasFiltradas.add(linea); // Preservamos la cabecera siempre
                }
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] campos = linea.split(";", -1);
                    if (campos.length > 0 && Integer.parseInt(campos[0].trim()) == idEliminar) {
                        eliminado = true; // Coincidencia detectada: se omite su inserción
                    } else {
                        lineasFiltradas.add(linea);
                    }
                }
            }

            if (eliminado) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, false))) {
                    for (String l : lineasFiltradas) {
                        bw.write(l);
                        bw.newLine();
                    }
                }
                System.out.println("Producto con ID " + idEliminar + " eliminado con éxito.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error numérico: Introduzca un identificador entero.");
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al reescribir el fichero: " + e.getMessage());
        }
    }

    // =========================================================================
    // 7. CONVERSIÓN DE FORMATOS (CSV <-> TSV)
    // =========================================================================
    /**
     * Transforma el archivo productos.csv delimitado por ';' en productos.tsv delimitado por '\t'.
     */
    public static void convertirCsvATsv() {
        File origen = archivoCsv;
        File destino = new File(RUTA_TSV);

        if (!origen.exists()) {
            System.err.println("Error: El fichero de origen " + RUTA_CSV + " no existe.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino, false))) {

            String linea;
            int registros = 0;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";", -1);
                // Unimos las columnas utilizando el carácter tabulador '\t'
                bw.write(String.join("\t", campos));
                bw.newLine();
                registros++;
            }
            System.out.println("Conversión CSV -> TSV completada (" + registros + " líneas volcadas en " + RUTA_TSV + ")");
        } catch (IOException e) {
            System.err.println("Error durante la conversión a TSV: " + e.getMessage());
        }
    }

    /**
     * Transforma el archivo productos.tsv delimitado por '\t' en productos.csv delimitado por ';'.
     */
    public static void convertirTsvACsv() {
        File origen = new File(RUTA_TSV);
        File destino = archivoCsv;

        if (!origen.exists()) {
            System.err.println("Error: El fichero de origen " + RUTA_TSV + " no existe. Genérelo primero.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino, false))) {

            String linea;
            int registros = 0;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split("\t", -1);
                // Reconstruimos el registro con el delimitador ';'
                bw.write(String.join(";", campos));
                bw.newLine();
                registros++;
            }
            System.out.println("Conversión TSV -> CSV completada (" + registros + " líneas volcadas en " + RUTA_CSV + ")");
        } catch (IOException e) {
            System.err.println("Error durante la conversión a CSV: " + e.getMessage());
        }
    }

    // =========================================================================
    // 8. EXPORTACIÓN E IMPORTACIÓN JSON SIMPLE (PARSEO SIN DEPENDENCIAS)
    // =========================================================================
    /**
     * Exporta el catálogo completo a un archivo JSON estructurado en un array.
     */
    public static void exportarAJson() {
        if (!archivoCsv.exists()) {
            System.err.println("Error: No existe el fichero origen " + RUTA_CSV);
            return;
        }

        List<Producto> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            String linea = br.readLine(); // Descartar cabecera
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] c = linea.split(";", -1);
                if (c.length >= 3) {
                    try {
                        lista.add(new Producto(Integer.parseInt(c[0].trim()), c[1].trim(), Double.parseDouble(c[2].trim())));
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer datos para exportación JSON: " + e.getMessage());
            return;
        }

        File fJson = new File(RUTA_JSON);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fJson, false))) {
            bw.write("[\n");
            for (int i = 0; i < lista.size(); i++) {
                bw.write(lista.get(i).toJson());
                // El último elemento del array JSON no debe llevar coma al final
                if (i < lista.size() - 1) {
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.write("]");
            bw.newLine();
            System.out.println("Exportación JSON completada con éxito en: " + RUTA_JSON);
        } catch (IOException e) {
            System.err.println("Error al escribir el fichero JSON: " + e.getMessage());
        }
    }

    /**
     * Importa registros desde datos/productos.json hacia el archivo CSV principal.
     * Parsea línea a línea procesando las claves "id", "nombre" y "precio" de forma segura.
     */
    public static void importarDesdeJson() {
        File fJson = new File(RUTA_JSON);
        if (!fJson.exists()) {
            System.err.println("Error: El fichero JSON no existe. Genérelo primero mediante la opción 8.");
            return;
        }

        int importados = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fJson))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                // Filtramos las líneas que representen objetos JSON con ID
                if (linea.startsWith("{") && linea.contains("\"id\"")) {
                    try {
                        // Eliminamos llaves de apertura/cierre y posibles comas finales
                        String limpio = linea.replaceAll("^\\{\\s*|\\s*\\},?$", "");
                        
                        // Partimos por las comas que delimitan atributos (id, nombre, precio)
                        String[] pares = limpio.split(",");
                        int id = 0;
                        String nombre = "";
                        double precio = 0.0;

                        for (String par : pares) {
                            String[] claveValor = par.split(":", 2);
                            if (claveValor.length == 2) {
                                String clave = claveValor[0].trim().replace("\"", "");
                                String valor = claveValor[1].trim().replace("\"", "");

                                switch (clave) {
                                    case "id" -> id = Integer.parseInt(valor);
                                    case "nombre" -> nombre = valor;
                                    case "precio" -> precio = Double.parseDouble(valor.replace(',', '.'));
                                }
                            }
                        }

                        // Evitamos importar registros con IDs ya existentes o inválidos
                        if (id > 0 && buscarProductoPorId(id) == null) {
                            Producto p = new Producto(id, nombre, precio);
                            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, true))) {
                                bw.write(p.toDelimitedString(";"));
                                bw.newLine();
                                importados++;
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Registro JSON omitido por inconsistencias: " + linea);
                    }
                }
            }
            System.out.println("Importación completada. Nuevos productos añadidos al CSV: " + importados);
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al importar JSON: " + e.getMessage());
        }
    }

    // =========================================================================
    // 9. COPIAS DE SEGURIDAD (NIO.2 - Files.copy CON TIMESTAMP)
    // =========================================================================
    /**
     * Genera una copia del fichero CSV en backups/productos_YYYYMMDD_HHMMSS.csv.
     */
    public static void crearBackup() {
        if (!archivoCsv.exists()) {
            System.err.println("No se puede generar backup: El archivo de datos no existe aún.");
            return;
        }

        // Generamos marca de tiempo única para el nombre del backup
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path origen = archivoCsv.toPath(); // Puente entre java.io.File y java.nio.file.Path
        Path destino = Paths.get(DIR_BACKUPS, "productos_" + timestamp + ".csv");

        try {
            // Files.copy realiza la copia atómica a nivel de sistema de archivos
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copia de seguridad generada con éxito: " + destino.getFileName());
        } catch (IOException e) {
            System.err.println("Error al generar la copia de seguridad: " + e.getMessage());
        }
    }

    // =========================================================================
    // 10. LISTADO DE DIRECTORIOS CON Streams (NIO.2 - Files.list)
    // =========================================================================
    /**
     * Lista los ficheros en la carpeta backups/ junto con su tamaño en bytes.
     */
    public static void listarBackups() {
        Path rutaBackups = Paths.get(DIR_BACKUPS);
        if (!Files.exists(rutaBackups)) {
            System.out.println("El directorio de backups no existe.");
            return;
        }

        System.out.println("\n--- COPIAS DE SEGURIDAD DISPONIBLES (backups/) ---");
        // Files.list devuelve un Stream<Path> perezoso que debe cerrarse con try-with-resources
        try (Stream<Path> stream = Files.list(rutaBackups)) {
            long total = stream.filter(Files::isRegularFile).peek(path -> {
                try {
                    // Files.size() consulta los metadatos de longitud en disco
                    System.out.printf("- %-35s (%d bytes)\n", path.getFileName(), Files.size(path));
                } catch (IOException ignored) {}
            }).count();

            if (total == 0) {
                System.out.println("No hay copias de seguridad creadas todavía.");
            }
            System.out.println("--------------------------------------------------");
        } catch (IOException e) {
            System.err.println("Error al listar las copias de seguridad: " + e.getMessage());
        }
    }
}