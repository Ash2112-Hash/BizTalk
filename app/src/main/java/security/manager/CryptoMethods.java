package security.manager;

import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoMethods {
    //private static final IvParameterSpec VECTOR = new IvParameterSpec(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
    public static String encryption(String message, SecretKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] temp = message.getBytes();
        byte[] encryptedMessage = cipher.doFinal(temp);
        String encryptedString = Base64.getEncoder().encodeToString(encryptedMessage);

        return encryptedString;
    }
    public static String decryption(String message, SecretKey key)throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(message));
        String decodedData = new String(decryptedData);

        return decodedData;
    }
    public static String SKeyToString(SecretKey key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    public static SecretKey StringToSKey(String s){
        byte[] decodedKey = Base64.getDecoder().decode(s);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public static SecretKey generateKey() {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
        }catch(Exception e){
            Log.d("Security", "Exception occur creating KeyGenerator");
            return null;
        }
        generator.init(128);
        SecretKey key = generator.generateKey();
        return key;
    }

}
