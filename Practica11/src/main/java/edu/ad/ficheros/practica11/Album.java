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
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    // 1. Atributo static: pertenece a la clase, NO se serializa
    public static String formatoDistribucion = "Digital / Streaming";

    // 2. Atributo transient: se ignora al serializar (volverá como null)
    private transient String enlaceDescargaPrivado;

    // 3. Atributos propios
    private int id;
    private String titulo;
    private int anioLanzamiento;
    private double precio;

    // 4. Referencia a un objeto de otra clase
    private Productor productor;

    // 5. Referencia a una lista de objetos de otra clase
    private List<Cancion> tracklist;

    public Album(int id, String titulo, int anioLanzamiento, double precio,
                 String enlaceDescargaPrivado, Productor productor, List<Cancion> tracklist) {
        this.id = id;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.precio = precio;
        this.enlaceDescargaPrivado = enlaceDescargaPrivado;
        this.productor = productor;
        this.tracklist = tracklist;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return "Álbum [ID=" + id + ", Título='" + titulo + "' (" + anioLanzamiento + "), Precio=" + precio + "€" +
               ", Formato(static)=" + formatoDistribucion +
               ", TokenDescarga(transient)=" + enlaceDescargaPrivado +
               "\n   -> Productor: " + productor +
               "\n   -> Tracklist: " + tracklist + "]";
    }
}
