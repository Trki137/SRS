package hr.fer.zemris.srs.commands;

import hr.fer.zemris.srs.utils.CryptoUtils;
import hr.fer.zemris.srs.utils.HexByteUtil;
import hr.fer.zemris.srs.utils.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PutCommand implements Command{

    private Cipher cipher;

    public PutCommand(){
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }catch (Exception ignored){}
    }

    @Override
    public void run(String commandLine, Path path) {
        String[] splitCommandLine = StringUtils.splitCommand(commandLine);

        if (splitCommandLine.length != 3){
            System.out.println("Invalid put command");
            return;
        }

        String masterPassword = splitCommandLine[0];
        String site = splitCommandLine[1];
        String password = splitCommandLine[2];

        try {
            if(!checkMasterPassword(masterPassword,path)){
                System.out.println("Master password incorrect");
                return;
            }

            final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

            byte[] salt = secureRandom.generateSeed(64);

            //Secret key
            byte[] secretKeyBytes = CryptoUtils.getSecretKey(masterPassword, salt);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "AES");

            //IV
            IvParameterSpec ivParameterSpec = CryptoUtils.getIv(cipher.getBlockSize());

            String textToEncrypt = String.format("(%s , %s)",site,password);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedText = cipher.doFinal(textToEncrypt.getBytes());

            //Mac
            byte[] mac = CryptoUtils.getMac(secretKey, HexByteUtil.toHexString(encryptedText));

            String formatted = StringUtils.getString(salt, ivParameterSpec.getIV(), mac, encryptedText);

            storePassword(site,formatted,masterPassword,path);

        }catch (Exception ignored){}
    }

    private boolean checkMasterPassword(String masterPassword,Path path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String firstLine = Files.readAllLines(path).get(0);

        return CryptoUtils.decryptText(firstLine,masterPassword,cipher) != null;
    }

    public void storePassword(String site,String formatted,String masterPassword,Path path){

        try(BufferedReader bufferedReader = new BufferedReader(Files.newBufferedReader(path))){
            boolean found = false;
            boolean firstLinePassed = false;

            StringBuilder fileLines = new StringBuilder();
            while(true){
                String line = bufferedReader.readLine();

                if(!firstLinePassed){
                    fileLines.append(line).append("\n");
                    firstLinePassed = true;
                    continue;
                }

                if(line == null) break;

                String decryptedText = CryptoUtils.decryptText(line,masterPassword,cipher);

                if(decryptedText == null) continue;

                String siteFromFile = decryptedText.substring(1,decryptedText.indexOf(" "));

                if (!site.equals(siteFromFile)) fileLines.append(line).append("\n");
                else {
                    fileLines.append(formatted).append("\n");
                    found = true;
                }
            }

            if (!found) fileLines.append(formatted).append("\n");

            Files.writeString(path, fileLines, StandardOpenOption.WRITE);
            System.out.printf("Stored password for %s.\n",site);

        }catch (Exception ignored){}
    }
}


