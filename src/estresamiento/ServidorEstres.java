package estresamiento;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;

/**
 *
 */

public class ServidorEstres extends Thread{
    public ServidorJuego servJuego;
    boolean running, soloRegistro;
    public ServidorEstres()
    {
        servJuego = null;
        running = true;
        soloRegistro = true;
    }
    public void setSoloRegistro(boolean como)
    {
        soloRegistro = como;
    }
    @Override
    public void run(){

        servJuego = new ServidorJuego();
        Tablero t = new Tablero(servJuego);
        t.start(); // Hilo para enviar constantemente tableros cambiantes.

        try {
            int serverPortRegister = 2806; // Puerto conexiones registro
            int serverPort = 7896; // Puerto conexiones TCP para jugar
            ServerSocket listenSocket = new ServerSocket(serverPort);
            ServerSocket listenSocketRegister = new ServerSocket(serverPortRegister);

            while (running) {
                //System.out.println("Waiting for players...");
                Socket registerSocket = listenSocketRegister.accept();
                Registro reg = new Registro(registerSocket, servJuego);
                reg.start();
                if(!soloRegistro)
                {
                    Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
                    //Registrar a un jugador o crear uno nuevo
                    Jugador j = (reg.getJugadorRegistrado());

                    //System.out.println(j.toString());

                    Connection c = new Connection(clientSocket, servJuego, j);

                    //servJuego.obtenerListaJugadores(); // Prueba para ver que se añagen jugadores.

                    c.start();
                }
            }

        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }

    }
}
