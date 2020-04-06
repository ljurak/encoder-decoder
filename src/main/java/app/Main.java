package app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MessageEncoder encoder = new MessageEncoder();

        System.out.print("Write a mode: ");
        ProgramMode mode;
        while ((mode = ProgramMode.valueOfMode(scanner.nextLine().toLowerCase())) != null) {
            System.out.println();
            switch (mode) {
                case ENCODE:
                    processEncoding(encoder);
                    break;
                case SEND:
                    processSending(encoder);
                    break;
                case DECODE:
                    processDecoding(encoder);
                    break;
                default:
                    break;
            }
            System.out.print("Write a mode: ");
        }
    }

    private static void processEncoding(MessageEncoder encoder) {
        try (InputStream in = Files.newInputStream(Paths.get("send.txt"));
             ByteArrayOutputStream bout = new ByteArrayOutputStream();
             OutputStream out = Files.newOutputStream(Paths.get("encoded.txt"))) {
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }

            byte[] bytes = bout.toByteArray();

            System.out.println("send.txt:");
            System.out.println("text view: " + new String(bytes));
            System.out.println("hex view: " + encoder.getHexString(bytes));
            System.out.println("bin view: " + encoder.getBinaryString(bytes));
            System.out.println();

            byte[] encodedBytes = encoder.encode(bytes);
            out.write(encodedBytes);

            System.out.println("encoded.txt:");
            System.out.println("hex view: " + encoder.getHexString(encodedBytes));
            System.out.println("bin view: " + encoder.getBinaryString(encodedBytes));
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error occurred during reading/writing from/to a file");
        }
    }

    private static void processSending(MessageEncoder encoder) {
        try (InputStream in = Files.newInputStream(Paths.get("encoded.txt"));
             ByteArrayOutputStream bout = new ByteArrayOutputStream();
             OutputStream out = Files.newOutputStream(Paths.get("received.txt"))) {
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }

            byte[] bytes = bout.toByteArray();

            System.out.println("encoded.txt:");
            System.out.println("hex view: " + encoder.getHexString(bytes));
            System.out.println("bin view: " + encoder.getBinaryString(bytes));
            System.out.println();

            byte[] corruptedBytes = encoder.changeBits(bytes);
            out.write(corruptedBytes);

            System.out.println("received.txt:");
            System.out.println("hex view: " + encoder.getHexString(corruptedBytes));
            System.out.println("bin view: " + encoder.getBinaryString(corruptedBytes));
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error occurred during reading/writing from/to a file");
        }
    }

    private static void processDecoding(MessageEncoder encoder) {
        try (InputStream in = Files.newInputStream(Paths.get("received.txt"));
             ByteArrayOutputStream bout = new ByteArrayOutputStream();
             OutputStream out = Files.newOutputStream(Paths.get("decoded.txt"))) {
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }

            byte[] bytes = bout.toByteArray();

            System.out.println("received.txt:");
            System.out.println("hex view: " + encoder.getHexString(bytes));
            System.out.println("bin view: " + encoder.getBinaryString(bytes));
            System.out.println();

            byte[] decodedBytes = encoder.decode(bytes);
            out.write(decodedBytes);

            System.out.println("decoded.txt:");
            System.out.println("text view: " + new String(decodedBytes));
            System.out.println("hex view: " + encoder.getHexString(decodedBytes));
            System.out.println("bin view: " + encoder.getBinaryString(decodedBytes));
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error occurred during reading/writing from/to a file");
        }
    }
}
