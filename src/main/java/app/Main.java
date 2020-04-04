package app;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        String message = scanner.nextLine();
        System.out.println(message);

        message = encode(message);
        System.out.println(message);

        message = makeErrors(message, random);
        System.out.println(message);

        message = decode(message);
        System.out.println(message);
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
