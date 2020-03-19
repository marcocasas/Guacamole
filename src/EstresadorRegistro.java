
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
        
    }
    
    @Override
    public void run() {
        
        int numeroCaracteresUsuario = 8;
        
        String mca = null;
        int mcp = 0;
        String tcpa = null;
        int tcpp = 0;

        //System.out.println("Escribe tu nombre para jugar:");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String nombreJugador = EstresadorRegistro.generateRandomString(numeroCaracteresUsuario);
        System.out.println("Intenta registro jugador " + nombreJugador);
//        try {
//            nombreJugador = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Socket s = null;
        try {
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
                    System.out.println("Closed socket REGISTRO");
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
        
    }
    
    public static void main(String args[]) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
           (new EstresadorRegistro()).start();
        }
    }

}
