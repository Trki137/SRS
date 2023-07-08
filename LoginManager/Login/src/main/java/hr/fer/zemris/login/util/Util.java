package hr.fer.zemris.login.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;

public class Util {

    private final static int NUM_OF_ITERATIONS = 65537;
    private final static int KEY_LENGTH = 256;

    public static String getNewPassword() {
        Console console = System.console();

        String pattern = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=.!?])"
                + "(?=\\S+$).{8,20}$";

        while(true) {

            if (Objects.isNull(console)) {
                throw new IllegalStateException("Couldn't get console");
            }

            System.out.print("\nNew Password: ");

            char[] password = console.readPassword();

            String pass = new String(password);
            if(!pass.matches(pattern)){
                System.out.println("Password is weak. Expected at least 8 characters with an upper case, lower case, number and special sign");
                continue;
            }

            System.out.print("Repeat Password: ");

            char[] repeatedPassword = console.readPassword();

            return Arrays.equals(password, repeatedPassword) ? new String(password) : null;
        }
    }

    public static String inputPassword() {
        Console console = System.console();

        if (Objects.isNull(console)) {
            throw new IllegalStateException("Couldn't get console");
        }

        System.out.print("Password: ");

        char[] password = console.readPassword();


        return new String(password);

    }
    public static byte[] generateSecret(String password, byte[] salt ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt,NUM_OF_ITERATIONS,KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey secretKey = factory.generateSecret(pbeKeySpec);

        return secretKey.getEncoded();
    }


    public static String formatOutput(String username, byte[] salt, byte[] hash) {
        return (username + "," +
                HexByteUtil.toHexString(salt) + "," +
                HexByteUtil.toHexString(hash) + "," +
                "0\n");
    }


}
