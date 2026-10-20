/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.ad.ficheros.ejercicio9ficheros;

import java.io.File;
import java.nio.file.Files;

/**
 *
 * @author adrianm.gib
 */
public class Ejercicio9Ficheros {

    public static void main(String[] args) {
        
        if(new File("datos").exists()){
            System.out.println("El directorio datos ya existe");
        }else {
            Files.createDirectory(new File("datos/productos.csv"));
        }
    }
}
