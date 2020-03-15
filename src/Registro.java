
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
public class Registro extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    ServidorJuego sj;
    String jugadorRegistrado;
    Jugador j;

    public Registro(Socket aClientSocket, ServidorJuego serv) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            sj = serv;
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public Jugador getJugadorRegistrado() {
        return j;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String nombreJugador;
                nombreJugador = in.readUTF();

                this.j = sj.buscaJugador(nombreJugador);

                if (this.j == null) {
                    System.out.println("Nuevo Jugador");
                    this.j = new Jugador(sj.getNumeroJugadores(), nombreJugador);
                    sj.nuevoJugador(this.j);
                } else {
                    System.out.println("Bienvenido de nuevo:");
                    System.out.println(j.getNombre());
                }

                this.j.marcarJugadorActivo();

                jugadorRegistrado = nombreJugador;
                out.writeUTF("228.28.6.13-6789-127.0.0.1-7896");
                System.out.println("Jugador registrado");
                sj.obtenerListaJugadores();

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
