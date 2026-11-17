package edu.ad.ficheros.ejercicio9ficheros;

/**
 * Modelo de datos representativo de la entidad Producto en el catálogo.
 * Encapsula identificador, denominación y precio con validaciones de integridad.
 * 
 * @author adria
 */
public class Producto {

    private int id;
    private String nombre;
    private double precio;

    /**
     * Constructor con validaciones estrictas de reglas de negocio.
     * 
     * @param id Identificador entero mayor a cero.
     * @param nombre Denominación no nula ni en blanco.
     * @param precio Valor numérico mayor o igual a 0.0.
     * @throws ProductoInvalidoException Si algún parámetro infringe las restricciones.
     */
    public Producto(int id, String nombre, double precio) throws ProductoInvalidoException {
        if (id <= 0) {
            throw new ProductoInvalidoException("El ID debe ser estrictamente mayor a 0.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
        }
        if (precio < 0.0) {
            throw new ProductoInvalidoException("El precio no puede ser negativo (debe ser >= 0.0).");
        }

        this.id = id;
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) throws ProductoInvalidoException {
        if (id <= 0) {
            throw new ProductoInvalidoException("El ID debe ser estrictamente mayor a 0.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws ProductoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) throws ProductoInvalidoException {
        if (precio < 0.0) {
            throw new ProductoInvalidoException("El precio no puede ser negativo.");
        }
        this.precio = precio;
    }

    /**
     * Serializa los datos del producto en formato de texto plano con un delimitador arbitrario.
     * 
     * @param delimitador Separador de campos (ej. ";" para CSV o "\t" para TSV).
     * @return Cadena formateada para persistencia secuencial.
     */
    public String toDelimitedString(String delimitador) {
        return id + delimitador + nombre + delimitador + precio;
    }

    /**
     * Representación JSON simple sin requerir librerías externas.
     * 
     * @return Registro JSON estructurado en una sola línea.
     */
    public String toJson() {
        // Escapamos comillas dobles en caso fortuito de que el nombre contenga alguna
        return String.format("  {\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}", 
                id, nombre.replace("\"", "\\\""), precio);
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Nombre: %-25s | Precio: %8.2f €", id, nombre, precio);
    }
}