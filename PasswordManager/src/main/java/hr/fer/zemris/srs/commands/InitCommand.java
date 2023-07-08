package hr.fer.zemris.srs.commands;

import hr.fer.zemris.srs.pm.PasswordManagerImpl;
import hr.fer.zemris.srs.utils.CryptoUtils;
import hr.fer.zemris.srs.utils.HexByteUtil;
import hr.fer.zemris.srs.utils.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class InitCommand implements Command{
    @Override
    public void run(String commandLine, Path path) {
        String[] split = StringUtils.splitCommand(commandLine);

        if(split.length != 1){
            System.out.println("Invalid init command");
            return;
        }

        try {
            boolean fileExists = Files.exists(path);
            if(fileExists){
                overwrite(split[0],path);
                return;
            }
            Files.createFile(path);
            saveMasterPassword(split[0],path);

        }catch (Exception e){
            System.out.println("Couldn't open file");
        }
    }

    private void saveMasterPassword(String masterPassword, Path path) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec iv = CryptoUtils.getIv(cipher.getBlockSize());

        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] salt = secureRandom.generateSeed(64);

        byte[] secretKeyBytes = CryptoUtils.getSecretKey(masterPassword,salt);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);

        byte[] encrypted = cipher.doFinal(PasswordManagerImpl.EMPTY_BASE_PASSWORD.getBytes());

        String masterPassEncryptedString = HexByteUtil.toHexString(encrypted);

        byte[] mac = CryptoUtils.getMac(secretKey,masterPassEncryptedString);

        String formatted = StringUtils.getString(salt, iv.getIV(), mac,encrypted);

        Files.writeString(path,formatted, StandardOpenOption.WRITE);

        System.out.println("Password manager initialized");
    }

    private void overwrite(String masterPassword, Path path) {
        System.out.println("Password manager is already initialized.");
        System.out.println("Do you want to overwrite it?(y/n)");
        System.out.println("WARNING!!! By overwriting you will lose your all data.");

        Scanner sc = new Scanner(System.in);
        String answer;
        do {
            System.out.print("> ");
            answer = sc.nextLine();
        }while(!(answer.equals("y") || answer.equals("n")));

        if(answer.equals("n")) return;

        try {
            Files.delete(path);
            Files.createFile(path);
            saveMasterPassword(masterPassword,path);
        }catch (Exception ignored){}


    }
}
