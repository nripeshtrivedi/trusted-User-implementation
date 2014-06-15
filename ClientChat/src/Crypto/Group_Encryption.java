/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crypto;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javax.crypto.*;
import java.security.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


 import java.io.*;
import java.security.*;
 import javax.crypto.*;
 import javax.crypto.spec.*;
 import java.util.*;



public class Group_Encryption {

    private static SecretKey key=null;         // in java.security
    private static Cipher cipher = null; // in javax.crypto
    private static Cipher encrypt_cipher = null; // in javax.crypto
    private static Cipher decrypt_cipher = null; // in javax.crypto
    private static boolean decrypted=false;
    private static SecretKey secretKey;
    private static KeyGenerator keyGenerator;
     private static String keyFile, cipherFile;
    private static String password;
    private static FileOutputStream keyOutFile, cipherOutFile;
        private static SecretKey passwordKey;
          private static PBEParameterSpec parameterSpec;
    
 
    private static FileInputStream keyInFile, cipherInFile;
    public static void StoreinFile()
    {
            try
            {
                System.out.println("Generating key and storing in file");
              password = "super_secret_password";
     PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
     SecretKeyFactory keyFactory =
          SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        passwordKey = keyFactory.generateSecret(keySpec); 
       byte[] salt = new byte[8];
       Random rnd = new Random();
       rnd.nextBytes(salt);
       int iterations = 1000;
 
       parameterSpec = new PBEParameterSpec(salt, iterations);
Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
      cipher.init(Cipher.ENCRYPT_MODE, passwordKey, parameterSpec);
      
 
 
       // Create a secret key using the DESede algorithm which
       // may be used by applications for symmetric encryption.
 
       keyGenerator = KeyGenerator.getInstance("DESede");
      keyGenerator.init(168);
       secretKey = keyGenerator.generateKey();

       // Get the bytes of this secret key and encrypted them,
 
       byte[] secretKeyBytes = secretKey.getEncoded();
       byte[] secretKeyBytesEncrypted = cipher.doFinal(secretKeyBytes);

      // A little "hack" to see how many bytes make up the encrypted
      // secret key.
 
       System.out.println(secretKeyBytesEncrypted.length);
 
       // Write the salt and the encrypted secret key to the file;
       // salt is needed when reconstructing the PBE key for decryption.
 
       keyFile = "bin.key";
      keyOutFile = new FileOutputStream(keyFile);

      keyOutFile.write(salt);
      keyOutFile.write(secretKeyBytesEncrypted);

      keyOutFile.close();
            }
            catch(Exception e)
            {e.printStackTrace();
            }
   }
public static void readFromFile()
{
    try
    {
        keyFile = "bin.key";
        File f = new File("bin.key");
         if(f.exists())
      {
        keyInFile = new FileInputStream((keyFile));
      }
      else
      {
           StoreinFile();
      }
        System.out.println("Reading the key from file");
        
     password = "super_secret_password";
       PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
       SecretKeyFactory keyFactory =
           SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        passwordKey = keyFactory.generateSecret(keySpec);
       // Read in the previouly stored salt, encryped secret key bytes,
       // and set the iteration count.

          
       
       
     
 
       byte[] salt = new byte[8];
       keyInFile.read(salt);
       int iterations = 1000;
      
 
   parameterSpec = new PBEParameterSpec(salt, iterations);
 
     Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
     cipher.init(Cipher.DECRYPT_MODE, passwordKey, parameterSpec);

       // Read the encrypted secret key and reconstruct it.
       // The array dimension was determined in StoreKey.java
       // Later we'll see how to not "hardcode" this information.
 
       byte[] secretKeyBytesEncrypted = new byte[32];
       keyInFile.read(secretKeyBytesEncrypted);
 
       byte[] secretKeyBytes = cipher.doFinal(secretKeyBytesEncrypted);
       secretKey = new SecretKeySpec(secretKeyBytes, "DESede");
       System.out.println("-----------"+secretKey.toString());
      
 
       keyInFile.close();
       
       
    }
    catch(Exception e)
    {     e.printStackTrace();
    }
}

    


    
  

    public static String encrypdatagroup(String message) {
        try {
            
            encrypt_cipher = Cipher.getInstance("DESede");
        encrypt_cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] clearTextBytes = message.getBytes("ISO-8859-1");

       
     
            byte[] cipherBytes = encrypt_cipher.doFinal(clearTextBytes);
          String cipherText = new String(cipherBytes,"ISO-8859-1");
            
            System.out.println("Encryted Data using public and group key:-"+cipherText); 
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return message;

        }

    }

    public static String Decrypt_group(String message) {
        try {
          
           decrypt_cipher = Cipher.getInstance("DESede");
             byte[] clearTextBytes = message.getBytes("ISO-8859-1");
            decrypt_cipher.init(Cipher.DECRYPT_MODE, secretKey);

             
           
            byte[] cipherBytes = decrypt_cipher.doFinal(clearTextBytes);
            String decryptedText = new String(cipherBytes,"ISO-8859-1");
           

            return decryptedText;
        } catch (Exception e) {
            e.printStackTrace();
            return message;

        }
    }

    public static boolean isDecrypted() {
        return decrypted;
    }

    public static void setDecrypted(boolean decrypted) {
        Group_Encryption.decrypted = decrypted;
    }
}



