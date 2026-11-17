/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.ad.ficheros.ejercicio5ficheros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author adrianm.gib
 */

/*Ejercicio 5. Solicita una cadena de texto y un fichero al usuario. 
Muestra por pantalla las líneas del fichero que contienen la cadena del usuario, 
incluyendo su número de línea (estilo comando 'grep').*/

public class Ejercicio5Ficheros {

    public static void main(String[] args) {
        // Pedir la información a buscar y el lugar (archivo)
        Scanner entrada = new Scanner(System.in);
        
        System.out.println("Introduce la frase o palabra a buscar: ");
        String frase = entrada.nextLine();
        
        System.out.println("Introduce el archivo donde quieres buscarlo: ");
        String archivoString = entrada.nextLine();
        
        // Comprobamos que exista el archivo
        File archivo = new File(archivoString);
        if(!archivo.exists() || !archivo.isFile()){
            System.out.println("La ruta proporcionada no existe o no es un archivo.");
            return ;
        }
        
        // Una vez comprobado y creado el archivo pasamos a leerlo
        try(BufferedReader br = new BufferedReader(new FileReader(archivo))){
            String linea;
            int numLinea = 0;
            
            while(((linea = br.readLine()) != null)){
                numLinea ++;
                if(linea.contains(frase)){
                    System.out.println(" LINEA NUMERO: " + numLinea + ": " + linea);
                }
            }
        }catch(FileNotFoundException e) {
            System.err.println("Fichero no encontrado " + e.getMessage());
        }catch(IOException e) {
            System.err.println("Error de lectura en el archivo " + e.getMessage());
        }
         
        
        entrada.close();
        
    }
}
