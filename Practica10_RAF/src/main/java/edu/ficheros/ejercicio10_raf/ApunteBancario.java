/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.ficheros.ejercicio10_raf;

/**
 * Representa un apunte bancario extraído del extracto de cuenta.
 * Define una estructura de datos apta para registros de tamaño fijo.
 * 
 * @author adria
 */
public class ApunteBancario {

    private int id;
    private String fechaOperacion;
    private String concepto;
    private String fechaValor;
    private double importe;
    private double saldo;
    private boolean activo;

    /**
     * Constructor por defecto.
     */
    public ApunteBancario() {
        this.activo = true;
    }

    /**
     * Constructor completo para inicializar un apunte bancario.
     * 
     * @param id Identificador numérico del apunte.
     * @param fechaOperacion Fecha en la que se registra la operación (formato dd-MM).
     * @param concepto Descripción del apunte bancario.
     * @param fechaValor Fecha de efecto de la transacción (formato dd-MM).
     * @param importe Importe ingresado o adeudado.
     * @param saldo Saldo resultante tras la transacción.
     * @param activo Estado del registro (true para activo, false para baja lógica).
     */
    public ApunteBancario(int id, String fechaOperacion, String concepto, String fechaValor,
                          double importe, double saldo, boolean activo) {
        this.id = id;
        this.fechaOperacion = fechaOperacion;
        this.concepto = concepto;
        this.fechaValor = fechaValor;
        this.importe = importe;
        this.saldo = saldo;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(String fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getFechaValor() {
        return fechaValor;
    }

    public void setFechaValor(String fechaValor) {
        this.fechaValor = fechaValor;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return String.format("ID: %-2d | F.Oper: %-5s | Concepto: %-22s | F.Valor: %-5s | Importe: %10.2f € | Saldo: %10.2f € | Estado: %s",
                id,
                fechaOperacion,
                concepto,
                fechaValor.isEmpty() ? "--" : fechaValor,
                importe,
                saldo,
                activo ? "Activo" : "BORRADO");
    }
}
