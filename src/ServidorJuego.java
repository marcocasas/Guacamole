/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author
 */
public class ServidorJuego {

    Jugador players[]; // Arreglo de jugadores
    int posicionTopo;
    int numeroJugadores;
    int puntosParaGanar;
    int segundosEntreTopo;
    boolean puntoDado;
    
    public ServidorJuego() {
        numeroJugadores = 0;
        players = new Jugador[10];
        puntoDado = false;
        puntosParaGanar = 5;
        segundosEntreTopo = 3000;
    }

    public void setPosicionTopo(int i) {
        posicionTopo = i;
    }
    
    public void setPuntoDado(boolean b) {
        puntoDado = b;
    }
    
    public int getSegundosEntreTopo() {
        return segundosEntreTopo;
    }
    
    public int getNumeroJugadores() {
        return numeroJugadores;
    }
    
    public boolean getPuntoDado() {
        return puntoDado;
    }
    
    public int getPosicionTopo() {
        return posicionTopo;
    }
    
    public void nuevoJugador(Jugador j) {
        players[numeroJugadores] = j;
        numeroJugadores++;
    }
    
    public void obtenerListaJugadores() {
        System.out.println("Están jugando:");
        for(Jugador p : players) {
            if (p != null) {
                System.out.println("Jugador " + p.getId());
            }
        }
    }
    
    public boolean alguienYaGano() {
        boolean r = false;
        int i = 0;
        while(!r && i < numeroJugadores && players[i] != null) {
            if (players[i].getPuntos() == puntosParaGanar){
                r = true;
            }
            i++;
        }
        return r;
    }
    
    public String obtenerGanador() {
        StringBuilder resp = new StringBuilder();
        resp.append("¡Ganó el Jugador ");
        int i = 0;
        
        while(i < numeroJugadores && players[i] != null) {
            if(players[i].getPuntos() == puntosParaGanar) {
                resp.append(i);
            }
            i++;
        }
        resp.append("!");
        return resp.toString();
    }

    public String muestraTablero() {
        StringBuilder sb = new StringBuilder();
        
        int i = 0;
        while(i < numeroJugadores && players[i] != null) {
            sb.append(players[i].toString());
            sb.append("-");
            i++;
        }
        
        return sb.toString();
    }
    
    public static void main(String args[]) throws InterruptedException {

        ServidorJuego servJuego = new ServidorJuego();
        Tablero t = new Tablero(servJuego);
        t.start(); // Hilo para enviar constantemente tableros cambiantes.

        try {
            int serverPort = 7896; // Puerto conexiones TCP
            ServerSocket listenSocket = new ServerSocket(serverPort);
            
            while (true) {
                System.out.println("Waiting for players...");
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
                
                //Instanciamos nuevo jugador
                Jugador j = new Jugador(servJuego.getNumeroJugadores());
                
                Connection c = new Connection(clientSocket, servJuego, j);
                
                servJuego.nuevoJugador(j);
                servJuego.obtenerListaJugadores(); // Prueba para ver que se añagen jugadores.
                
                c.start();
            }
            
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }

    }
}
