package hr.fer.zemris.srs.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class CryptoUtils {

    private final static int NUM_OF_ITERATIONS = 10_000;
    private final static int KEY_LENGTH = 256;

    public static byte[] getSecretKey(String masterPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(masterPassword.toCharArray(), salt,NUM_OF_ITERATIONS,KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey secretKey = factory.generateSecret(pbeKeySpec);

        return secretKey.getEncoded();
    }

    public static byte[] getMac(SecretKey secretKey, String text) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        return mac.doFinal(text.getBytes());
    }

    public static IvParameterSpec getIv(int size) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[size];
        secureRandom.nextBytes(iv);

        return new IvParameterSpec(iv);
    }

    public static String decryptText(String line,String masterPassword,Cipher cipher) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String saltHex = line.split(",")[0];
        String hexIv = line.split(",")[1];
        String macHex = line.split(",")[2];
        String encryptedTextHex = line.split(",")[3];

        //Secret key
        byte[] secretKeyBytes = CryptoUtils.getSecretKey(masterPassword, HexByteUtil.toByteArray(saltHex));
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");

        //Mac
        byte[] mac = CryptoUtils.getMac(secretKey, encryptedTextHex);
        byte[] macStored = HexByteUtil.toByteArray(macHex);

        if (!Arrays.equals(mac, macStored)) return null;

        //IV
        byte[] iv = HexByteUtil.toByteArray(hexIv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE,secretKey,ivParameterSpec);

        byte[] decryptedTextBytes = cipher.doFinal(HexByteUtil.toByteArray(encryptedTextHex));

        return new String(decryptedTextBytes);
    }


}
