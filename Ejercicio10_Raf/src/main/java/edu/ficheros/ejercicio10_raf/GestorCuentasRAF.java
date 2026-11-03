/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ficheros.ejercicio10_raf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia directa sobre 'cuentas.dat' utilizando RandomAccessFile.
 * Gestiona lecturas, escrituras, modificaciones directas y bajas lógicas.
 * 
 * @author adria
 */
public class GestorCuentasRAF {

    public static final String NOMBRE_FICHERO = "cuentas.dat";
    
    // Tamaños constantes de los campos en caracteres
    public static final int LONG_FECHA = 5;       // 5 chars = 10 bytes
    public static final int LONG_CONCEPTO = 25;   // 25 chars = 50 bytes
    
    // Tamaño exacto del registro: 4 + 10 + 50 + 10 + 8 + 8 + 1 = 91 bytes
    public static final int TAM_REGISTRO = (Integer.BYTES) 
            + (LONG_FECHA * Character.BYTES) 
            + (LONG_CONCEPTO * Character.BYTES) 
            + (LONG_FECHA * Character.BYTES) 
            + (Double.BYTES) 
            + (Double.BYTES) 
            + 1;

    private final File archivo;

    /**
     * Inicializa el gestor con el archivo por defecto.
     */
    public GestorCuentasRAF() {
        this.archivo = new File(NOMBRE_FICHERO);
    }

    /**
     * Escribe un apunte bancario en la posición fija calculada por su identificador.
     * 
     * @param apunte Objeto con la información bancaria.
     * @throws IOException Si ocurre un error de E/S.
     */
    public void escribirApunte(ApunteBancario apunte) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            long posicion = (long) (apunte.getId() - 1) * TAM_REGISTRO;
            raf.seek(posicion);

            // 1. ID (4 bytes)
            raf.writeInt(apunte.getId());

            // 2. Fecha Operacion (10 bytes - 5 caracteres fijos)
            escribirCadenaFija(raf, apunte.getFechaOperacion(), LONG_FECHA);

            // 3. Concepto (50 bytes - 25 caracteres fijos)
            escribirCadenaFija(raf, apunte.getConcepto(), LONG_CONCEPTO);

            // 4. Fecha Valor (10 bytes - 5 caracteres fijos)
            escribirCadenaFija(raf, apunte.getFechaValor() == null ? "" : apunte.getFechaValor(), LONG_FECHA);

            // 5. Importe (8 bytes)
            raf.writeDouble(apunte.getImporte());

            // 6. Saldo (8 bytes)
            raf.writeDouble(apunte.getSaldo());

