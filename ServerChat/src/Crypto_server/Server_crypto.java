/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Crypto_server;
import java.io.BufferedOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.ObjectInputStream;  
import java.io.ObjectOutputStream;  
import java.math.BigInteger;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.RSAPrivateKeySpec;  
import java.security.spec.RSAPublicKeySpec;  
import javax.crypto.Cipher; 

/**
 *
 * @author admin
 */
public class Server_crypto {
    private static PublicKey publickey;
    private static PrivateKey privatekey;
     private static final String PUBLIC_KEY_FILE = "Public.key";  
 private static final String PRIVATE_KEY_FILE = "Private.key";
 private static boolean Decrypted=false;

    public static boolean isDecrypted() {
        return Decrypted;
    }

    public static void setDecrypted(boolean Decrypted) {
        Server_crypto.Decrypted = Decrypted;
    }


    public static PublicKey getPublickey() {
        return publickey;
    }

    public static void setPublickey(PublicKey publickey) {
        Server_crypto.publickey = publickey;
    }

    public static PrivateKey getPrivatekey() {
        return privatekey;
    }

    public static void setPrivatekey(PrivateKey privatekey) {
        Server_crypto.privatekey = privatekey;
    }
    public static String decryptData(String message) throws IOException {  
  System.out.println("\n----------------DECRYPTION STARTED------------");  
    byte[] data = message.getBytes("ISO-8859-1");
  byte[] descryptedData = null;  
  String text=null;
    
  try {  
   PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);  
   Cipher cipher = Cipher.getInstance("RSA");  
   cipher.init(Cipher.DECRYPT_MODE, privateKey);  
   descryptedData = cipher.doFinal(data);  
   text = new String( descryptedData,"ISO-8859-1");
   System.out.println("Decrypted Data: " + text);  
     
  } catch (Exception e) {  
   e.printStackTrace();  
   return message;
  }   
    
  System.out.println("----------------DECRYPTION COMPLETED------------");   
  return text;
 }  
   public static PrivateKey readPrivateKeyFromFile(String fileName) throws IOException{  
  FileInputStream fis = null;  
  ObjectInputStream ois = null;  
  try {  
   fis = new FileInputStream(new File(fileName));  
   ois = new ObjectInputStream(fis);  
     
   BigInteger modulus = (BigInteger) ois.readObject();  
      BigInteger exponent = (BigInteger) ois.readObject();  
     
      //Get Private Key  
      RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);  
      KeyFactory fact = KeyFactory.getInstance("RSA");  
      PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);  
              
      return privateKey;  
        
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
    
public static void  generate_Key()
{
     try 
     {  
   System.out.println("-------GENRATE PUBLIC and PRIVATE KEY-------------");  
   KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");  
   keyPairGenerator.initialize(2048); //1024 used for normal securities  
   KeyPair keyPair = keyPairGenerator.generateKeyPair();  
   publickey = keyPair.getPublic();  
   privatekey = keyPair.getPrivate();
    System.out.println("\n------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------\n");  
   KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
   RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publickey, RSAPublicKeySpec.class);  
   RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privatekey, RSAPrivateKeySpec.class); 
    System.out.println("\n--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------\n");   
   saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());  
   saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());  
     }
   catch(Exception e)
           {
           e.printStackTrace();
           }
     }
private static void saveKeys(String fileName,BigInteger mod,BigInteger exp) throws IOException{  
  FileOutputStream fos = null;  
  ObjectOutputStream oos = null;  
    
  try {  
   System.out.println("Generating "+fileName + "...");  
   fos = new FileOutputStream(fileName);  
   oos = new ObjectOutputStream(new BufferedOutputStream(fos));  
     
   oos.writeObject(mod);  
   oos.writeObject(exp);     
     
   System.out.println(fileName + " generated successfully");  
  } catch (Exception e) {  
   e.printStackTrace();  
  }  
  finally{  
   if(oos != null){  
    oos.close();  
      
    if(fos != null){  
     fos.close();  
    }  
   }  
  }    
}
}
   
