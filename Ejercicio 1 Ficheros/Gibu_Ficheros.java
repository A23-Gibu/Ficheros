/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.ficheros.ficheros;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Stream;

/*Crea la siguiente estructura y comprueba su existencia:
* - src, bin, img, src/junit y los ficheros: readme.md, src/run.java e img/imagen
* - recorrer el contenido y muéstralo por pantalla
* @author adrianm.gib
*/
public class Gibu_Ficheros {

    public static void main(String[] args) {
        //Creación de los path de los directorios
        Path dirBin = Paths.get("bin");
        Path dirImg = Paths.get("img");
        Path dirSrcJunit = Paths.get("src/junit");
        
        //Creación de los path de los archivos
        Path ficReadme = Paths.get("readme.md");
        Path ficRun = Paths.get("src/run.java");
        Path ficImagen = Paths.get("img/imagen");
        
        
        try {
            // Creamos las carpetas pasando como parametro el anterior path
            Files.createDirectories(dirBin);
            Files.createDirectories(dirImg);
            Files.createDirectories(dirSrcJunit);

            // Creamos los archivos solo si no existen
            if (!Files.exists(ficReadme)) {
                Files.createFile(ficReadme);
            }
            if (!Files.exists(ficRun)) {
                Files.createFile(ficRun);
            }
            if (!Files.exists(ficImagen)) {
                Files.createFile(ficImagen);
            }

            System.out.println("Estructura creada correctamente.");

            //Recorremos el contenido 
            System.out.println("---Mostrando el contenido---");

            // Paths.get(".") crea el path para la carpeta actual
            Path raiz = Paths.get(".");

            // Files.walk recorre la carpeta y lo que hay dentro
            try (Stream flujo = Files.walk(raiz)) {
                flujo.forEach(ruta -> {
                    // Mostramos los elementos
                    System.out.println(ruta);
                });
            }

        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
}
