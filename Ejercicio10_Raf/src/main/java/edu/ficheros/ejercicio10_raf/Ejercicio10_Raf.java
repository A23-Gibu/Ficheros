/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ficheros.ejercicio10_raf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Aplicación de consola con menú interactivo para gestionar los apuntes
 * bancarios de 'cuentas.dat' y ejecutar paso a paso los requisitos del extracto
 * bancario.
 *
 * @author adria
 */
public class Ejercicio10_Raf {

    private static final GestorCuentasRAF gestor = new GestorCuentasRAF();
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;
        do {
            mostrarMenu();
            try {
                String entrada = teclado.nextLine();
                opcion = Integer.parseInt(entrada.trim());

                switch (opcion) {
                    case 1 ->
                        cargarDatosInicialesExtracto();
                    case 2 ->
                        listarTodosLosRegistros();
                    case 3 ->
                        modificarConceptoRegistro();
                    case 4 ->
                        eliminarRegistroPorId();
                    case 5 ->
                        mostrarMetadatos();
                    case 6 ->
                        ejecutarPruebaCompletaAutomatizada();
                    case 0 ->
                        System.out.println("\nCerrando aplicación bancaria. ¡Hasta pronto!");
                    default ->
                        System.err.println("Opción no válida. Seleccione una opción entre 0 y 6.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Entrada incorrecta: introduzca un número entero para seleccionar la opción.");
            } catch (Exception e) {
                System.err.println("Se produjo un error no controlado: " + e.getMessage());
            }
            System.out.println();
        } while (opcion != 0);

        teclado.close();
    }

