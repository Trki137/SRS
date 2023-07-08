package hr.fer.zemris.srs.usermgmt.commands;

import hr.fer.zemris.srs.usermgmt.console.ConsoleImpl;
import hr.fer.zemris.srs.usermgmt.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;

public class ChangePasswordCommand implements Command {
    @Override
    public void execute(String username) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        List<String> allLines = Files.readAllLines(ConsoleImpl.dbPath);

        if (allLines.size() == 0) {
            System.out.println("Password change failed. User doesn't exists");
            return;
        }

        String password = Util.getPassword();

        if (Objects.isNull(password)) {
            System.out.println("Password change failed. Password mismatch");
            return;
        }

        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] salt = secureRandom.generateSeed(64);

        byte[] secret = Util.generateSecret(password, salt);

        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        byte[] hash = md.digest(secret);


        String format = Util.formatOutput(username, salt, hash);
        format = format.substring(0, format.length() - 1);
        for (int i = 0; i < allLines.size(); i++) {
            if (!allLines.get(i).startsWith(username)) continue;

            allLines.remove(i);
            allLines.add(i, format);

            saveList(allLines);

            System.out.println("Password change successful");
            return;

        }

        System.out.println("Password change failed. User doesn't exists");

    }

    private void saveList(List<String> allLines) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(String line: allLines){
            sb.append(line).append("\n");
        }

        Files.writeString(ConsoleImpl.dbPath,sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
