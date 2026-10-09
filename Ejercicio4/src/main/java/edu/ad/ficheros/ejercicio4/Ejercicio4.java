/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ad.ficheros.ejercicio4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author adrianm.gib
 */
public class Ejercicio4 {

    public static void main(String[] args) {
        File archivo = new File("file.txt");

        // 1. Validar que el archivo exista antes de intentar abrirlo
        if (!archivo.exists() || !archivo.isFile()) {
            System.err.println("El archivo file.txt no existe o no es un fichero válido");
            return;
        }

        int totalPalabras = 0;
        int totalLineas = 0;

        // 2. lectura con try with resources
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                totalLineas++;

                //Limpiar espacios en blanco al inicio y al final
                String lineaLimpia = linea.trim();

                //Si la linea no esta vacia, separamos por espacios
                if (!lineaLimpia.isEmpty()) {
                    // \\s+ agrupa uno o mas espacios, tabuladores o saltos seguidos
                    String[] palabras = lineaLimpia.split("\\s+");
                    totalPalabras += palabras.length;
                }
                
            }
            
            System.out.println("Lectura completada con exito.");
            System.out.println("Lineas leidas: " + totalLineas);
            System.out.println("Total de palabras: " + totalPalabras);
            
        }catch (FileNotFoundException e) {
            System.err.println("Error: Fichero no encontrado -> " + e.getMessage());          
        }catch (IOException e) {
            System.err.println("Error de lectura en el archivo -> " + e.getMessage());
        }

        }
    }