    private static void mostrarMenu() {
        System.out.println("==========================================================");
        System.out.println("     BANCO IBÉRICO - GESTOR DE APUNTES (CUENTAS.DAT)      ");
        System.out.println("==========================================================");
        System.out.println("1. Cargar datos del extracto bancario de la imagen");
        System.out.println("2. Mostrar todos los registros y saldo acumulado");
        System.out.println("3. Modificar concepto de un registro");
        System.out.println("4. Eliminar registro por ID (Baja lógica)");
        System.out.println("5. Mostrar número de registros y tamaño en bytes");
        System.out.println("6. Ejecutar ciclo completo de prueba");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Inserta los 7 apuntes reflejados en el extracto de cuenta de la imagen.
     */
    private static void cargarDatosInicialesExtracto() {
        System.out.println("\n[INFO] Insertando los datos del extracto bancario...");
        try {
            // Datos extraídos directamente de la imagen
            gestor.escribirApunte(new ApunteBancario(1, "01-01", "Saldo anterior", "", 0.00, 3528.76, true));
            gestor.escribirApunte(new ApunteBancario(2, "05-01", "Pago fra. teléfono", "05-01", -91.57, 3437.19, true));
            gestor.escribirApunte(new ApunteBancario(3, "10-01", "Ingreso en efectivo", "10-01", 150.00, 3587.19, true));
            gestor.escribirApunte(new ApunteBancario(4, "15-01", "Pago de préstamo", "15-01", -392.61, 3194.58, true));
            gestor.escribirApunte(new ApunteBancario(5, "20-01", "Cargo compra", "20-01", -123.45, 3071.13, true));
            gestor.escribirApunte(new ApunteBancario(6, "25-01", "Transf. a su favor", "25-01", 721.27, 3792.40, true));
            gestor.escribirApunte(new ApunteBancario(7, "31-01", "Reintegro cajero autom.", "31-01", -80.00, 3712.40, true));

            System.out.println("¡Datos iniciales cargados con éxito en '" + GestorCuentasRAF.NOMBRE_FICHERO + "'!");
        } catch (IOException e) {
            System.err.println("Error al escribir los apuntes bancarios: " + e.getMessage());
        }
    }

    /**
     * Muestra todos los apuntes del fichero y calcula el saldo final de los
     * activos.
     */
    private static void listarTodosLosRegistros() {
        try {
            List<ApunteBancario> apuntes = gestor.leerTodos(true);
            if (apuntes.isEmpty()) {
                System.out.println("El fichero se encuentra vacío o no existe.");
                return;
            }

            System.out.println("\n----------------- EXTRACTO DE CUENTA (CUENTAS.DAT) -----------------");
            double totalSaldoFinal = 0.0;
            int activos = 0;

            for (ApunteBancario a : apuntes) {
                System.out.println(a);
                if (a.isActivo()) {
                    totalSaldoFinal = a.getSaldo(); // El último apunte activo marca el saldo
                    activos++;
                }
            }
            System.out.println("--------------------------------------------------------------------");
            System.out.printf("Registros totales: %d | Registros activos: %d | Último saldo registrado: %.2f €\n",
                    apuntes.size(), activos, totalSaldoFinal);
        } catch (FileNotFoundException e) {
            System.err.println("El archivo no existe: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de lectura en el fichero: " + e.getMessage());
        }
    }

    /**
     * Modifica el concepto de un registro determinado.
     */
    private static void modificarConceptoRegistro() {
        try {
            System.out.print("Introduce el ID del registro a modificar (ej. 2): ");
            int id = Integer.parseInt(teclado.nextLine().trim());

            System.out.print("Introduce el nuevo concepto (ej. 'Ingreso'): ");
            String nuevoConcepto = teclado.nextLine().trim();

            boolean modificado = gestor.modificarConcepto(id, nuevoConcepto);
            if (modificado) {
                System.out.println("Registro con ID " + id + " modificado correctamente.");
            } else {
                System.err.println("No se pudo modificar: el ID no existe o se encuentra dado de baja.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El identificador debe ser un valor numérico.");
        } catch (IOException e) {
            System.err.println("Error al modificar en disco: " + e.getMessage());
        }
    }

    /**
     * Da de baja lógica un apunte por ID.
     */
    private static void eliminarRegistroPorId() {
        try {
            System.out.print("Introduce el ID del registro a eliminar (ej. 5 para Cargo compra): ");
            int id = Integer.parseInt(teclado.nextLine().trim());

            boolean eliminado = gestor.bajaLogica(id);
            if (eliminado) {
                System.out.println("Registro con ID " + id + " eliminado con éxito (Baja lógica).");
            } else {
                System.err.println("No se pudo eliminar: el ID no existe en el fichero.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El identificador debe ser un número entero.");
        } catch (IOException e) {
            System.err.println("Error al aplicar la baja lógica: " + e.getMessage());
        }
    }

    /**
     * Consulta y muestra el tamaño y número de registros.
     */
    private static void mostrarMetadatos() {
        try {
            System.out.println("\n[METADATOS DEL FICHERO]");
            System.out.println(gestor.obtenerMetadatosFichero());
        } catch (IOException e) {
            System.err.println("Error al obtener metadatos: " + e.getMessage());
        }
    }

    /**
     * Ejecuta automáticamente la secuencia solicitada en la práctica (Puntos 2
     * al 6).
     */
    private static void ejecutarPruebaCompletaAutomatizada() {
        System.out.println("\n========== INICIO DEL PROTOCOLO DE PRUEBA ==========");

        // Paso 2: Inserción de datos
        System.out.println("\n--- PASO 2: Inserción de los datos de la imagen ---");
        cargarDatosInicialesExtracto();

        // Paso 3: Eliminar "Cargo compra"
        // En la imagen original, "Cargo compra" es el 5.º movimiento del extracto (ID 5)
        System.out.println("\n--- PASO 3: Eliminación de 'Cargo compra' ---");
        try {
            // Buscamos si "Cargo compra" está en el ID 5 o en el ID 4 indicado en el enunciado
            List<ApunteBancario> lista = gestor.leerTodos(true);
            int idBorrar = 4; // Por defecto según enunciado
            for (ApunteBancario a : lista) {
                if (a.getConcepto().equalsIgnoreCase("Cargo compra")) {
                    idBorrar = a.getId();
                    break;
                }
            }
            gestor.bajaLogica(idBorrar);
            System.out.println("Registro '" + idBorrar + "' ('Cargo compra') marcado como eliminado.");
        } catch (IOException e) {
            System.err.println("Error en paso 3: " + e.getMessage());
        }

        // Paso 4: Modificar registro id 2 a "Ingreso"
        System.out.println("\n--- PASO 4: Modificación del ID 2 a 'Ingreso' ---");
        try {
            gestor.modificarConcepto(2, "Ingreso");
            System.out.println("Concepto del registro ID 2 modificado a 'Ingreso'.");
        } catch (IOException e) {
            System.err.println("Error en paso 4: " + e.getMessage());
        }

        // Paso 5: Mostrar número de registros y tamaño
        System.out.println("\n--- PASO 5: Metadatos y tamaño de fichero ---");
        mostrarMetadatos();

        // Paso 6: Cerrar y reabrir para mostrar todos los campos y el saldo total
        System.out.println("\n--- PASO 6: Relectura completa desde disco tras cerrar flujos ---");
        listarTodosLosRegistros();

        System.out.println("\n========== FIN DEL PROTOCOLO DE PRUEBA ==========");
    }
}
