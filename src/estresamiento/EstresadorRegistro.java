package estresamiento;

import java.io.*;
import static java.lang.Math.random;
import java.security.SecureRandom;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

public class EstresadorRegistro extends Thread {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();
    long spentTime = 0, startTime;
    boolean conecto;
    
    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

			// 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            // System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);

        }

        return sb.toString();

    }

    public EstresadorRegistro() {
        conecto = false;
    }
    public boolean logroConectar()
    {
        return conecto;
    }
    @Override
    public void run() {
        
        int numeroCaracteresUsuario = 8;
        String mca = null;
        int mcp = 0;
        String tcpa = null;
        int tcpp = 0;

        String nombreJugador = EstresadorRegistro.generateRandomString(numeroCaracteresUsuario);
        //System.out.println("Intenta registro jugador " + nombreJugador);
        Socket s = null;
        
        try 
        {
            startTime = System.currentTimeMillis();
            
            int serverPort = 2806;
            s = new Socket("localhost", serverPort);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(nombreJugador);

            String data = in.readUTF();

            String strArray[] = data.split("-");

            mca = strArray[0];
            mcp = Integer.parseInt(strArray[1]);
            tcpa = strArray[2];
            tcpp = Integer.parseInt(strArray[3]);
            spentTime = System.currentTimeMillis() - startTime;
            conecto = true;
            
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                    //System.out.println("T: "+ spentTime);
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
        
    }
    
    public static void main(String args[]) throws InterruptedException {
        ServidorEstres s = new ServidorEstres();
        s.start();
        
        int registros = 500;
        int fallos = 0;
        
        Thread.sleep(1000);
        
        for(int k = 0; k < 10; k++) //10 repeticiones por experimento
        {
            EstresadorRegistro [] hilos = new EstresadorRegistro[registros];
            for (int i = 0; i < registros; i++) 
            {
                hilos[i] = new EstresadorRegistro();
                hilos[i].start();
            }
            Thread.sleep(500);
            for (int i = 0; i < registros; i++) 
            {
                if(hilos[i].logroConectar())
                    s.servJuego.acumula(hilos[i].spentTime);
                else
                    fallos++;
            }

            Thread.sleep(1000);
            
            System.out.println("\nCorrida " + (k+1) + " intentando " + registros + " registros.");
            System.out.println("Jugadores conectados: " + s.servJuego.getCuantos());
            System.out.println("Promedio: " + s.servJuego.promedio());
            System.out.println("Desv. Est.: " + s.servJuego.stdDev());
            
            Thread .sleep(500);
            
            s.servJuego.acumulaPromediosYDesviaciones();
            
            Thread .sleep(500);
            
            s.servJuego.reiniciaServidor();
        }
        System.out.println("\n\nResultado de experimento intentando " + registros + " registros.");
        s.servJuego.reportaExperimento();
    }

}
