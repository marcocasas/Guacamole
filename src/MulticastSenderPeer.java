/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author JGUTIERRGARC
 */
public class MulticastSenderPeer {
    	public static void main(String args[]) throws InterruptedException{ 
  	 
	MulticastSocket s =null;
   	 try {
                
                InetAddress group = InetAddress.getByName("228.28.6.13"); // destination multicast group 
	    	s = new MulticastSocket(6789); //Puerto en el que me estoy uniendo al grupo.
	   	s.joinGroup(group); 
                //s.setTimeToLive(10);
                System.out.println("Messages' TTL (Time-To-Live): " + s.getTimeToLive());
                //String myMessage="Hello";
                //String currentDate;
                
                int pos = 0; //Posicion en la que saldra el topo. Será el indice del arreglo
                int prevPos; //Posicion previa
                byte[] posiciones = {0,0,0,0,0,0,0,0,0};
                
                while(true) {
                    //currentDate = (new Date()).toString();
                    //byte [] m = myMessage.getBytes();
                    prevPos = pos;
                    pos = new Random().nextInt(9);
                    System.out.println("Topo en " + pos);
                    posiciones[prevPos] = 0;
                    posiciones[pos] = 1;
                    //byte [] m = posiciones;
                    DatagramPacket messageOut = 
                            new DatagramPacket(posiciones, posiciones.length, group, 6789);
                    s.send(messageOut);
                    
                    Thread.sleep(3000);
                }
	    	//s.leaveGroup(group);		
 	    }
         catch (SocketException e){
             System.out.println("Socket: " + e.getMessage());
	 }
         catch (IOException e){
             System.out.println("IO: " + e.getMessage());
         }
	 finally {
            if(s != null) s.close();
        }
    }		     
}
