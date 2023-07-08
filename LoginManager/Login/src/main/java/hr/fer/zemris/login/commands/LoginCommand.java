package hr.fer.zemris.login.commands;

import hr.fer.zemris.login.console.LoginConsole;
import hr.fer.zemris.login.util.HexByteUtil;
import hr.fer.zemris.login.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;

public class LoginCommand implements Command {

    private static int numOfTries = 0;

    @Override
    public void execute(String username) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String user = null;

        String password = Util.inputPassword();
        while(true){
            try{
                if(numOfTries == 0) break;
                Thread.sleep((long) (1000 * Math.pow(numOfTries,2)));
                break;
            }catch (InterruptedException ignored){}
        }

        try (BufferedReader br = Files.newBufferedReader(LoginConsole.dbPath)) {
            String line = br.readLine();

            while (!Objects.isNull(line)) {
                String storedUsername = line.split(",")[0];
                if (storedUsername.equals(username)) {
                    user = line;
                    break;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Couldn't open file.");
            return;
        }
        if (Objects.isNull(user)) {
            System.out.println("Username or password incorrect.");
            numOfTries++;
            return;
        }

        String[] split = user.split(",");

        if (split.length != 4) {
            System.out.println("Database is corrupted.");
            System.exit(0);
        }

        byte[] salt = HexByteUtil.toByteArray(split[1]);
        byte[] storedHash = HexByteUtil.toByteArray(split[2]);

        byte[] secret = Util.generateSecret(password, salt);

        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        byte[] calculatedHash = md.digest(secret);

        if (!Arrays.equals(storedHash, calculatedHash)) {
            System.out.println("Username or password incorrect.");
            numOfTries++;
            return;
        }

        String changePassFlag = split[3];

        if (changePassFlag.equals("1")) {
            changePass(username);
        }

        System.out.println("Login successful");
        numOfTries = 0;

    }

    private void changePass(String username) throws  NoSuchAlgorithmException, InvalidKeySpecException {
        String newPass;
        while (true) {
            newPass = Util.getNewPassword();
            if (Objects.isNull(newPass)) {
                System.out.println("Password mismatch. Try again.");
                continue;
            }
            break;
        }

        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] salt = secureRandom.generateSeed(64);

        byte[] secret = Util.generateSecret(newPass, salt);

        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        byte[] hash = md.digest(secret);

        String formatted = Util.formatOutput(username, salt, hash);

        try (BufferedReader br = Files.newBufferedReader(LoginConsole.dbPath)) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (!Objects.isNull(line)) {
                String user = line.split(",")[0];

                if (!user.equals(username)) {
                    sb.append(line).append("\n");
                } else {
                    sb.append(formatted);
                }

                line = br.readLine();
            }

            Files.writeString(LoginConsole.dbPath,sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Couldn't open file");
        }
    }
}
