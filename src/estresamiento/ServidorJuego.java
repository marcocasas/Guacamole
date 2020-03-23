/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estresamiento;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ServidorJuego {

    Jugador players[]; // Arreglo de jugadores
    int posicionTopo;
    int numeroJugadores;
    int puntosParaGanar;
    int segundosEntreTopo;
    boolean puntoDado;
    final int maxJugadores = 10000000;
    long tiempos;
    int cuantos;
    double [] tes;
    double [] promedios;
    double [] desviaciones;
    double [] cuantosJugaron;
    int numExp;
    public boolean ganoAlguien;
    int registrosExtra;
    public ServidorJuego() 
    {
        promedios = new double[10]; //Vamos a repetir cada experimento 10 veces
        desviaciones = new double[10]; 
        cuantosJugaron = new double[10];
        numExp = 0;
        registrosExtra = 0;
        reiniciaServidor();
    }
    public int getRegistrosExtra()
    {
        return registrosExtra;
    }
    public void registroExtra()
    {
        registrosExtra++;
    }
    public void reportaExperimento()
    {
        long p = 0, d = 0, j = 0;
        for(int i = 0; i < numExp; i++)
        {
            p+= promedios[i];
            d += desviaciones[i];
            j += cuantosJugaron[i];
        }
        System.out.println("Promedio: "+ p*(1.0)/(numExp*1.0));
        System.out.println("Desv. Est.: "+ d*(1.0)/(1.0*numExp));
        System.out.println("Jugaron: "+ j*1.0/(1.0*numExp));
    }
    public void acumulaPromediosYDesviaciones()
    {
        promedios[numExp] = promedio();
        desviaciones[numExp] = (long) stdDev();
        cuantosJugaron[numExp] = cuantos;
        numExp++;
    }
    public void reiniciaManteniendoJugadores()
    {
        try {
            tiempos = 0;
            cuantos = 0;
            tes = new double[maxJugadores];
            ganoAlguien = false;
            registrosExtra = 0;
            Thread.sleep(1000);
            System.out.println("\nServidor reiniciado y listo para comenzar.");
        } catch (InterruptedException ex) {
            Logger.getLogger(ServidorJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void reiniciaServidor()
    {
        try {
            numeroJugadores = 0;
            players = new Jugador[maxJugadores];
            puntoDado = false;
            puntosParaGanar = 5;
            segundosEntreTopo = 200;
            tiempos = 0;
            cuantos = 0;
            tes = new double[maxJugadores];
            ganoAlguien = false;
            Thread.sleep(1000);
            System.out.println("\nServidor reiniciado y listo para comenzar.");
        } catch (InterruptedException ex) {
            Logger.getLogger(ServidorJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public double stdDev()
    {
        double sum = 0.0;
        double num = 0.0;

        for (int i=0; i < cuantos; i++)
        sum+=tes[i];

        double mean = sum/cuantos;
        for (int i=0; i <cuantos; i++)
        num+=Math.pow((tes[i] - mean),2);
        return Math.sqrt(num/cuantos);
    }
    public double promedio()
    {
        return tiempos/cuantos*(1.0);
    }
    public void acumula(long tiempo)
    {
        tes[cuantos] = tiempo;
        tiempos += tiempo;
        cuantos++;
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
        for (Jugador p : players) {
            if (p != null) {
                System.out.println(p.getNombre() + ": Jugador " + p.getId());
            }
        }
    }

    public boolean alguienYaGano() {
        boolean r = false;
        int i = 0;
        while (!r && i < numeroJugadores && players[i] != null) {
            if (players[i].getPuntos() == puntosParaGanar) {
                r = true;
            }
            i++;
        }
        return r;
    }

    public Jugador buscaJugador(String nombre) {
        Jugador j = null;
        int i = 0;
        boolean encontrado = false;

        while (!encontrado && i < numeroJugadores && players[i] != null) {
            if (players[i].getNombre().equals(nombre)) {
                j = players[i];
                encontrado = true;
            }
            i++;
        }
        return j;
    }

    public String obtenerGanador() {
        ganoAlguien = true;
        StringBuilder resp = new StringBuilder();
        resp.append("¡Ganó ");
        int i = 0;

        while (i < numeroJugadores && players[i] != null) {
            if (players[i].getPuntos() == puntosParaGanar) {
                resp.append(players[i].getNombre());
            }
            i++;
        }
        resp.append("!");
        return resp.toString();
    }
    public int getCuantos()
    {
        return cuantos;
    }
    public String muestraTablero() {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < numeroJugadores && players[i] != null) {
            sb.append(players[i].toString());
            sb.append("-");
            i++;
        }

        return sb.toString();
    }

    public void reiniciaTablero() {
        int i = 0;
        int jugadoresActivos = 0;
        
        Jugador aux;
        
        while(i < players.length) {
            if(players[i] != null) {
                aux = players[i];
                players[i] = null;
                if (aux.estaActivo()) {
                    aux.reseteaPuntuacion();
                    players[jugadoresActivos] = aux;
                    jugadoresActivos++;
                }
            }
            i++;
        }

        while (i < numeroJugadores && players[i] != null) {
            players[i].reseteaPuntuacion();
            i++;
        }
    }

    public static void main(String args[]) throws InterruptedException {

        ServidorJuego servJuego = new ServidorJuego();
        Tablero t = new Tablero(servJuego);
        t.start(); // Hilo para enviar constantemente tableros cambiantes.

        try {
            int serverPortRegister = 2806; // Puerto conexiones registro
            int serverPort = 7896; // Puerto conexiones TCP para jugar
            ServerSocket listenSocket = new ServerSocket(serverPort);
            ServerSocket listenSocketRegister = new ServerSocket(serverPortRegister);

            while (true) {
                //System.out.println("Waiting for players...");

                Socket registerSocket = listenSocketRegister.accept();
                Registro reg = new Registro(registerSocket, servJuego);
                reg.start();

                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 

                //Registrar a un jugador o crear uno nuevo
                Jugador j = (reg.getJugadorRegistrado());

                System.out.println(j.toString());

                Connection c = new Connection(clientSocket, servJuego, j);

                //servJuego.nuevoJugador(j);
                //servJuego.obtenerListaJugadores(); // Prueba para ver que se añagen jugadores.

                c.start();
            }

        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }

    }
}
