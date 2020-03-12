/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;
import java.util.Random;

/**
 *
 * @author
 */
public class MulticastSenderPeer {

    MulticastSocket s = null;
    int serverPort = 7896;
    int pos = 0; //Posicion en la que saldra el topo. Será el indice del arreglo
    int prevPos; //Posicion previa
    byte[] posiciones = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    
    public static void main(String args[]) throws InterruptedException {
        
        MulticastSenderPeer servJuego = new MulticastSenderPeer();
        
        MulticastSocket s = null;
        try {

            InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group 
            servJuego.s = new MulticastSocket(6789); //Puerto en el que me estoy uniendo al grupo.
            servJuego.s.joinGroup(group);

            //int serverPort = 7896; //TCP
            ServerSocket listenSocket = new ServerSocket(servJuego.serverPort); //TCP

//            int pos = 0; //Posicion en la que saldra el topo. Será el indice del arreglo
//            int prevPos; //Posicion previa
//            byte[] posiciones = {0, 0, 0, 0, 0, 0, 0, 0, 0};

            while (true) {
                System.out.println("Waiting for players...");
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
                Connection c = new Connection(clientSocket, new ServidorJuego()); // ESTA MAL BORRAAAAAR
                c.start();
                //}

                //s.setTimeToLive(10);
                //System.out.println("Messages' TTL (Time-To-Live): " + s.getTimeToLive());
                //String myMessage="Hello";
                //String currentDate;
                //while(true) {
                //currentDate = (new Date()).toString();
                //byte [] m = myMessage.getBytes();
                servJuego.prevPos = servJuego.pos;
                servJuego.pos = new Random().nextInt(9);
                System.out.println("Topo en " + servJuego.pos);
                servJuego.posiciones[servJuego.prevPos] = 0;
                servJuego.posiciones[servJuego.pos] = 1;
                //byte [] m = posiciones;
                DatagramPacket messageOut
                        = new DatagramPacket(servJuego.posiciones, servJuego.posiciones.length, group, 6789);
                servJuego.s.send(messageOut);

                Thread.sleep(3000);
                System.out.println("Va de nuez");
            }
            //s.leaveGroup(group);		
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
