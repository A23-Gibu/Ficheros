/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

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
 * Aplicación de consola para la gestión integral de productos en formatos CSV/TSV/JSON.
 * Demuestra el uso combinado de las APIs java.io y java.nio.file, flujos con buffer,
 * persistencia secuencial y manejo estricto de excepciones.
 * 
 * @author adria
 */
public class Ejercicio9Ficheros {

    private static final String DIR_DATOS = "datos";
    private static final String DIR_BACKUPS = "backups";
    private static final String RUTA_CSV = DIR_DATOS + File.separator + "productos.csv";
    private static final String RUTA_TSV = DIR_DATOS + File.separator + "productos.tsv";
    private static final String RUTA_JSON = DIR_DATOS + File.separator + "productos.json";
    private static final String CABECERA_CSV = "ID;NOMBRE;PRECIO";
    private static final String CABECERA_TSV = "ID\tNOMBRE\tPRECIO";

    private static final File archivoCsv = new File(RUTA_CSV);
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        prepararEntorno();

        int opcion = -1;
        do {
            mostrarMenu();
            try {
                String input = teclado.nextLine();
                opcion = Integer.parseInt(input);

                switch (opcion) {
                    case 1 -> listarProductos();
                    case 2 -> menuAnadirProducto();
                    case 3 -> menuBuscarProducto();
                    case 4 -> menuModificarProducto();
                    case 5 -> menuEliminarProducto();
                    case 6 -> convertirCsvATsv();
                    case 7 -> convertirTsvACsv();
                    case 8 -> exportarAJson();
                    case 9 -> crearBackup();
                    case 10 -> listarBackups();
                    case 0 -> System.out.println("\nCerrando aplicación GestorProductosRA1. ¡Hasta pronto!");
                    default -> System.err.println("Opción fuera de rango. Seleccione una opción del 0 al 10.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Entrada no válida: introduzca el número correspondiente a la opción.");
            } catch (Exception e) {
                System.err.println("Se produjo un error imprevisto: " + e.getMessage());
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
        System.out.println("9.  Generar Copia de Seguridad (Backup con timestamp)");
        System.out.println("10. Listar copias de seguridad existentes");
        System.out.println("0.  Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Valida y prepara la jerarquía de directorios y el fichero principal con su cabecera.
     */
    private static void prepararEntorno() {
        try {
            Path dirDatos = Paths.get(DIR_DATOS);
            Path dirBackups = Paths.get(DIR_BACKUPS);

            if (!Files.exists(dirDatos)) {
                Files.createDirectories(dirDatos);
            }
            if (!Files.exists(dirBackups)) {
                Files.createDirectories(dirBackups);
            }

            if (!archivoCsv.exists()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv))) {
                    bw.write(CABECERA_CSV);
                    bw.newLine();
                }
                System.out.println("[INFO] Directorios creados e inicializado " + RUTA_CSV + " con cabecera.");
            }
        } catch (IOException e) {
            System.err.println("Error crítico al inicializar el sistema de ficheros: " + e.getMessage());
        }
    }

    /**
     * Lee y parsea los registros del archivo CSV ignorando la cabecera.
     */
    public static void listarProductos() {
        if (!archivoCsv.exists()) {
            System.err.println("Error: El fichero " + RUTA_CSV + " no existe.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            String linea = br.readLine(); // Saltamos cabecera
            if (linea == null) {
                System.out.println("El fichero se encuentra completamente vacío.");
                return;
            }

            int contador = 0;
            System.out.println("\n----------------- LISTADO DE PRODUCTOS -----------------");
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(";", -1);
                if (campos.length < 3) {
                    System.err.println("[ADVERTENCIA] Registro malformado omitido: " + linea);
                    continue;
                }
                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String nombre = campos[1].trim();
                    double precio = Double.parseDouble(campos[2].trim());
                    Producto p = new Producto(id, nombre, precio);
                    System.out.println(p);
                    contador++;
                } catch (NumberFormatException | ProductoInvalidoException ex) {
                    System.err.println("[ADVERTENCIA] Error de parseo en línea [" + linea + "]: " + ex.getMessage());
                }
            }
            System.out.println("--------------------------------------------------------");
            System.out.println("Total de productos válidos registrados: " + contador);
        } catch (FileNotFoundException e) {
            System.err.println("Fichero no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de lectura: " + e.getMessage());
        }
    }

    /**
     * Localiza un producto por su clave primaria sin emitir salida por consola.
     */
    public static Producto buscarProductoPorId(int idBuscado) {
        if (!archivoCsv.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            String linea = br.readLine(); // Saltar cabecera
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
                    } catch (NumberFormatException | ProductoInvalidoException ignored) {}
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
                System.out.println("\n[RESULTADO]");
                System.out.println(encontrado);
            } else {
                System.out.println("No se encontró ningún producto con ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.err.println("El identificador introducido no es un número válido.");
        }
    }

