package estresamiento;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marco
 */
public class Jugador {
    private int id;
    private int puntos;
    private String nombre;
    boolean active;
    
    public Jugador() {
        active = true;
    }
    
    public Jugador(int i) {
        id = i;
        active = true;
    }

    public Jugador(int i, String nombre) {
        id = i;
        active = true;
        this.nombre = nombre;
    }
    
    public void sumaPuntos() {
        puntos++;
        //System.out.println("¡Punto para ti!");
    }
    
    public int getPuntos() {
        return puntos;
    }
    
    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return nombre + " (Jugador " + id + ") con " + puntos + " pts";
    }
    
    public boolean estaActivo() {
        return active;
    }
    
    public void marcarJugadorInactivo() {
        this.active = false;
    }
    
    public void marcarJugadorActivo() {
        this.active = true;
    }
    
    public void reseteaPuntuacion() {
        this.puntos = 0;
    }
}
