/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ficheros.gibu_adrian_ficheros_ej2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adria
 */
public class Gibu_Adrian_Ficheros_Ej2 {

    public static void main(String[] args) throws IOException {
        // Creación de las 5 instancias
        List<Estudiante> estudiantes = new ArrayList<>();
        estudiantes.add(new Estudiante("Sofía García", "Programación II", 9.5));
        estudiantes.add(new Estudiante("Mateo López", "Bases de Datos", 7.2));
        estudiantes.add(new Estudiante("Lucía Martín", "Estructuras de Datos", 8.8));
        estudiantes.add(new Estudiante("Carlos Ruiz", "Redes", 6.4));
        estudiantes.add(new Estudiante("Elena Torres", "Sistemas Operativos", 10.0));

        // Definición de ruta 
        Path rutaArchivo = Paths.get("estudiantes.csv");

        // Escritura 
        try (BufferedWriter writer = Files.newBufferedWriter(
                rutaArchivo,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            // Cabecera del archivo CSV
            writer.write("Nombre,Curso,Nota");
            writer.newLine();

            // Escritura de cada instancia
            for (Estudiante est : estudiantes) {
                writer.write(est.toCsvRow());
                writer.newLine();
            }

            System.out.println("Archivo CSV generado en: " + rutaArchivo.toAbsolutePath());

        }
    }
}
