package hr.fer.zemris.srs.commands;

import hr.fer.zemris.srs.utils.CryptoUtils;
import hr.fer.zemris.srs.utils.StringUtils;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class GetCommand implements Command{

    private Cipher cipher;

    public GetCommand(){
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }catch (Exception ignored){}
    }
    @Override
    public void run(String commandLine, Path path) {

        String[] splitCommandLine = StringUtils.splitCommand(commandLine);

        if (splitCommandLine.length != 2){
            System.out.println("Invalid put command");
            return;
        }

        String masterPassword = splitCommandLine[0];
        String site = splitCommandLine[1];
        String line;

        boolean firstLinePassed = false;
        try(BufferedReader bufferedReader = new BufferedReader(Files.newBufferedReader(path))) {
            while(true) {
                line = bufferedReader.readLine();

                if(!firstLinePassed){
                    firstLinePassed = true;
                    continue;
                }

                if(line == null) break;

                String decryptedText = CryptoUtils.decryptText(line,masterPassword,cipher);

                if(decryptedText == null) continue;

                String password = decryptedText.substring(decryptedText.lastIndexOf(" "), decryptedText.length()-1);
                String siteFromFile = decryptedText.substring(1,decryptedText.indexOf(" "));

                if(siteFromFile.equals(site)){
                    System.out.printf("Password for %s is: %s.\n",site,password.trim());
                    return;
                }
            }

            System.out.println("Master password incorrect or integrity check failed.");


        }catch (Exception ignored){}

    }
}
