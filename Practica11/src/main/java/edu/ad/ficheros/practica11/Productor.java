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

public class Productor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreArtistico;
    private String discografica;

    public Productor(String nombreArtistico, String discografica) {
        this.nombreArtistico = nombreArtistico;
        this.discografica = discografica;
    }

    @Override
    public String toString() {
        return nombreArtistico + " [" + discografica + "]";
    }
}
