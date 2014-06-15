/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package message;

import static Crypto.Public_Encryption.encryptData;
import java.nio.ByteBuffer;
import Crypto.Group_Encryption;
import static Crypto.Group_Encryption.encrypdatagroup;

/**
 *
 * @author admin
 */
public class User_Server_Phase1 {
     private static String public_encryption;
     private static String group_encryption;
     private static boolean First_Phase_over=false;

    public static boolean isFirst_Phase_over() {
        return First_Phase_over;
    }

    public static void setFirst_Phase_over(boolean First_Phase_over) {
        User_Server_Phase1.First_Phase_over = First_Phase_over;
    }
    
    public static String encrypt_public_group(String message)
    {
        try
        { 
            System.out.println("PHASE 1 STARTED");
       public_encryption=encryptData(message);
       group_encryption=encrypdatagroup(public_encryption);
       setFirst_Phase_over(true);
       return group_encryption;
       
        }
        catch(Exception e)
        { return message;
        }
        
        
    }
    
}
