/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ficheros.gibu_adrian_ficheros_ej3;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Scanner;

/**
 *
 * @author adria
 */
public class Gibu_Adrian_Ficheros_Ej3 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduce la ruta del directorio a explorar: ");
        String rutaEntrada = sc.nextLine().trim();

        File directorio = new File(rutaEntrada);

        // Validacion de robustez: existencia y tipo de recurso
        if (!directorio.exists()) {
            System.err.println("Error: El directorio indicado no existe.");
            sc.close();
            return;
        }

        if (!directorio.isDirectory()) {
            System.err.println("Error: La ruta proporcionada no corresponde a un directorio.");
            sc.close();
            return;
        }

        System.out.println("\nDirectorio de trabajo: " + directorio.getAbsolutePath());
        System.out.println("===============================================================");

        // --- FORMA 1: Clase anónima FilenameFilter (.txt) ---
        System.out.println("\n[FORMA 1] Filtro .txt con Clase Anonima (nombre y tamaño):");
        File[] ficherosTxt = directorio.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        if (ficherosTxt == null || ficherosTxt.length == 0) {
            System.out.println("  No se encontraron ficheros .txt.");
        } else {
            for (File f : ficherosTxt) {
                System.out.println("  Archivo: " + f.getName() + " | Tamaño: " + f.length() + " bytes");
            }
        }

        // --- FORMA 2: Expresión lambda FilenameFilter (.java) ---
        System.out.println("\n[FORMA 2] Filtro .java con Expresion Lambda:");
        File[] ficherosJava = directorio.listFiles(
                (dir, nombre) -> nombre.toLowerCase().endsWith(".java")
        );

        if (ficherosJava == null || ficherosJava.length == 0) {
            System.out.println("  No se encontraron ficheros .java.");
        } else {
            for (File f : ficherosJava) {
                System.out.println("  Archivo: " + f.getName());
            }
        }

        // --- FORMA 3: startsWith("alumno") && endsWith(".csv") con ruta absoluta ---
        System.out.println("\n[FORMA 3] Filtro 'alumno*.csv' (muestra ruta absoluta):");
        File[] ficherosAlumnoCsv = directorio.listFiles((dir, nombre) -> {
            String nom = nombre.toLowerCase();
            return nom.startsWith("alumno") && nom.endsWith(".csv");
        });

        if (ficherosAlumnoCsv == null || ficherosAlumnoCsv.length == 0) {
            System.out.println("  No se encontraron ficheros que empiecen por 'alumno' y terminen en '.csv'.");
        } else {
            for (File f : ficherosAlumnoCsv) {
                System.out.println("  Ruta absoluta: " + f.getAbsolutePath());
            }
        }

        // --- FORMA 4: FileFilter solo directorios ([DIR] nombre) ---
        System.out.println("\n[FORMA 4] Filtro solo subdirectorios con FileFilter:");
        File[] subdirectorios = directorio.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if (subdirectorios == null || subdirectorios.length == 0) {
            System.out.println("  No se encontraron subdirectorios.");
        } else {
            for (File d : subdirectorios) {
                System.out.println("  [DIR] " + d.getName());
            }
        }

        System.out.println("\n===============================================================");
        System.out.println("Exploracion finalizada con exito.");
        sc.close();
    }
}
