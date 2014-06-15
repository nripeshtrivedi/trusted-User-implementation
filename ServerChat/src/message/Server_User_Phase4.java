/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package message;

import static Crypto_server.Server_crypto.decryptData;
import static Crypto_server.Server_crypto.setDecrypted;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JTextArea;
import static message.Server_User_Phase2.send_to_group;
import static message.Server_User_Phase2.setPhase_2_over;

/**
 *
 * @author admin
 */
public class Server_User_Phase4 {
     private static ByteBuffer ReadBuffer=ByteBuffer.allocateDirect(30000);
     private static final int CHAT_CONSTANT=3;
     private static int count_chat=0;
     private static int first_index=-1;
       private static int second_index=0;
       private static String line=null;
        private static String find_difference = new String("message");
         private static ArrayList<String> Chat_Buffer=new <String> ArrayList();
         private static ArrayList<String> Decrypted_Chat_Buffer=new <String> ArrayList();
        private static ByteBuffer WriteBuffer=ByteBuffer.allocateDirect(30000);;
    private static boolean Phase_4_over=false;

    public static boolean isPhase_4_over() {
        return Phase_4_over;
    }

    public static void setPhase_4_over(boolean Phase_4_over) {
        Server_User_Phase4.Phase_4_over = Phase_4_over;
    }
    
    public static void Read_Enrypted_Massage(Selector ReadSelector,JTextArea ChatBox,  Iterator iter,LinkedList Clients)
    {
        try
        {
              
        while(iter.hasNext()) {
            
         SelectionKey key=(SelectionKey) iter.next();
                    iter.remove();
                    
                    SocketChannel client=(SocketChannel)key.channel();
                    ReadBuffer.clear();
                    try
                    {
                    Thread.sleep(3000);
                    }
                    catch(Exception e)
                    {}
                    long nbytes = client.read(ReadBuffer);
                    
                    
                  
                    
                     if (nbytes == -1) {
                        ChatBox.append("You logged out !\n");
                        client.close();
                    } else {
                        System.out.println("Reading Encrypted messsage, Collecting it and decrypting it using private key");
                        StringBuffer sb = (StringBuffer)key.attachment();
                        
                        ReadBuffer.flip( );
                        String str =ISO_8859_1.decode( ReadBuffer).toString( );
                        sb.append( str );
                        ReadBuffer.clear( );
                        
                       
                         line = sb.toString();
                         System.out.println("Data Received is----------"+line+"--------------");
                         if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                            
                            
                            ChatBox.append("> "+ line);
                            ChatBox.append(""+'\n');
                            sb.delete(0,sb.length());
                        }
                     
                         
                        
                             
         while(count_chat<CHAT_CONSTANT)
                         {
                             System.out.println("Entering while loop");
                             if(first_index==-1)
                             {
                               first_index=line.indexOf( find_difference,first_index );
                             }
                               System.out.println("first_index--"+first_index);
                               if(first_index==-1){
                                   break;
                               }
                               second_index=line.indexOf(find_difference,first_index+1);
                               System.out.println("second_index--"+second_index);
                               if(second_index==-1)
                               {
                                  String Buffer_element=new String(line.substring(first_index+7));
                                  Chat_Buffer.add(Buffer_element);
                                 
                                 
                                  System.out.println("Printing individual Buffer element---"+Buffer_element);
                                  break;
                               }
                               String Buffer_element=new String(line.substring(first_index+7,second_index));
                             System.out.println("Printing individual Buffer element---"+Buffer_element);
                               Chat_Buffer.add(Buffer_element);
                               first_index=second_index;
                               count_chat++;
                         }
                         count_chat=0;
                             first_index=-1;
                             second_index=0;
                         Iterator iterator = Chat_Buffer.iterator();
                         
                             while(iterator.hasNext())
                             {
                                 String public_encrypted_message=iterator.next().toString();
                                 String message=decryptData(public_encrypted_message);
                                 Decrypted_Chat_Buffer.add(message);
                                  System.out.println("Decrypted individual Buffer element---"+message);
                                 
                             }
                              setDecrypted(true);
                              Chat_Buffer.clear();
                              send_to_group(Clients,Decrypted_Chat_Buffer);
 }
        }
                      
        
    
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
     public static void send_to_group(LinkedList Clients,ArrayList<String> Decrypted_Chat_Buffer)
    {
      
            LinkedHashSet client_set = new LinkedHashSet();
                Iterator j = Clients.iterator();
                while(j.hasNext())
                {
                     boolean isthere=false;
                    Object g=j.next();
                    Iterator k = client_set.iterator();
                    while(k.hasNext())
                    {
                        System.out.println("Comparing two elements");
                   if(k.next().toString().equals(g.toString()))
                   {
                       isthere=true;
                       System.out.println("Elements can not be added");
                   }
                  }
                    if(!isthere)
                    {
                            System.out.println("Adding element");
                        client_set.add(g);
                    }
                }
                
            
          
             System.out.println(client_set.size());
             System.out.println(Clients.size());
             Iterator i = client_set.iterator();
            
             System.out.println("Writing Data to all the clients");
              while (i.hasNext()) {
              System.out.println("Writing Data to all the client"+i.toString());
              SocketChannel channel = (SocketChannel)i.next();
              Iterator chat = Decrypted_Chat_Buffer.iterator();
            while(chat.hasNext())
               {
                    String mesg=chat.next().toString();
                    System.out.println("message"+mesg);
                    prepareBuffer("message"+mesg);
                    channelWrite(channel);
                       System.out.println("Writen Data to client"+i.toString());
                    
                
            }
           
            
        }
               setPhase_4_over(true);
             setPhase_2_over(false);
             setDecrypted(false);
             Decrypted_Chat_Buffer.clear();
   }
    
           
        public static void prepareBuffer(String massg) {
            try
            {
            WriteBuffer.clear();
            WriteBuffer.put(massg.getBytes("ISO-8859-1"));
            WriteBuffer.putChar('\n');
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
    
    


