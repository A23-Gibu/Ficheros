/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ficheros.ejercicio8ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author adria
 *
 * Aplicación completa que gestiona un fichero alumnos.txt con operaciones de
 * alta, consulta, búsqueda y conteo. Es el primer mini proyecto integrador del
 * bloque de texto. Alta. Añadir un alumno al final del fichero. Mostrar. Leer y
 * listar todos los alumnos. Buscar. Localizar un alumno por nombre. Contar.
 * Indicar el total de registros.
 */
public class Ejercicio8Ficheros {

    private static final Scanner entrada = new Scanner(System.in);
    private static final File alumnos = new File("alumnos.txt");

    public static void main(String[] args) {

        int eleccion;
        do {
            System.out.println("-----MENU------");
            System.out.println("Elige entre las siguientes opciones: ");
            System.out.println("1. Alta alumno");
            System.out.println("2. Mostrar alumnos");
            System.out.println("3. Buscar alumno");
            System.out.println("4. Total alumnos");
            System.out.println("0. Salir");

            eleccion = entrada.nextInt();
            entrada.nextLine();

            switch (eleccion) {
                case 1 ->
                    altaAlumno();
                case 2 ->
                    mostrarAlumnos();
                case 3 ->
                    buscarAlumno();
                case 4 ->
                    totalAlumnos();
                case 0 ->
                    System.out.println("Saliendo del programa...");
                default ->
                    System.out.println("Opcion no disponible");

            }
        } while (eleccion != 0);
        entrada.close();
    }

    public static void altaAlumno() {
        System.out.println("Introduce el nombre del alumno: ");
        String nombre = entrada.nextLine();

        System.out.println("Introduce la clase del alumno: ");
        String clase = entrada.nextLine();

        // El proprio FileWriter se encarga de crear el archivo si no lo hubiera
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(alumnos, true))) {
            bw.write(nombre + " " + clase);
            bw.newLine();
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        }
    }

    public static void mostrarAlumnos() {
        try (BufferedReader br = new BufferedReader(new FileReader(alumnos))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        }
    }

    public static void buscarAlumno() {
        System.out.println("Introduce el nombre del alumno a buscar: ");
        String nombreAlumno = entrada.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(alumnos))) {
            String linea;
            int numLinea = 0;
            boolean alumnoEncontrado = false;
            while ((linea = br.readLine()) != null) {
                numLinea += 1;
                if (linea.contains(nombreAlumno)) {
                    System.out.println("El alumno :" + nombreAlumno + " se encuentra en la línea: " + numLinea);
                    alumnoEncontrado = true;
                }
            }
            if (!alumnoEncontrado) {
                System.out.println("El alumno no se encuentra registrado.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        }
    }

    public static void totalAlumnos() {
        try (BufferedReader br = new BufferedReader(new FileReader(alumnos))) {
            String linea;
            int numLineas = 0;
            while ((linea = br.readLine()) != null) {
                numLineas++;
            }
            System.out.println("El numero total de alumnos es: " + numLineas);
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        }
    }
}
