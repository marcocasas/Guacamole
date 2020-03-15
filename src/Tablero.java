
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marco
 */
public class Tablero extends Thread {

    ServidorJuego sj;

    MulticastSocket s = null; // Socket para Multicast.
    int serverPort = 7896; // Puerto Multicast

    byte[] posiciones = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Tablero actual
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

                while (!sj.alguienYaGano()) {
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

                    Thread.sleep(sj.getSegundosEntreTopo());
                    System.out.println("Va de nuez");
                }

                posiciones[prevPos] = 0;
                posiciones[9] = 1;
                DatagramPacket messageOut
                        = new DatagramPacket(posiciones, posiciones.length, group, 6789);
                s.send(messageOut);
                System.out.println("¡Tenemos un(a) ganador(a)!");

                System.out.println(sj.obtenerGanador());

                byte[] fin = sj.obtenerGanador().getBytes();
                messageOut = new DatagramPacket(fin, fin.length, group, 6789);
                s.send(messageOut);
                
                sj.reiniciaTablero();
                posiciones[9] = 0;

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
