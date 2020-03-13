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
    
    public Jugador(int i) {
        id = i;
    }
    
    public void sumaPuntos() {
        puntos++;
    }
    
    public int getId() {
        return id;
    }
}
