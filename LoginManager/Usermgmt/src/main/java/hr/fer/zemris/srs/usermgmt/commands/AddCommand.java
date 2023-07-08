package hr.fer.zemris.srs.usermgmt.commands;

import hr.fer.zemris.srs.usermgmt.console.ConsoleImpl;
import hr.fer.zemris.srs.usermgmt.util.HexByteUtil;
import hr.fer.zemris.srs.usermgmt.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class AddCommand implements Command{
    @Override
    public void execute(String username) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Util.checkIfDBExists();

        boolean userExists = Files.readAllLines(ConsoleImpl.dbPath)
                .stream()
                .anyMatch(line -> username.equals(line.split(",")[0]));

        if(userExists){
            System.out.println("User with username "+ username +" already exists.");
            return;
        }

        String password = Util.getPassword();
        if(Objects.isNull(password)){
            System.out.println("User add fail. Password mismatch");
            return;
        }

        byte[] salt = new SecureRandom().generateSeed(64);

        byte[] secret = Util.generateSecret(password,salt);



        MessageDigest md = MessageDigest.getInstance("SHA3-512");
        byte[] hash = md.digest(secret);

        String format = Util.formatOutput(username,salt,hash);

        Files.writeString(ConsoleImpl.dbPath, format,StandardOpenOption.APPEND);

        System.out.println("User successfully added");
    }
}
