/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package message;

import static Crypto_server.Server_crypto.setDecrypted;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author admin
 */
public class Server_User_Phase2 {
    private static ByteBuffer WriteBuffer=ByteBuffer.allocateDirect(30000);;
    private static boolean Phase_2_over=false;

    public static boolean isPhase_2_over() {
        return Phase_2_over;
    }

    public static void setPhase_2_over(boolean Phase_2_over) {
        Server_User_Phase2.Phase_2_over = Phase_2_over;
    }
    public static void send_to_group(LinkedList Clients,ArrayList<String> Chat_Buffer)
    {
        
             System.out.println("PHASE 2 STARTED");
             Set client_set = new HashSet(Clients);
             Iterator i = client_set.iterator();
             System.out.println("Writing Data to all the clients");
              while (i.hasNext()) {
                  System.out.println("Writing Data to the client");
              SocketChannel channel = (SocketChannel)i.next();
              Iterator chat = Chat_Buffer.iterator();
            while(chat.hasNext())
               {
                    String mesg=chat.next().toString();
                    System.out.println("message"+mesg);
                    prepareBuffer("message"+mesg);
                    channelWrite(channel);
          
                   
                       System.out.println("Writen Data to client"+i.toString());
                    
                
            }
        }
              WriteBuffer.clear();
              client_set.clear();
              setPhase_2_over(true);
              setDecrypted(false);
   }
    
           
        public static void prepareBuffer(String massg) {
            try
            {
            WriteBuffer.clear();
            WriteBuffer.put(massg.getBytes("ISO-8859-1"));
         
            WriteBuffer.flip();
            }
            catch(Exception e)
            {}
        }
    
 public static void channelWrite(SocketChannel client) {
            long num=0;
            long len=WriteBuffer.remaining();
            while(num!=len) {
                try {
                    num+=client.write(WriteBuffer);
                    
                    Thread.sleep(5);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch(InterruptedException ex) {
                    
                }
            }
            WriteBuffer.rewind();
        }
}
    

