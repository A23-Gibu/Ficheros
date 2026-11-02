package edu.ad.ficheros.ejercicio9ficheros;

/**
 * Excepción personalizada para señalar infracciones de las reglas de negocio
 * en los datos de un producto (ID no positivo, nombre vacío o precio negativo).
 * Hereda de Exception para obligar a su captura o tratamiento comprobado.
 * 
 * @author adria
 */
public class ProductoInvalidoException extends Exception {

    /**
     * Construye la excepción con un mensaje descriptivo de la regla infringida.
     * 
     * @param mensaje Detalle del error de validación.
     */
    public ProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
