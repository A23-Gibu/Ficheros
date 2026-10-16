/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package edu.ad.ficheros.ejercicio6ficheros;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author adrianm.gib
 *
 * Ejercicio 6. Permite añadir nuevos alumnos al fichero (students.txt) sin
 * borrar los existentes.
 *
 */
public class Ejercicio6Ficheros {
    
    // Definimos la ruta del archivo fuera de main para usarla tambien en las funciones
    private static final File archivo = new File("students.txt");

    public static void main(String[] args) {
        
        // Creamos el archivo si no existe
        try {
            if (archivo.createNewFile()) {
                System.out.println("El fichero students.txt no existía y se ha creado correctamente");
            }
            else System.out.println("El archivo ya existe");
        } catch (IOException e) {
            System.err.println("Error al intentar crear el fichero: " + e.getMessage());
        }
        
        // Pedimos los datos del alumno a insertar
        Scanner entrada = new Scanner(System.in);
        
        int opcion;
        do{
            System.out.println("-----MENU-----");
            System.out.println("Elige una opcion");
            System.out.println("1. Introducir un nuevo alumno");
            System.out.println("0. Salir");
            
            opcion = entrada.nextInt();
            // Limpieza del buffer, si no saltaría directamente a clase y no cogería el nombre
            entrada.nextLine();
            
            switch(opcion){
                case 1 -> IntroducirAlumno(entrada);
                case 0 -> System.out.println("Saliendo del programa");
                default -> System.out.println("Opcion invalida");
            }
        }while(opcion != 0);
        
        entrada.close();
    }
    
    public static void IntroducirAlumno(Scanner entrada){
        System.out.println("Introduce el nombre del alumno: ");
        String nombre = entrada.nextLine();
        
        System.out.println("Introduce la clase: ");
        String clase = entrada.nextLine();
        
        // Escritura en modo append con try-with-resources
        // El parámetro 'true' en FileWriter es lo que evita borrar los alumnos existentes
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))){
            bw.write(nombre + " :" + clase );
            bw.newLine();
        }catch(IOException e){
            System.out.println("Error al escribir: " + e.getMessage());
        }
    }
}
