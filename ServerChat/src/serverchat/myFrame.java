
package serverchat;

import static Crypto_server.Server_crypto.generate_Key;
import static Crypto_server.Server_crypto.isDecrypted;
import static Crypto_server.Server_crypto.setDecrypted;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.charset.StandardCharsets.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import static message.Server_User_Phase2.isPhase_2_over;
import static message.Server_User_Phase2.send_to_group;
import static message.Server_User_Phase4.Read_Enrypted_Massage;
import static message.Server_User_Phase4.setPhase_4_over;


public class myFrame extends JFrame{
    
    /** Creates a new instance of myFrame */
    private JTextArea ChatBox=new JTextArea(10,45);
    private JScrollPane myChatHistory=new JScrollPane(ChatBox,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    private JTextArea UserText = new JTextArea(5,40);
    private JScrollPane myUserHistory=new JScrollPane(UserText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JButton Send = new JButton("Send");
    private JButton Start = new JButton("Start Server!");
    private server ChatServer;
    private static int Server_accept=0;
    private InetAddress ServerAddress ;
    private static int count_message=0;
    private ArrayList<String> Chat_Buffer;
    private final int CHAT_CONSTANT=3;
  
    
    
    public myFrame() {
        generate_Key();
        setTitle("Server");
        setSize(560,400);
        Container cp=getContentPane();
        cp.setLayout(new FlowLayout());
        cp.add(new JLabel("Server History"));
        cp.add(myChatHistory);
        cp.add(new JLabel("Chat Box : "));
        cp.add(myUserHistory);
        cp.add(Send);
        cp.add(Start);
        
        
        
        Start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                ChatServer=new server();
                ChatServer.start();
                
            }
        });
        Send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatServer.SendMassage(ServerAddress.getHostName()+" < Server > "+UserText.getText());
            }
        });
        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new myFrame();
    }
    
    
    public class server extends Thread {
        private static final int PORT=9999;
        private LinkedList Clients;
        private LinkedList Buffer_Clients;
        private ByteBuffer ReadBuffer;
        private ByteBuffer WriteBuffer;
        public  ServerSocketChannel SSChan;
        private Selector ReaderSelector;
        private CharsetDecoder asciiDecoder;
        
  
        
        
        
        public server() {
            Clients=new LinkedList();
            Buffer_Clients=new LinkedList();
            ReadBuffer=ByteBuffer.allocateDirect(30000);
            WriteBuffer=ByteBuffer.allocateDirect(30000);
            asciiDecoder = Charset.forName( "US-ASCII").newDecoder();
            Chat_Buffer=new <String> ArrayList();
            
        }
        
        public void InitServer() {
            try {
                SSChan=ServerSocketChannel.open();
                SSChan.configureBlocking(false);
                ServerAddress=InetAddress.getLocalHost();
                System.out.println(ServerAddress.toString());
                
                SSChan.socket().bind(new InetSocketAddress(ServerAddress,PORT));
                
                ReaderSelector=Selector.open();
                ChatBox.setText(ServerAddress.getHostName()+"<Server> Started. \n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            InitServer();
            
            while(true) {
                acceptNewConnection();
                
                ReadMassage();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                
            }
        }
        
        public void acceptNewConnection() {
            SocketChannel newClient;
            try {
                
                while ((newClient = SSChan.accept()) != null) {
                    ChatServer.addClient(newClient);
                    
                    
                    
                    SendMassage(newClient,ServerAddress.getHostName()+"<server> welcome you !\n Note :To exit" +
                            " from server write 'quit' .\n");
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
        public void addClient(SocketChannel newClient) {
            Clients.add(newClient);
            try {
                newClient.configureBlocking(false);
                newClient.register(ReaderSelector,SelectionKey.OP_READ,new StringBuffer());
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void SendMassage(SocketChannel client ,String messg) {
            prepareBuffer(messg);
            channelWrite(client);
        }
        public void SendMassage(String massg) {
            if(Clients.size()>0) {
                for(int i=0;i<Clients.size();i++) {
                    SocketChannel client=(SocketChannel)Clients.get(i);
                    SendMassage(client,massg);
                }
            }
        }
        
        
        public void prepareBuffer(String massg) {
            WriteBuffer.clear();
            WriteBuffer.put(massg.getBytes());
            WriteBuffer.putChar('\n');
            WriteBuffer.flip();
        }
        
        public void channelWrite(SocketChannel client) {
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
        
        public void sendBroadcastMessage(SocketChannel client,String mesg) {
            prepareBuffer(mesg);
            Iterator i = Clients.iterator();
            while (i.hasNext()) {
                SocketChannel channel = (SocketChannel)i.next();
                if (channel != client) {
                    channelWrite(channel);
                }
            }
        }
        public void ReadMassage() {
            try {
                
                ReaderSelector.selectNow();
                Set readkeys=ReaderSelector.selectedKeys();
                Iterator iter=readkeys.iterator();
                while(iter.hasNext()) {
                     
                     if(isPhase_2_over()&&(!isDecrypted()&&(Server_accept<2)))
                 {
                         Read_Enrypted_Massage(ReaderSelector,ChatBox,iter,Clients);
                         Server_accept++;// for accepting only one clients message
                }
                       else
                     {
                          Server_accept=0;
                          setPhase_4_over(false);
                    SelectionKey key=(SelectionKey) iter.next();
                    iter.remove();
                    
                    SocketChannel client=(SocketChannel)key.channel();
                    ReadBuffer.clear();
                    
                    long num=client.read(ReadBuffer);
                  try
                  {
             Thread.sleep(100);
                  }
                  catch(Exception e)
                  {}
                    
                    if(num==-1) {
                        client.close();
                        Clients.remove(client);
                       
                        sendBroadcastMessage(client,"logout: " +
                                client.socket().getInetAddress());
                        
                    } else {
                       
                       
                        StringBuffer str=(StringBuffer)key.attachment();
                        
                        ReadBuffer.flip();
                        String data=  ISO_8859_1.decode(ReadBuffer).toString();
                        ReadBuffer.clear();
                        
                        str.append(data);
                        
                       
                        String line = str.toString();
                         System.out.println("------my line"+line);
                       
                          
                            Buffer_Clients.add(client);
                             count_message+=1;
                        
                        
                             System.out.println("------my line"+line);
                            System.out.println(line);
                            
                            if (line.endsWith("quit")) {
                                client.close();
                                
                                Clients.remove(client);
                                
                                ChatBox.append("Logout: " + client.socket().getInetAddress());
                                
                                Chat_Buffer.add(line);
                                
                                 if(count_message==CHAT_CONSTANT)
                                 {
                                      send_to_group(Buffer_Clients,Chat_Buffer);
                                      count_message=0;
                                         Buffer_Clients.clear();
                                         Chat_Buffer.clear();
                                 }
                                ChatBox.append(""+'\n');
                            } else {
                               
                                ChatBox.append(client.socket().getInetAddress() + ": " + line);
                                
                                 Chat_Buffer.add(line);
                               
                                if(count_message==CHAT_CONSTANT)
                                {
                                    send_to_group(Buffer_Clients,Chat_Buffer);
                                    count_message=0;
                                    Buffer_Clients.clear();
                                    Chat_Buffer.clear();
                                }
                                
                                ChatBox.append(""+'\n');
                                
                                str.delete(0,str.length());
                            }
                        }
                    }
                }
               
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
