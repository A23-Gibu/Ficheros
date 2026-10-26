/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ad.ficheros.ejercicio9ficheros;

/**
 *
 * @author adrianm.gib
 */
public class Producto {
    private int id;
    private String nombre;
    private double precio;

    /**
     * Construye una instancia de Producto validando sus reglas de negocio.
     * 
     * @param id Identificador único entero mayor que 0.
     * @param nombre Denominación del producto (no nula ni en blanco).
     * @param precio Valor monetario mayor o igual que 0.0.
     * @throws ProductoInvalidoException Si algún parámetro infringe las validaciones.
     */
    public Producto(int id, String nombre, double precio) throws ProductoInvalidoException {
        if (id <= 0) {
            throw new ProductoInvalidoException("El ID debe ser un número entero estrictamente positivo (> 0).");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
        }
        if (precio < 0.0) {
            throw new ProductoInvalidoException("El precio no puede ser negativo (>= 0.0).");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    /**
     * Serializa los datos del producto en formato delimitado por el separador indicado.
     * 
     * @param delimitador Carácter o cadena delimitadora (ej: ";" o "\t").
     * @return Cadena formateada para persistencia.
     */
    public String toDelimitedString(String delimitador) {
        return id + delimitador + nombre + delimitador + precio;
    }

    /**
     * Genera la representación en formato JSON simple sin librerías externas.
     * 
     * @return Cadena JSON que representa el objeto.
     */
    public String toJson() {
        return String.format("  {\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}", id, nombre.replace("\"", "\\\""), precio);
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Nombre: %-25s | Precio: %8.2f €", id, nombre, precio);
    }
}
