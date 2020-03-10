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

/**
 *
 * @author JGUTIERRGARC
 */
public class MulticastReceivingPeer {
    	public static void main(String args[]){ 
  	 
            int puntos = 0;
            
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
                
                while(puntos < 11) {
                    s.receive(messageIn);
                    //System.out.println("Fecha actualizada: " + new String(messageIn.getData())+ " de: "+ messageIn.getAddress());
                    //}
                    System.out.println("Tablero actual: ");

                    byte[] m = messageIn.getData();

                    for (int i = 0; i<10; i++) {
                        System.out.print(m[i]);
                    }
                
                    System.out.println("\n");
                    
                    puntos++;
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
    }		     
                 // get messages from others in group
}
