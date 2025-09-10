package com.javdin.integration;

import com.javdin.main.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * End-to-end integration tests for the Javdin interpreter.
 */
class EndToEndTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void testSimpleProgram() throws IOException {
        // Create a simple test program
        Path testFile = tempDir.resolve("test.d");
        Files.writeString(testFile, "var x = 42;\nprint;");
        
        // Capture stdout
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Run the interpreter
            Main.main(new String[]{testFile.toString()});
            
            // Verify output
            String output = outputStream.toString();
            assertThat(output).contains("Print statement executed");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    void testInvalidSyntax() throws IOException {
        // Create a program with syntax errors
        Path testFile = tempDir.resolve("invalid.d");
        Files.writeString(testFile, "var;"); // Missing identifier
        
        // Capture stderr
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStream));
        
        try {
            // This should exit with error code 1
            assertThatThrownBy(() -> Main.main(new String[]{testFile.toString()}))
                .isInstanceOf(SecurityException.class); // System.exit throws this in tests
        } finally {
            System.setErr(originalErr);
        }
    }
    
    @Test
    void testFileNotFound() {
        // Capture stderr
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStream));
        
        try {
            assertThatThrownBy(() -> Main.main(new String[]{"nonexistent.d"}))
                .isInstanceOf(SecurityException.class); // System.exit throws this in tests
            
            String error = errorStream.toString();
            assertThat(error).contains("Error reading file");
        } finally {
            System.setErr(originalErr);
        }
    }
    
    @Test
    void testNoArguments() {
        // Capture stderr
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStream));
        
        try {
            assertThatThrownBy(() -> Main.main(new String[]{}))
                .isInstanceOf(SecurityException.class); // System.exit throws this in tests
            
            String error = errorStream.toString();
            assertThat(error).contains("Usage:");
        } finally {
            System.setErr(originalErr);
        }
    }
}
