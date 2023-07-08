package hr.fer.zemris.srs.utils;

public class StringUtils {

    public static String getString(byte[] salt, byte[] iv,byte[] mac, byte[] encryptedPassword){
        String saltHex = HexByteUtil.toHexString(salt);
        String initializationVector = HexByteUtil.toHexString(iv);
        String macString = HexByteUtil.toHexString(mac);
        String passEnc = HexByteUtil.toHexString(encryptedPassword);

        return String.format("%s,%s,%s,%s", saltHex, initializationVector,macString,passEnc);
    }

    public static String[] splitCommand(String commandLine){
        String commandLineWithoutCommand = commandLine.substring(commandLine.indexOf(" "));
        return commandLineWithoutCommand.trim().split(" ");
    }
}