    private static void menuAnadirProducto() {
        try {
            System.out.print("Introduce el ID del producto: ");
            int id = Integer.parseInt(teclado.nextLine().trim());

            if (buscarProductoPorId(id) != null) {
                System.err.println("Error de restricción: Ya existe un producto con el ID " + id + ". Inserción cancelada.");
                return;
            }

            System.out.print("Introduce el nombre del producto: ");
            String nombre = teclado.nextLine();

            // Advertencia documental ante delimitadores
            if (nombre.contains(";")) {
                System.err.println("Error: El nombre no puede contener el carácter ';' reservado como delimitador.");
                return;
            }

            System.out.print("Introduce el precio del producto: ");
            double precio = Double.parseDouble(teclado.nextLine().trim().replace(',', '.'));

            Producto nuevo = new Producto(id, nombre, precio);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, true))) {
                bw.write(nuevo.toDelimitedString(";"));
                bw.newLine();
                System.out.println("Producto añadido con éxito en " + RUTA_CSV);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error de formato: El ID y el precio deben ser valores numéricos válidos.");
        } catch (ProductoInvalidoException e) {
            System.err.println("Validación fallida: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de escritura en el disco: " + e.getMessage());
        }
    }

    /**
     * Aplica el patrón Leer-Modificar-Reescribir para actualizar un registro por ID.
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

            // Fase 1: Leer todas las líneas
            List<String> lineas = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    lineas.add(linea);
                }
            }

            // Fase 2: Modificar en memoria
            boolean cambiado = false;
            for (int i = 1; i < lineas.size(); i++) {
                String[] campos = lineas.get(i).split(";", -1);
                if (campos.length > 0 && Integer.parseInt(campos[0].trim()) == idObjetivo) {
                    lineas.set(i, actualizado.toDelimitedString(";"));
                    cambiado = true;
                    break;
                }
            }

            // Fase 3: Reescribir sin append
            if (cambiado) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCsv, false))) {
                    for (String l : lineas) {
                        bw.write(l);
                        bw.newLine();
                    }
                }
                System.out.println("Producto modificado y archivo reescrito con éxito.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error numérico: ID o precio introducido no válido.");
        } catch (ProductoInvalidoException e) {
            System.err.println("Validación fallida: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al reescribir el archivo: " + e.getMessage());
        }
    }

    /**
     * Aplica el patrón Leer-Filtrar-Reescribir para suprimir un registro por ID.
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
                    lineasFiltradas.add(linea); // Preservar cabecera
                }
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] campos = linea.split(";", -1);
                    if (campos.length > 0 && Integer.parseInt(campos[0].trim()) == idEliminar) {
                        eliminado = true; // Excluimos esta línea
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
            System.err.println("Error de E/S al actualizar el fichero: " + e.getMessage());
        }
    }

    /**
     * Transforma el archivo CSV delimitado por ';' en formato TSV con '\t'.
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
                bw.write(String.join("\t", campos));
                bw.newLine();
                registros++;
            }
            System.out.println("Conversión CSV -> TSV completada. (" + registros + " líneas volcadas en " + RUTA_TSV + ")");
        } catch (IOException e) {
            System.err.println("Error durante la conversión a TSV: " + e.getMessage());
        }
    }

    /**
     * Transforma el archivo TSV delimitado por '\t' en formato CSV con ';'.
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
                bw.write(String.join(";", campos));
                bw.newLine();
                registros++;
            }
            System.out.println("Conversión TSV -> CSV completada. (" + registros + " líneas volcadas en " + RUTA_CSV + ")");
        } catch (IOException e) {
            System.err.println("Error durante la conversión a CSV: " + e.getMessage());
        }
    }

    /**
     * Exporta los datos a un archivo JSON simple estructurado.
     */
    public static void exportarAJson() {
        if (!archivoCsv.exists()) {
            System.err.println("Error: No existe " + RUTA_CSV);
            return;
        }

        List<Producto> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCsv))) {
            String linea = br.readLine(); // Saltar cabecera
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
                if (i < lista.size() - 1) {
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.write("]");
            bw.newLine();
            System.out.println("Exportación completada con éxito en: " + RUTA_JSON);
        } catch (IOException e) {
            System.err.println("Error al escribir el fichero JSON: " + e.getMessage());
        }
    }

    /**
     * Genera una copia atómica del CSV en backups/ con patrón de fecha y hora.
     */
    public static void crearBackup() {
        if (!archivoCsv.exists()) {
            System.err.println("No se puede crear backup: El archivo de datos no existe aún.");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path origen = archivoCsv.toPath();
        Path destino = Paths.get(DIR_BACKUPS, "productos_" + timestamp + ".csv");

        try {
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copia de seguridad generada con éxito: " + destino.getFileName());
        } catch (IOException e) {
            System.err.println("Error al generar la copia de seguridad: " + e.getMessage());
        }
    }

    /**
     * Lista los backups existentes utilizando Files.list() con try-with-resources.
     */
    public static void listarBackups() {
        Path rutaBackups = Paths.get(DIR_BACKUPS);
        if (!Files.exists(rutaBackups)) {
            System.out.println("El directorio de backups no existe.");
            return;
        }

        System.out.println("\n--- COPIAS DE SEGURIDAD DISPONIBLES (backups/) ---");
        try (Stream<Path> stream = Files.list(rutaBackups)) {
            long total = stream.filter(Files::isRegularFile).peek(path -> {
                try {
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