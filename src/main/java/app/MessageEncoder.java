package app;

import java.util.Random;

public class MessageEncoder {

    private Random random = new Random();

    /**
     * Converts positive integer in the range from 0 to 255 to a binary string representation.
     *
     * @param n positive integer from 0 to 255
     * @param withLeadingZeros whether should append leading zeroes
     * @return binary string representation of a given number
     */
    public String getBinaryString(int n, boolean withLeadingZeros) {
        StringBuilder sb = new StringBuilder();
        if (n == 0) {
            sb.append("0");
        }

        int number = n & 0b11111111;
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
     * Converts array of bytes to a binary string representation.
     *
     * @param bytes byte array to convert
     * @return binary string representation of a given byte array
     */
    public String getBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(getBinaryString(b, true)).append(' ');
        }
        return sb.toString();
    }

    /**
     * Converts given binary string to integer value.
     *
     * @param binary binary string representation of a number
     * @return integer value
     */
    public int getIntegerFromBinaryString(String binary) {
        char[] bits = binary.toCharArray();

        int result = 0;
        int powerOfTwo = 1;
        for (int i = bits.length - 1; i >= 0; i--) {
            result = bits[i] == '0' ? result : result + powerOfTwo;
            powerOfTwo <<= 1;
        }
        return result;
    }

    /**
     * Converts positive integer in the range from 0 to 255 to a hexadecimal string representation.
     *
     * @param n positive integer from 0 to 255
     * @param withLeadingZeros whether should append leading zeroes
     * @return hexadecimal string representation of a given number
     */
    public String getHexString(int n, boolean withLeadingZeros) {
        StringBuilder sb = new StringBuilder();
        if (n == 0) {
            sb.append("0");
        }

        char[] hexDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int number = n & 0b11111111;
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
     * Converts array of bytes to a hexadecimal string representation.
     *
     * @param bytes byte array to convert
     * @return hexadecimal string representation of a given byte array
     */
    public String getHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(getHexString(b, true)).append(' ');
        }
        return sb.toString();
    }

    /* BIT LEVEL OPERATIONS */

    /**
     * Encodes byte array by writing every bit twice, in every byte 2 last bits are bits of parity.
     *
     * @param bytes bytes to encode
     * @return encoded bytes
     */
    public byte[] encode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        int triplesCount = 0;
        byte[] triples = new byte[3];
        for (byte b : bytes) {
            int mask = 0b10000000;

            byte bit;
            while (mask != 0) {
                bit = (byte) ((b & mask) == 0 ? 0 : 1);
                sb.append(bit).append(bit);
                triples[triplesCount] = bit;
                triplesCount++;

                if (triplesCount % 3 == 0) {
                    byte parity = (byte) (triples[0] ^ triples[1] ^ triples[2]);
                    sb.append(parity).append(parity).append(' ');
                    triplesCount = 0;
                }

                mask >>= 1;
            }
        }

        if (triplesCount != 0) {
            for (int i = triplesCount; i < 3; i++) {
                sb.append(0).append(0);
                triples[i] = 0;
            }

            byte parity = (byte) (triples[0] ^ triples[1] ^ triples[2]);
            sb.append(parity).append(parity);
        }

        String[] parts = sb.toString().split(" ");
        byte[] output = new byte[parts.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) getIntegerFromBinaryString(parts[i]);
        }
        return output;
    }

    /**
     * Decodes bytes encoded with encode method.
     *
     * @param bytes bytes to decode
     * @return decoded bytes
     */
    public byte[] decode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        int bitsCount = 0;
        int corruptedBit = -1;
        byte[] triples = new byte[3];
        for (byte b : bytes) {
            char[] bits = getBinaryString(b, true).toCharArray();

            for (int i = 0; i < 6; i += 2) {
                if (bits[i] == bits[i + 1]) {
                    triples[i / 2] = (byte) (bits[i] == '0' ? 0 : 1);
                } else {
                    triples[i / 2] = 0;
                    corruptedBit = i / 2;
                }
            }

            if (corruptedBit != -1) {
                byte parity = (byte) (bits[6] == '0' ? 0 : 1);
                if ((triples[0] ^ triples[1] ^ triples[2]) != parity) {
                    triples[corruptedBit] = 1;
                }
            }

            for (int i = 0; i < 3; i++) {
                sb.append(triples[i]);
                bitsCount++;

                if (bitsCount == 8) {
                    sb.append(' ');
                    bitsCount = 0;
                }
            }

            corruptedBit = -1;
        }

        int lastSpace = sb.lastIndexOf(" ");
        sb.delete(lastSpace, sb.length());

        String[] parts = sb.toString().split(" ");
        byte[] output = new byte[parts.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) getIntegerFromBinaryString(parts[i]);
        }
        return output;
    }

    /**
     * Changes one bit to opposite one in every byte from the passed byte array.
     *
     * @param bytes array of bytes to change
     * @return byte array containing bytes with one bit replaced by opposite bit per every byte
     */
    public byte[] changeBits(byte[] bytes) {
        byte[] output = new byte[bytes.length];

        for (int i = 0; i < output.length; i++) {
            char[] bits = getBinaryString(bytes[i], true).toCharArray();
            int index = random.nextInt(bits.length);
            bits[index] = bits[index] == '0' ? '1' : '0';
            output[i] = (byte) getIntegerFromBinaryString(new String(bits));
        }

        return output;
    }

    /* SYMBOL LEVEL OPERATIONS */

    /**
     * Encodes the message by tripling all the characters.
     *
     * @param message message to encode
     * @return encoded message
     */
    public String encode(String message) {
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
    public String decode(String message) {
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
    public String makeErrors(String input, Random random) {
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
    private void replaceCharacter(char[] chars, int start, int end, Random random) {
        int replacedIndex = start + random.nextInt(end - start);
        char randomCharacter = (char) (32 + random.nextInt(95));
        chars[replacedIndex] = randomCharacter;
    }
}
