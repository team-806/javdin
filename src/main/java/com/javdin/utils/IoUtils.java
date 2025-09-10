package com.javdin.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Utility class for I/O operations.
 */
public class IoUtils {
    
    /**
     * Read the entire contents of a file as a string.
     */
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
    
    /**
     * Read a line from standard input.
     */
    public static String readLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    
    /**
     * Read an integer from standard input.
     */
    public static int readInt() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
    
    /**
     * Read a double from standard input.
     */
    public static double readDouble() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextDouble();
    }
    
    /**
     * Print a message to standard output.
     */
    public static void print(Object message) {
        System.out.print(message);
    }
    
    /**
     * Print a message to standard output with newline.
     */
    public static void println(Object message) {
        System.out.println(message);
    }
}
