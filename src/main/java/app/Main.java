package app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        /*Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        String message = scanner.nextLine();
        System.out.println(message);

        message = encode(message);
        System.out.println(message);

        message = makeErrors(message, random);
        System.out.println(message);

        message = decode(message);
        System.out.println(message);*/

        Random random = new Random();

        try (InputStream in = Files.newInputStream(Paths.get("send.txt"));
             OutputStream out = Files.newOutputStream(Paths.get("received.txt"))) {
            int b;
            String binary;
            while ((b = in.read()) != -1) {
                binary = getBinaryString(b, true);
                binary = changeBit(binary, random);
                b = getIntegerFromBinaryString(binary);
                out.write(b);
            }
        } catch (IOException e) {
            System.err.println("Error occured during reading/writing from/to a file");
        }

        /*showMenu();
        int option;
        while ((option = Integer.parseInt(scanner.nextLine())) != 0) {
            System.out.println();
            switch (option) {
                case 1:
                    break;
                default:
                    System.out.println("Incorrect option! Try again.\n");
                    break;
            }
            showMenu();
        }*/
    }

    /**
     * Converts positive integer to a binary string representation.
     *
     * @param n positive integer
     * @param withLeadingZeros whether should append leading zeroes
     * @return binary string representation of a given number
     */
    private static String getBinaryString(int n, boolean withLeadingZeros) {
        StringBuilder sb = new StringBuilder();
        if (n == 0) {
            sb.append("0");
        }

        int number = n;
        while (number != 0) {
            sb.append(number % 2);
            number >>= 1;
        }

        if (withLeadingZeros) {
            int length = sb.length();
            while (length % 8 != 0) {
                sb.append("0");
                length++;
            }
        }

        return sb.reverse().toString();
    }

    /**
     * Converts given binary string to integer value.
     *
     * @param binary binary string representation of a number
     * @return integer value
     */
    private static int getIntegerFromBinaryString(String binary) {
        char[] bits = binary.toCharArray();

        int result = 0;
        int powerOfTwo = 1;
        for (int i = bits.length - 1; i >= 0; i--) {
            result = bits[i] == '0' ? result : result + powerOfTwo;
            powerOfTwo *= 2;
        }
        return result;
    }

    /**
     * Converts positive integer to a hexadecimal string representation.
     *
     * @param n positive integer
     * @param withLeadingZeros whether should append leading zeroes
     * @return hexadecimal string representation of a given number
     */
    private static String getHexString(int n, boolean withLeadingZeros) {
        StringBuilder sb = new StringBuilder();
        if (n == 0) {
            sb.append("0");
        }

        char[] hexDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int number = n;
        while (number != 0) {
            sb.append(hexDigits[number % 16]);
            number >>= 4;
        }

        if (withLeadingZeros) {
            if (sb.length() % 2 != 0) {
                sb.append(0);
            }
        }

        return sb.reverse().toString();
    }

    /**
     * Changes one bit to opposite from the passed binary string.
     *
     * @param binaryString string representing number in a binary system (e.g. 00101011)
     * @param random random number generator
     * @return input string with one bit replaced by opposite bit
     */
    private static String changeBit(String binaryString, Random random) {
        StringBuilder sb = new StringBuilder(binaryString);
        int index = random.nextInt(sb.length());
        sb.setCharAt(index, sb.charAt(index) == '0' ? '1' : '0');
        return sb.toString();
    }

    /**
     * Encodes the message by tripling all the characters.
     *
     * @param message message to encode
     * @return encoded message
     */
    private static String encode(String message) {
        char[] input = message.toCharArray();
        char[] output = new char[3 * input.length];

        for (int i = 0; i < output.length; i++) {
            output[i] = input[i / 3];
        }
        return new String(output);
    }

    /**
     * Decodes the message broken by makeErrors method.
     *
     * @param message message to decode
     * @return decoded message
     */
    private static String decode(String message) {
        char[] input = message.toCharArray();
        char[] output = new char[input.length / 3];

        for (int i = 0; i < output.length; i++) {
            char first = input[3 * i];
            char second = input[3 * i + 1];
            char third = input[3 * i + 2];
            output[i] = (first == second)
                    ? first
                    : ((first == third) ? first : second);
        }
        return new String(output);
    }

    /**
     * Simulates errors caused by wireless connections interference.
     * One random character is replaced by another random one per 3 characters.
     *
     * @param input string to make errors
     * @param  random random number generator
     * @return string with errors
     */
    private static String makeErrors(String input, Random random) {
        char[] chars = input.toCharArray();

        int start = 0;
        int end = 0;
        for (int i = 0; i <= chars.length; i++) {
            if (i > 0 && i % 3 == 0) {
                end = i;
                replaceCharacter(chars, start, end, random);
                start = end;
            }
        }

        return new String(chars);
    }

    /**
     * Replaces one character from start (inclusive) to end (exclusive)
     * by a random character.
     *
     * @param chars array of characters
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     * @param  random random number generator
     */
    private static void replaceCharacter(char[] chars, int start, int end, Random random) {
        int replacedIndex = start + random.nextInt(end - start);
        char randomCharacter = (char) (32 + random.nextInt(95));
        chars[replacedIndex] = randomCharacter;
    }
}
