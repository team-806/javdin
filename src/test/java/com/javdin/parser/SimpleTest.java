package com.javdin.parser;

import com.javdin.ast.*;
import com.javdin.lexer.Lexer;

public class SimpleTest {
    public static void main(String[] args) {
        String testCase = "var f := func(x) => x * 2;";
        
        System.out.println("Testing: " + testCase);
        try {
            Lexer lexer = new Lexer(testCase);
            System.out.println("Tokens:");
            while (true) {
                com.javdin.lexer.Token token = lexer.nextToken();
                System.out.println("  " + token.type() + ": " + token.value());
                if (token.type() == com.javdin.lexer.TokenType.EOF) break;
            }
            
            lexer = new Lexer(testCase);
            Parser parser = new Parser(lexer);
            ProgramNode program = parser.parse();
            System.out.println("✓ Parsed successfully!");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
