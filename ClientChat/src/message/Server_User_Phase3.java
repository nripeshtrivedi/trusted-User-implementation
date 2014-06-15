package message;

import static Crypto.Group_Encryption.Decrypt_group;
import static Crypto.Group_Encryption.setDecrypted;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.swing.JTextArea;
import static message.User_Server_Phase1.encrypt_public_group;
import static message.User_Server_Phase1.setFirst_Phase_over;

public class Server_User_Phase3
{
     private static ByteBuffer ReadBuffer =ByteBuffer.allocateDirect(30000);
     private static CharsetDecoder asciiDecoder=asciiDecoder = Charset.forName( "US-ASCII").newDecoder();;
     private static ArrayList<String> Chat_Buffer=new <String> ArrayList();
     private static ArrayList<String> Decrypted_Chat_Buffer=new <String> ArrayList();
     private static final int CHAT_CONSTANT=3;
      private static int count_chat=0;
      private static String find_difference = new String("message");
      private static int first_index=-1;
       private static int second_index=0;
       private static String line=null;
       private static ByteBuffer pb_encrypted =ByteBuffer.allocateDirect(30000);
       private static boolean third_phhase_over=false;

    public static boolean isThird_phhase_over() {
        return third_phhase_over;
    }

    public static void setThird_phhase_over(boolean third_phhase_over) {
        Server_User_Phase3.third_phhase_over = third_phhase_over;
    }
    public static void Read_Enrypted_Massage(Selector ReadSelector,JTextArea ChatBox,SocketChannel SChan, Iterator i)
    { 
      
        
        try
        {
            System.out.println("PHASE 3 STARTED");
              while (i.hasNext()){
                 
              SelectionKey key = (SelectionKey) i.next();
                   
                    SocketChannel channel = (SocketChannel) key.channel();
            
                   
                  Thread.sleep(7000);
                   
                 
                 
                    ReadBuffer.clear();
                    
                    
                    long nbytes = channel.read(ReadBuffer);
                    
                     if (nbytes == -1) {
                        ChatBox.append("You logged out !\n");
                        channel.close();
                    } else {
                        System.out.println("Reading Encrypted messsage, Collecting it and decrypting it using group key");
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
                     }
                         
                        
       while(count_chat< CHAT_CONSTANT)// Since the messages from the server to the client are coming in the thread
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
                               
       }
                               
              }  
                     first_index=-1;
                     second_index=0;
                         Iterator iterator = Chat_Buffer.iterator();
                         
                             while(iterator.hasNext())
                             {
                                 String group_public=iterator.next().toString();
                                 String public_encrypted_message=Decrypt_group(group_public);
                                 Decrypted_Chat_Buffer.add(public_encrypted_message);
                                  System.out.println("Decrypted individual Buffer element---"+public_encrypted_message);
                                 
                             }
                             Collections.shuffle(Decrypted_Chat_Buffer);
                            
                              setDecrypted(true);
                                 send_to_server(ReadSelector,ChatBox,SChan);
                                 count_chat=0;
 
     
    }
         catch(Exception e)
        {}
     
                
    }
    private static void send_to_server(Selector ReadSelector,JTextArea ChatBox,SocketChannel SChan)
    {
        try
        {
        System.out.println("Sending all the encrypted messages to server");
        
        Iterator iterator = Decrypted_Chat_Buffer.iterator();
         while(iterator.hasNext())
                             {
                                 String group_public=iterator.next().toString();
        
            prepareBuffer("message"+group_public);
            channelWrite(SChan);
                             }
        }
         catch(Exception e)
                 {e.printStackTrace();}
        setThird_phhase_over(true);
         setFirst_Phase_over(false);
         setDecrypted(false);
         System.out.println("Succesfully sent all the messages to the server");
          Decrypted_Chat_Buffer.clear();
                             Chat_Buffer.clear();
        }
    
        
        
        public static void prepareBuffer(String massg)throws Exception {
            
           pb_encrypted.clear();   
           pb_encrypted.put(massg.getBytes("ISO-8859-1"));
           pb_encrypted.flip();
        }
        
        public static void channelWrite(SocketChannel client) {
            long num=0;
            long len=pb_encrypted.remaining();
            while(num!=len) {
                try {
                    num+=client.write(pb_encrypted);
                    
                    Thread.sleep(5);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch(InterruptedException ex) {
                    
                }
                
            }
            pb_encrypted.rewind();
        }
    }
