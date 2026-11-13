/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ad.ficheros.practica11;

/**
 *
 * @author adrianm.gib
 */
import java.io.Serializable;

public class Cancion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int pista;
    private String titulo;
    private int duracionSegundos;

    public Cancion(int pista, String titulo, int duracionSegundos) {
        this.pista = pista;
        this.titulo = titulo;
        this.duracionSegundos = duracionSegundos;
    }

    @Override
    public String toString() {
        int min = duracionSegundos / 60;
        int seg = duracionSegundos % 60;
        return String.format("%d. %s (%d:%02d)", pista, titulo, min, seg);
    }
}
