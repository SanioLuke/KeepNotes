package com.example.keepnotes;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public SecretKeySpec generateSecretkeySpec(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] sb = Base64.getDecoder().decode(salt);
        PBEKeySpec key = new PBEKeySpec(password.toCharArray(), sb, 12000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] kb = skf.generateSecret(key).getEncoded();
        return new SecretKeySpec(kb, "AES");
    }


    public IvParameterSpec generateIvSpec(String iv)
    {
        byte[] i = Base64.getDecoder().decode(iv.getBytes());
        return new IvParameterSpec(i);
    }

    public HashMap<String,String> encrypt(String ... arg) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{

        SecureRandom rand = new SecureRandom();
        byte[] s = new byte[256];
        rand.nextBytes(s);
        String salt = Base64.getEncoder().encodeToString(s);
        String password = arg[0];
        SecretKeySpec keySpec = generateSecretkeySpec(password,salt);
        rand = new SecureRandom();
        byte[] i = new byte[16];
        rand.nextBytes(i);
        String iv = Base64.getEncoder().withoutPadding().encodeToString(i);
        IvParameterSpec ivSpec = generateIvSpec(iv);
        String message=arg[1];
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] emsg = cipher.doFinal(message.getBytes());
        String emessage = Base64.getEncoder().encodeToString(emsg);
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("salt", salt);
        map.put("iv",iv);
        map.put("message", emessage);
        return map;
    }

    public String decrypt(HashMap<String,String> map) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        String password = map.get("password");
        String salt = map.get("salt");
        String iv = map.get("iv");
        SecretKeySpec keySpec = generateSecretkeySpec(password, salt);
        IvParameterSpec ivSpec = generateIvSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] msg = Base64.getDecoder().decode(map.get("message").getBytes());
        String message = new String(cipher.doFinal(msg));
        return message;
    }

}
