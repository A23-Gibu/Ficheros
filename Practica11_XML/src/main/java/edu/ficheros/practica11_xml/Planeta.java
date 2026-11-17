/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ficheros.practica11_xml;

/**
 *
 * @author adria
 */
import java.util.List;

public class Planeta {

    private String nombre;
    private String tamano;
    private List<Luna> lunas;

    public Planeta(String nombre, String tamano, List<Luna> lunas) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.lunas = lunas;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTamano() {
        return tamano;
    }

    public List<Luna> getLunas() {
        return lunas;
    }
}
