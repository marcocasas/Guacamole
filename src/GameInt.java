
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameInt implements Runnable, ActionListener {

    boolean salir = false;

    int mcPort, tcpPort;
    String mcAddress, tcpAddress;

    JDialog dialog = new JDialog();
    int number = 0;
    JLabel message = new JLabel();
    JRadioButton pos0 = new JRadioButton("(   )"), pos1 = new JRadioButton("(   )");
    JRadioButton pos2 = new JRadioButton("(   )"), pos3 = new JRadioButton("(   )");
    JRadioButton pos4 = new JRadioButton("(   )"), pos5 = new JRadioButton("(   )");
    JRadioButton pos6 = new JRadioButton("(   )"), pos7 = new JRadioButton("(   )");
    JRadioButton pos8 = new JRadioButton("(   )");

    JButton getOut = new JButton("Salir");

    String messageStr = "Topo en la posicion: ";
    boolean runProcess = true;

    private DataOutputStream out;
    private DataInputStream in;

    public GameInt(String mca, int mcp, String tcpa, int tcpp) {

        mcAddress = mca;
        mcPort = mcp;
        tcpAddress = tcpa;
        tcpPort = tcpp;

        dialog.setLayout(new GridLayout(4, 1));
        pos0.addActionListener(this);
        pos1.addActionListener(this);
        pos2.addActionListener(this);
        pos3.addActionListener(this);
        pos4.addActionListener(this);
        pos5.addActionListener(this);
        pos6.addActionListener(this);
        pos7.addActionListener(this);
        pos8.addActionListener(this);

        getOut.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //System.out.println("SALIR!!!");
                    out.writeUTF("EXT");
                    salir = true;
                } catch (IOException ex) {
                    Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        JPanel btnPanel = new JPanel();
        JPanel btnPanel2 = new JPanel();
        JPanel btnPanel3 = new JPanel();
        JPanel msgPanel = new JPanel();

        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel3.setLayout(new FlowLayout(FlowLayout.CENTER));
        msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        btnPanel.add(pos0);
        btnPanel.add(pos1);
        btnPanel.add(pos2);
        btnPanel2.add(pos3);
        btnPanel2.add(pos4);
        btnPanel2.add(pos5);
        btnPanel3.add(pos6);
        btnPanel3.add(pos7);
        btnPanel3.add(pos8);

        msgPanel.add(message);
        msgPanel.add(getOut);

        //dialog.add(message, "Center");
        dialog.add(btnPanel, "Center");
        dialog.add(btnPanel2, "Center");//
        dialog.add(btnPanel3, "Center");//
        dialog.add(msgPanel, "Center");

        dialog.setPreferredSize(new Dimension(400, 400));

        dialog.pack();

        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        new Thread(this).start();
    }

    @Override
    public void run() {
        int prevPos = 0;

        int serverPort = tcpPort; //puerto servidor TCP
        Socket stcp = null; //socket tcp

        MulticastSocket s = null; //socket multicast

        JRadioButton[] posiciones = {pos0, pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};

        while (runProcess) {
            try {
                InetAddress group = InetAddress.getByName(mcAddress); // destination multicast group; misma direccion que el sender
                s = new MulticastSocket(mcPort);
                s.joinGroup(group);

                byte[] buffer = new byte[1000];

                DatagramPacket messageIn
                        = new DatagramPacket(buffer, buffer.length);

                stcp = new Socket(tcpAddress, serverPort);

                in = new DataInputStream(stcp.getInputStream());
                this.out = new DataOutputStream(stcp.getOutputStream());

                while (!salir) {
                    System.out.println("¡Nuevo juego!");
                    message.setText("¡Un nuevo juego ha comenzado!");
                    boolean juegoTerminado = false;

                    while (!salir && !juegoTerminado) {
                        s.receive(messageIn);

                        posiciones[prevPos].setText("(   )");

                        byte[] m = messageIn.getData();

                        if (m[9] == 1) {
                            juegoTerminado = true;
                            s.receive(messageIn);
                            message.setText(new String(messageIn.getData()));
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            for (int i = 0; i < 9; i++) {
                                if (m[i] == 1) {
                                    prevPos = i;
                                    posiciones[i].setText("ʕ•.•ʔ");
                                }
                            }
                        }
                    }

                    System.out.println("Juego terminado");
                    System.out.println("Usuario pide salir...");
                }

                s.leaveGroup(group);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } finally {
                if (s != null) {
                    s.close(); // Cerramos para liberar recursos del servidor.
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

    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButton pressed = (JRadioButton) e.getSource();
        if (pressed == pos0) {
            try {
                out.writeUTF("0");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("0");
            pos0.setSelected(false);
        } else if (pressed == pos1) {
            try {
                out.writeUTF("1");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("1");
            pos1.setSelected(false);
        } else if (pressed == pos2) {
            try {
                out.writeUTF("2");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("2");
            pos2.setSelected(false);
        } else if (pressed == pos3) {
            try {
                out.writeUTF("3");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("3");
            pos3.setSelected(false);
        } else if (pressed == pos4) {
            try {
                out.writeUTF("4");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("4");
            pos4.setSelected(false);
        } else if (pressed == pos5) {
            try {
                out.writeUTF("5");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("5");
            pos5.setSelected(false);
        } else if (pressed == pos6) {
            try {
                out.writeUTF("6");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("6");
            pos6.setSelected(false);
        } else if (pressed == pos7) {
            try {
                out.writeUTF("7");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("7");
            pos7.setSelected(false);
        } else if (pressed == pos8) {
            try {
                out.writeUTF("8");
                String data = in.readUTF();
                System.out.println("Received:");
                System.out.println(data);

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                String str[] = data.split("-");
                for (String s : str) {
                    sb.append(s);
                    sb.append("<br>");
                }
                sb.append("</html>");
                System.out.println(sb.toString());
                message.setText(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(GameInt.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("8");
            pos8.setSelected(false);
        } else {
            dialog.dispose();
        }
    }

    public static void main(String args[]) {

        String mca = null;
        int mcp = 0;
        String tcpa = null;
        int tcpp = 0;

        System.out.println("Escribe tu nombre para jugar:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String nombreJugador = null;
        try {
            nombreJugador = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }

        GameInt ean = new GameInt(mca, mcp, tcpa, tcpp);
    }
}
