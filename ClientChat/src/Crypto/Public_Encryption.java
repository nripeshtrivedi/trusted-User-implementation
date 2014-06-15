/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

/**
 *
 * @author admin
 */
public class Public_Encryption {
     private static String keyFile, cipherFile;
    private static String password;
    private static FileInputStream keyInFile, cipherInFile;
       private static final String PUBLIC_KEY_FILE = "../ServerChat/Public.key"; 
     public static String encryptData(String data) throws IOException {  
  System.out.println("\n----------------ENCRYPTION STARTED------------");  
    
  System.out.println("Data Before Encryption :" + data);  
  byte[] dataToEncrypt = data.getBytes("ISO-8859-1");  
  byte[] encryptedData = null;  
  try {  
   PublicKey pubKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);  
   Cipher cipher = Cipher.getInstance("RSA");  
   cipher.init(Cipher.ENCRYPT_MODE, pubKey);  
   encryptedData = cipher.doFinal(dataToEncrypt);
    
     
     
  } catch (Exception e) {  
   e.printStackTrace();  
  }   
    
  System.out.println("----------------ENCRYPTION COMPLETED------------");  
  String encrypted_data = new String(encryptedData,"ISO_8859_1");
   System.out.println("Encryted Data using public key: " + encrypted_data);
  return encrypted_data;  
 }  
     public static PublicKey readPublicKeyFromFile(String fileName) throws IOException{  
  FileInputStream fis = null;  
  ObjectInputStream ois = null;  
  try {  
   fis = new FileInputStream(new File(fileName));  
   ois = new ObjectInputStream(fis);  
     
   BigInteger modulus = (BigInteger) ois.readObject();  
      BigInteger exponent = (BigInteger) ois.readObject();  
     
      //Get Public Key  
      RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);  
      KeyFactory fact = KeyFactory.getInstance("RSA");  
      PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);  
              
      return publicKey;  
        
  } catch (Exception e) {  
   e.printStackTrace();  
  }  
  finally{  
   if(ois != null){  
    ois.close();  
    if(fis != null){  
     fis.close();  
    }  
   }  
  }  
  return null;  
 }
}
