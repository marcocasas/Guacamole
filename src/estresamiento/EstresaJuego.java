package estresamiento;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class EstresaJuego extends Thread {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();
    long spentTime = 0, startTime;
    
    boolean salir = false;

    int mcPort, tcpPort;
    String mcAddress, tcpAddress;

    int number = 0;
    
    String messageStr = "Topo en la posicion: ";
    boolean runProcess = true, puedeTirar;
    long auxT1, auxT2, dif;
    
    private DataOutputStream out;
    private DataInputStream in;
    Socket stcp;
    MulticastSocket sMulticast;
    InetAddress group;
    ServidorJuego servidor;
    public EstresaJuego(ServidorJuego s) 
    {
        puedeTirar = false;
        auxT1 = 0; auxT2 = 0;
        auxT1 = System.currentTimeMillis();
        dif = ((long) Math.random()*1000 + 500);
        servidor = s;
    }
    public void juega()
    {
        new Thread(this).start();
    }
    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

			// 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);

        }

        return sb.toString();

    }
    public void salir()
    {
        try {
            //System.out.println("SALIR!!!");
            out.writeUTF("EXT");
            salir = true;
        } catch (IOException ex) {
            Logger.getLogger(EstresaJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    @Override
    public void run() {
        int prevPos = 0;

        int serverPort = tcpPort; //puerto servidor TCP
        while (runProcess) {
            try {
                byte[] buffer = new byte[1000];

                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                
                while (!salir) {
                    //System.out.println("¡Nuevo juego!");
                    boolean juegoTerminado = false;
                    
                    while (!salir && !juegoTerminado) {
                        if(auxT2 - auxT1 > dif)
                        {
                            puedeTirar = true;
                            auxT1 = System.currentTimeMillis();
                            dif = ((long) Math.random()*100 + 100);
                        }
                        sMulticast.receive(messageIn);
                        byte[] m = messageIn.getData();
                        if (m[9] == 1) {
                            juegoTerminado = true;
                            sMulticast.receive(messageIn);
                            //System.out.println((new String(messageIn.getData())));
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(EstresaJuego.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            for (int i = 0; i < 9; i++) {
                                if (m[i] == 1) {
                                    prevPos = i;
                                    //System.out.println("Topo en: " + i);//posiciones[i].setText("ʕ•.•ʔ");
                                }
                            }
                        }
                        if(puedeTirar)
                        {
                            int x = ((int) (Math.random()*8));
                            puedeTirar = false;
                            //timer.setDelay(((int) Math.random()*1000 + 500));
                            //System.out.println("Tirando en "+ x);
                            //Empieza codigo de tiro                            
                            tira(x);
                        }
                        auxT2 = System.currentTimeMillis();
                    }

                    //System.out.println("Juego terminado");
                    //System.out.println("Usuario pide salir...");
                }

                sMulticast.leaveGroup(group);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } finally {
                //timer.stop();
                if (sMulticast != null) {
                    sMulticast.close(); // Cerramos para liberar recursos del servidor.
                }
                if (stcp != null) {
                    try {
                        stcp.close();
                    } catch (IOException e) {
                        System.out.println("close:" + e.getMessage());
                    }
                }
                
                runProcess = false;
            }
        }
        
        System.exit(0);
        
    }
    public void tira(int donde)
    {
        startTime = System.currentTimeMillis();
        try {
                out.writeUTF("" + donde);
                String data = in.readUTF();
                //System.out.println("Received:");
                //System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                //System.out.println(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(EstresaJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
            spentTime = System.currentTimeMillis() - startTime;
            try
            {
            servidor.acumula(spentTime);
            }
            catch(Exception e)
            {
            }
    }
    public void registra()
    {
        String mca = null;
        int mcp = 0;
        String tcpa = null;
        int tcpp = 0;

        String nombreJugador = EstresadorRegistro.generateRandomString(8);

        Socket s = null;
        try {
            int serverPort = 2806;

            s = new Socket("localhost", serverPort);
            DataInputStream inLoc = new DataInputStream(s.getInputStream());
            DataOutputStream outLoc = new DataOutputStream(s.getOutputStream());
            outLoc.writeUTF(nombreJugador);

            String data = inLoc.readUTF();

            String strArray[] = data.split("-");

            mca = strArray[0];
            mcp = Integer.parseInt(strArray[1]);
            tcpa = strArray[2];
            tcpp = Integer.parseInt(strArray[3]);
            //Aqui acaba registro
            
            mcAddress = mca;
            mcPort = mcp;
            tcpAddress = tcpa;
            tcpPort = tcpp;
            
            stcp = null; //socket tcp

            sMulticast = null; //socket multicast

            group = InetAddress.getByName(mcAddress); // destination multicast group; misma direccion que el sender
            sMulticast = new MulticastSocket(mcPort);
            sMulticast.joinGroup(group);
        
            stcp = new Socket(tcpAddress, tcpPort);

            in = new DataInputStream(stcp.getInputStream());
            out = new DataOutputStream(stcp.getOutputStream());
            
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
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }

    public static void main(String args[]) throws InterruptedException
    {
        
        ServidorEstres s = new ServidorEstres();
        s.start();
        s.setSoloRegistro(false);
        
        int clientes = 500;
        int fallos = 0;
        
        Thread.sleep(1000); //Le damos tiempo a que el servidor este listo
        
        EstresaJuego[] hilos = new EstresaJuego[clientes];
        for (int i = 0; i < clientes; i++) {
            hilos[i] = new EstresaJuego(s.servJuego);
            hilos[i].registra();
        }
        System.out.println("Clientes Listos.");
        Thread.sleep(1000);
        for (int i = 0; i < clientes; i++) {
            hilos[i].juega();
        }
        while (!s.servJuego.ganoAlguien) {
            Thread.sleep(10);
        }

    }
}
