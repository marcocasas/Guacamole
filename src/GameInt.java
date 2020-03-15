
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameInt implements Runnable, ActionListener {

    JDialog dialog = new JDialog();
    int number = 0;
    JLabel message = new JLabel();
    JRadioButton pos0 = new JRadioButton("(   )"), pos1 = new JRadioButton("(   )");
    JRadioButton pos2 = new JRadioButton("(   )"), pos3 = new JRadioButton("(   )");
    JRadioButton pos4 = new JRadioButton("(   )"), pos5 = new JRadioButton("(   )");
    JRadioButton pos6 = new JRadioButton("(   )"), pos7 = new JRadioButton("(   )");
    JRadioButton pos8 = new JRadioButton("(   )");

    String messageStr = "Topo en la posicion: ";
    boolean runProcess = true;

    private DataOutputStream out;
    private DataInputStream in;

    public GameInt() {
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

        JPanel btnPanel = new JPanel();
        JPanel btnPanel2 = new JPanel();
        JPanel btnPanel3 = new JPanel();

        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel3.setLayout(new FlowLayout(FlowLayout.CENTER));

        btnPanel.add(pos0);
        btnPanel.add(pos1);
        btnPanel.add(pos2);
        btnPanel2.add(pos3);
        btnPanel2.add(pos4);
        btnPanel2.add(pos5);
        btnPanel3.add(pos6);
        btnPanel3.add(pos7);
        btnPanel3.add(pos8);

        dialog.add(message, "Center");
        dialog.add(btnPanel, "Center");
        dialog.add(btnPanel2, "Center");//
        dialog.add(btnPanel3, "Center");//

        dialog.setPreferredSize(new Dimension(300, 300));

        dialog.pack();

        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        new Thread(this).start();
    }

    @Override
    public void run() {
        int puntos = 0;
        int prevPos = 0;

        int serverPort = 7896; //puerto servidor TCP
        Socket stcp = null; //socket tcp

        MulticastSocket s = null; //socket multicast

        JRadioButton[] posiciones = {pos0, pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};

        while (runProcess) {
            try {
                InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group; misma direccion que el sender
                s = new MulticastSocket(6789);
                s.joinGroup(group);

                byte[] buffer = new byte[1000];
                //for(int i=0; i< 3; i++) {
                //System.out.println("Waiting for messages");
                DatagramPacket messageIn
                        = new DatagramPacket(buffer, buffer.length);

                stcp = new Socket("localhost", serverPort);
                //   s = new Socket("127.0.0.1", serverPort);    
                in = new DataInputStream(stcp.getInputStream());
                this.out = new DataOutputStream(stcp.getOutputStream());

                while (puntos < 50) {
                    s.receive(messageIn);
                    //System.out.println("Fecha actualizada: " + new String(messageIn.getData())+ " de: "+ messageIn.getAddress());
                    //}
                    //System.out.println("Tablero actual: ");

                    posiciones[prevPos].setText("(   )");

                    byte[] m = messageIn.getData();

                    for (int i = 0; i < 9; i++) {
                        if (m[i] == 1) {
                            prevPos = i;
                            posiciones[i].setText("ʕ•.•ʔ");
                            message.setText(messageStr + " " + i + "");
                        }
                    }

                    //System.out.println("\n");
                    puntos++;
                    //
                }

                s.leaveGroup(group);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } finally {
                if (s != null) {
                    s.close(); // Cerramos para liberar recursos del serbiddor.
                }
                if (stcp != null) {
                    try {
                        stcp.close();
                    } catch (IOException e) {
                        System.out.println("close:" + e.getMessage());
                    }
                }
            }
        }

        runProcess = false;
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
        GameInt ean = new GameInt();
    }
}
