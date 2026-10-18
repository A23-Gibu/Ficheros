/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.ficheros.ejercicio7ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author adria
 * Ejercicio 7. Copia el contenido de un fichero de texto en otro usando BufferedReader/Writer
 */
public class Ejercicio7Ficheros {

    public static void main(String[] args) {
        
        // Creando el archivo original
        // try con {} 
        
        File archivoOriginal = new File("archivoOriginal.txt");
        File archivoCopiado = new File("archivoCopiado.txt");
        
        try{
            
            if(archivoOriginal.exists()){
                System.err.println("El archivo ya existe");
            }
            else{
                archivoOriginal.createNewFile();
            }   
        }catch(IOException e){
            System.out.println("Error al crear el archivo");
        }
        
        // Escribiendo algo en el archivo creado
        // bw para escribir algo primero en el original
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(archivoOriginal))){
            
            // Escribimos algo en el original
            bw.write("Miau");
            bw.newLine();
            bw.write("Guau");
            bw.newLine();
            bw.write("quack");
            
            
           
        }catch(IOException e){
            System.err.println("Error de entrada/salida " + e.getMessage());
        }
        
        // br para leer lo que hemos escrito en el original
        // bw2 para copiar lo escrito en el original (el true significa que sigue escribiendo sin borrar lo que ya había en el archivo)
        // La ruta introducida en el FileWriter se crea automáticamente 
        try(BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(archivoCopiado, true))){
            
            // Leemos lo del original para almacenarlo y posteriormente escribirlo en el copiado
            String linea;
            while((linea = br.readLine()) != null){
                bw2.append(linea);
                bw2.newLine();
            }
        }catch(IOException e){
            System.err.println("Error de entrada/salida " + e.getMessage());
        }
        
    }
}
