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

    Jugador players[] = new Jugador[50]; // Arreglo de jugadores
    int posicionTopo;

    public void setPosicionTopo(int i) {
        posicionTopo = i;
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
                Connection c = new Connection(clientSocket, servJuego);
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

    public Tablero(ServidorJuego s) {
        sj = s;
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
                System.out.println("Topo en " + pos);
                posiciones[prevPos] = 0;
                posiciones[pos] = 1;
                DatagramPacket messageOut
                        = new DatagramPacket(posiciones, posiciones.length, group, 6789);
                s.send(messageOut);

                sj.setPosicionTopo(pos);

                Thread.sleep(3500);
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

    public Connection(Socket aClientSocket, ServidorJuego sj) {

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
            String data;
            while(true) {
                data = in.readUTF();
                System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress());
                System.out.println("Un jugador tiró: " + data);
                out.writeUTF(data);
            }
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
