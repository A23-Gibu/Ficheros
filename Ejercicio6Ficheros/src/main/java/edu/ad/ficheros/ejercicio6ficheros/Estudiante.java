/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ad.ficheros.ejercicio6ficheros;

/**
 *
 * @author adrianm.gib
 */
public class Estudiante {
    private String nombre;
    private String clase;

    public Estudiante(String nombre, String clase) {
        this.nombre = nombre;
        this.clase = clase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }
    
    
}
