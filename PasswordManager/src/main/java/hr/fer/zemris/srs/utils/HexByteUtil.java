package hr.fer.zemris.srs.utils;

/**
 * Contains methods for the conversion between hexadecimal representation and bytes
 *
 * @author Dean Trkulja
 * @version 1.0
 */
public class HexByteUtil {

    public static String toHexString(byte[] secretKeyBytes) {
        StringBuilder sb = new StringBuilder();
        for(byte num : secretKeyBytes) sb.append(byteToHex(num));

        return sb.toString();
    }

    public static byte[] toByteArray(String hex) {
        if (hex.length() % 2 == 1)
            throw new IllegalArgumentException("Invalid hexadecimal");

        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i < hex.length(); i += 2)
            bytes[i/2] = hexToByte(hex.substring(i, i+2));

        return bytes;
    }

    private static String byteToHex(byte num){
        char[] hexValues = new char[2];
        hexValues[0] = Character.forDigit((num>>4) & 0xF,16);
        hexValues[1] = Character.forDigit(num & 0xF,16);

        return new String(hexValues);
    }

    private static byte hexToByte(String hex){
        return (byte) ((toDigit(hex.charAt(0)) << 4) + toDigit(hex.charAt(1)));
    }

    private static int toDigit(char hex){
        int digit = Character.digit(hex,16);
        if(digit == -1) throw new IllegalArgumentException("Not valid hex");

        return digit;
    }
}
