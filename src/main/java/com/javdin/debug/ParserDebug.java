package com.javdin.debug;

import com.javdin.lexer.Lexer;
import com.javdin.lexer.Token;

public class ParserDebug {
    public static void main(String[] args) {
        String input = "var x = 42;\nprint;";
        System.out.println("Input: '" + input + "'");
        System.out.println("Length: " + input.length());
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            System.out.println("Position " + (i+1) + ": '" + c + "' (" + (int)c + ")");
        }
        
        System.out.println("\nTokens:");
        Lexer lexer = new Lexer(input);
        Token token;
        int tokenCount = 0;
        do {
            token = lexer.nextToken();
            tokenCount++;
            System.out.println("Token " + tokenCount + ": " + token);
        } while (token.type().name() != "EOF" && tokenCount < 10);
    }
}
