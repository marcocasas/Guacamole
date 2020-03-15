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
    boolean puntoDado;
    
    public ServidorJuego() {
        numeroJugadores = 0;
        players = new Jugador[10];
        puntoDado = false;
        puntosParaGanar = 5;
    }

    public void setPosicionTopo(int i) {
        posicionTopo = i;
    }
    
    public void setPuntoDado(boolean b) {
        puntoDado = b;
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

    public String muestraTablero() {
        StringBuilder sb = new StringBuilder();
        
        int i = 0;
        while(i < numeroJugadores && players[i] != null) {
            sb.append(players[i].toString());
            sb.append("\n");
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

class Tablero extends Thread {

    ServidorJuego sj;

    MulticastSocket s = null; // Socket para Multicast.
    int serverPort = 7896; // Puerto Multicast

    byte[] posiciones = {0, 0, 0, 0, 0, 0, 0, 0, 0}; // Tablero actual
    int pos = 0; // Posicion en la que saldra el topo. Será el indice del arreglo
    int prevPos; // Posicion previa en la que estaba el topo.

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Tablero(ServidorJuego servJuego) {
        sj = servJuego;
    }

    @Override
    public void run() {

        MulticastSocket s = null;

        try {
            InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group 
            s = new MulticastSocket(6789); // Puerto en el que me estoy uniendo al grupo.
            s.joinGroup(group);

            while (true) {
                prevPos = pos;
                pos = new Random().nextInt(9);
                sj.setPosicionTopo(pos);
                sj.setPuntoDado(false);
                System.out.println("Topo en " + pos);
                posiciones[prevPos] = 0;
                posiciones[pos] = 1;
                DatagramPacket messageOut
                        = new DatagramPacket(posiciones, posiciones.length, group, 6789);
                s.send(messageOut);

                sj.setPosicionTopo(pos);

                Thread.sleep(5000);
                System.out.println("Va de nuez");
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(Tablero.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    
    ServidorJuego s;
    Jugador player;
    
    public Connection(Socket aClientSocket, ServidorJuego sj, Jugador j) {
        s = sj;
        player = j;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {			                 // an echo server
            String data = "START";
            while(!data.equals("EXT")) {
                data = in.readUTF();
                
                if(!s.getPuntoDado() && Integer.parseInt(data) == s.getPosicionTopo()) {
                    s.setPuntoDado(true);
                    player.sumaPuntos();
                    //out.writeUTF("Punto!");
                } 
                //else {
                    //out.writeUTF("Be faster!");
                //}
                
                out.writeUTF(s.muestraTablero());
                
                System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress());
                System.out.println("Un jugador tiró: " + data);
            }
            
            // Sacar al jugador del servidor.
            
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
