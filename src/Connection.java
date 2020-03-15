
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marco
 */
public class Connection extends Thread {

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
            boolean salir = false;
            while (!salir) {
                data = in.readUTF();

                if (data.equals("EXT")) {
                    salir = true;
                    player.marcarJugadorInactivo();
                    System.out.println(player.getNombre() + " ahora está " + player.estaActivo());
                } else {
                    if (!s.getPuntoDado() && Integer.parseInt(data) == s.getPosicionTopo()) {
                        s.setPuntoDado(true);
                        player.sumaPuntos();
                    }

                    out.writeUTF(s.muestraTablero());

                    System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress());
                    System.out.println("Un jugador tiró: " + data);
                }

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