            // 7. Flag de borrado lógico (1 byte)
            raf.writeBoolean(apunte.isActivo());
        }
    }

    /**
     * Lee un registro directamente desde la posición actual del puntero.
     * 
     * @param raf Flujo de acceso aleatorio ya posicionado.
     * @return Instancia reconstruida de ApunteBancario.
     * @throws IOException Si falla la lectura.
     */
    public ApunteBancario leerRegistroActual(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        String fechaOperacion = leerCadenaFija(raf, LONG_FECHA);
        String concepto = leerCadenaFija(raf, LONG_CONCEPTO);
        String fechaValor = leerCadenaFija(raf, LONG_FECHA);
        double importe = raf.readDouble();
        double saldo = raf.readDouble();
        boolean activo = raf.readBoolean();

        return new ApunteBancario(id, fechaOperacion, concepto, fechaValor, importe, saldo, activo);
    }

    /**
     * Recupera todos los apuntes almacenados en el fichero secuencialmente.
     * 
     * @param incluirBorrados Indica si se muestran también las bajas lógicas.
     * @return Lista de apuntes recuperados.
     * @throws IOException Si ocurre un error de lectura.
     */
    public List<ApunteBancario> leerTodos(boolean incluirBorrados) throws IOException {
        List<ApunteBancario> lista = new ArrayList<>();
        if (!archivo.exists()) {
            return lista;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            long totalRegistros = raf.length() / TAM_REGISTRO;
            for (int i = 0; i < totalRegistros; i++) {
                raf.seek((long) i * TAM_REGISTRO);
                ApunteBancario apunte = leerRegistroActual(raf);
                if (incluirBorrados || apunte.isActivo()) {
                    lista.add(apunte);
                }
            }
        }
        return lista;
    }

    /**
     * Modifica exclusivamente el concepto de un apunte bancario directamente en disco
     * sin alterar el resto de campos del registro.
     * 
     * @param id Identificador del registro a modificar.
     * @param nuevoConcepto Nueva descripción.
     * @return true si se modificó correctamente, false si no existe o está inactivo.
     * @throws IOException Si ocurre un error de acceso al fichero.
     */
    public boolean modificarConcepto(int id, String nuevoConcepto) throws IOException {
        if (!archivo.exists() || id <= 0) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            long posicion = (long) (id - 1) * TAM_REGISTRO;
            if (posicion + TAM_REGISTRO > raf.length()) {
                return false;
            }

            // Verificamos si el registro está activo
            raf.seek(posicion);
            ApunteBancario actual = leerRegistroActual(raf);
            if (!actual.isActivo()) {
                return false;
            }

            // Desplazamiento hasta el campo concepto: ID (4) + FechaOperacion (10) = 14 bytes
            long posConcepto = posicion + Integer.BYTES + (LONG_FECHA * Character.BYTES);
            raf.seek(posConcepto);
            escribirCadenaFija(raf, nuevoConcepto, LONG_CONCEPTO);
            return true;
        }
    }

    /**
     * Aplica una baja lógica a un registro mediante su flag booleano.
     * 
     * @param id Identificador del apunte a eliminar.
     * @return true si fue marcado como eliminado; false en caso contrario.
     * @throws IOException Si ocurre un error de E/S.
     */
    public boolean bajaLogica(int id) throws IOException {
        if (!archivo.exists() || id <= 0) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            long posicion = (long) (id - 1) * TAM_REGISTRO;
            if (posicion + TAM_REGISTRO > raf.length()) {
                return false;
            }

            // Desplazamiento al flag: inicio del registro + 90 bytes
            long posFlag = posicion + (TAM_REGISTRO - 1);
            raf.seek(posFlag);
            raf.writeBoolean(false); // Baja lógica sin romper la estructura fija
            return true;
        }
    }

    /**
     * Devuelve el tamaño en bytes y el total de registros calculados.
     * 
     * @return Cadena informativa de metadatos.
     * @throws IOException Si no se puede consultar el archivo.
     */
    public String obtenerMetadatosFichero() throws IOException {
        if (!archivo.exists()) {
            return "El fichero '" + NOMBRE_FICHERO + "' aún no existe.";
        }
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            long bytes = raf.length();
            long totalRegistros = bytes / TAM_REGISTRO;
            return String.format("Fichero: %s | Tamaño: %d bytes | Total huecos de registro: %d (a %d bytes c/u)",
                    archivo.getName(), bytes, totalRegistros, TAM_REGISTRO);
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES PARA CADENAS DE LONGITUD FIJA
    // =========================================================================

    /**
     * Escribe una cadena truncándola o rellenándola hasta la longitud exacta requerida.
     */
    private void escribirCadenaFija(RandomAccessFile raf, String texto, int longitud) throws IOException {
        StringBuffer sb = new StringBuffer(texto == null ? "" : texto);
        sb.setLength(longitud); // Trunca o rellena con caracteres nulos '\0'
        raf.writeChars(sb.toString()); // Escribe 2 bytes por carácter
    }

    /**
     * Lee una cadena de longitud fija y elimina los espacios y caracteres nulos sobrantes.
     */
    private String leerCadenaFija(RandomAccessFile raf, int longitud) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(raf.readChar()); // Lee 2 bytes Unicode por carácter
        }
        return sb.toString().replace('\0', ' ').trim();
    }
}
