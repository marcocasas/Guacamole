import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

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

    public GameInt() {
        dialog.setLayout(new GridLayout(4,1));
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
        
        JRadioButton[] posiciones = {pos0,pos1,pos2,pos3,pos4,pos5,pos6,pos7,pos8};
        
        while (runProcess) {
        
            MulticastSocket s = null;
            try {
                InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group; misma direccion que el sender
	    	s = new MulticastSocket(6789);
	   	s.joinGroup(group); 

	    	byte[] buffer = new byte[1000];
 	   	//for(int i=0; i< 3; i++) {
                    //System.out.println("Waiting for messages");
                DatagramPacket messageIn = 
                    new DatagramPacket(buffer, buffer.length);
                
                while(puntos < 50) {
                    s.receive(messageIn);
                    //System.out.println("Fecha actualizada: " + new String(messageIn.getData())+ " de: "+ messageIn.getAddress());
                    //}
                    //System.out.println("Tablero actual: ");

                    posiciones[prevPos].setText("(   )");
                    
                    byte[] m = messageIn.getData();                    
                    
                    for (int i = 0; i<9; i++) {                        
                        if(m[i] == 1) {
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
            }
            catch (SocketException e){
                System.out.println("Socket: " + e.getMessage());
            }
            catch (IOException e){
                System.out.println("IO: " + e.getMessage());
            }
            finally {
               if(s != null) s.close(); //Cerramos para liberar recursos del serbiddor.
           }

      runProcess = false;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JRadioButton pressed = (JRadioButton)e.getSource();
    if (pressed == pos0) {
        System.out.println("0");
        pos0.setSelected(false);
    } else if (pressed == pos1) {
        System.out.println("1");
        pos1.setSelected(false);
    } else if (pressed == pos2) {
        System.out.println("2");
        pos2.setSelected(false);
    } else if (pressed == pos3) {
        System.out.println("3");
        pos3.setSelected(false);
    } else if (pressed == pos4) {
        System.out.println("4");
        pos4.setSelected(false);
    } else if (pressed == pos5) {
        System.out.println("5");
        pos5.setSelected(false);
    } else if (pressed == pos6) {
        System.out.println("6");
        pos6.setSelected(false);
    } else if (pressed == pos7) {
        System.out.println("7");
        pos7.setSelected(false);
    } else if (pressed == pos8) {
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