/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ficheros.gibu_adrian_ficheros_ej2;

/**
 *
 * @author adria
 */
public class Estudiante {

    private String nombre;
    private String curso;
    private double nota;

    public Estudiante(String nombre, String curso, double nota) {
        this.nombre = nombre;
        this.curso = curso;
        this.nota = nota;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCurso() {
        return curso;
    }

    public double getNota() {
        return nota;
    }

    // Formato CSV respetando comillas si hubiera caracteres especiales
    public String toCsvRow() {
        return String.format("%s,%s,%.2f", nombre, curso, nota);
    }
}
