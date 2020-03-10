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
 * @author JGUTIERRGARC
 */
public class MulticastSenderPeer {

    public static void main(String args[]) throws InterruptedException {

        MulticastSocket s = null;
        try {

            InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group 
            s = new MulticastSocket(6789); //Puerto en el que me estoy uniendo al grupo.
            s.joinGroup(group);

            int serverPort = 7896; //TCP
            ServerSocket listenSocket = new ServerSocket(serverPort); //TCP

            int pos = 0; //Posicion en la que saldra el topo. Será el indice del arreglo
            int prevPos; //Posicion previa
            byte[] posiciones = {0, 0, 0, 0, 0, 0, 0, 0, 0};

            while (true) {
                System.out.println("Waiting for players...");
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
                Connection c = new Connection(clientSocket);
                c.start();
                //}

                //s.setTimeToLive(10);
                //System.out.println("Messages' TTL (Time-To-Live): " + s.getTimeToLive());
                //String myMessage="Hello";
                //String currentDate;
                //while(true) {
                //currentDate = (new Date()).toString();
                //byte [] m = myMessage.getBytes();
                prevPos = pos;
                pos = new Random().nextInt(9);
                System.out.println("Topo en " + pos);
                posiciones[prevPos] = 0;
                posiciones[pos] = 1;
                //byte [] m = posiciones;
                DatagramPacket messageOut
                        = new DatagramPacket(posiciones, posiciones.length, group, 6789);
                s.send(messageOut);

                Thread.sleep(3000);
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

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
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
            String data = in.readUTF();
            System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress());
            out.writeUTF(data);
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
