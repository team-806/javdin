package com.javdin.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lexer demonstration utility for showing tokenization of Javdin source code.
 * This is useful for demonstrating the lexer's capabilities.
 */
public class LexerDebug {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            // Default demonstration with sample code
            System.out.println("=".repeat(60));
            System.out.println("JAVDIN LEXER DEMONSTRATION");
            System.out.println("=".repeat(60));
            System.out.println("Running with sample code ");
            System.out.println();
            
            String sampleCode = """
                var factorial := func(n) is
                    if n <= 1 then
                        return 1
                    else
                        return n * factorial(n - 1)
                    end
                end
                
                for i in 1..5 loop
                    print factorial(i)
                    if i = 3 => exit
                end
                """;
                
            System.out.println("SAMPLE SOURCE CODE:");
            System.out.println("-".repeat(40));
            printSourceWithLineNumbers(sampleCode);
            System.out.println();
            
            System.out.println("TOKENS:");
            System.out.println("-".repeat(40));
            tokenizeSource(sampleCode);
            return;
        }
        
        try {
            tokenizeFile(args[0]);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (LexicalException e) {
            System.err.println("Lexical error: " + e.toString());
            System.exit(1);
        }
    }
    
    /**
     * Tokenizes a file and prints all tokens with detailed information
     */
    public static void tokenizeFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        String source = Files.readString(path);
        
        System.out.println("=".repeat(60));
        System.out.println("JAVDIN LEXER DEMONSTRATION");
        System.out.println("=".repeat(60));
        System.out.println("File: " + filename);
        System.out.println("Source length: " + source.length() + " characters");
        System.out.println();
        
        System.out.println("SOURCE CODE:");
        System.out.println("-".repeat(40));
        printSourceWithLineNumbers(source);
        System.out.println();
        
        System.out.println("TOKENS:");
        System.out.println("-".repeat(40));
        tokenizeSource(source);
    }
    
    /**
     * Tokenizes source code string and demonstrates all discovered tokens
     */
    public static void tokenizeSource(String source) {
        Lexer lexer = new Lexer(source);
        int tokenCount = 0;
        
        System.out.printf("%-4s %-20s %-15s %-30s %s%n", 
                         "#", "TOKEN_TYPE", "POSITION", "VALUE", "DESCRIPTION");
        System.out.println("-".repeat(85));
        
        Token token;
        do {
            token = lexer.nextToken();
            tokenCount++;
            
            String position = String.format("%d:%d", token.line(), token.column());
            String value = token.value().isEmpty() ? "<no value>" : "\"" + escapeString(token.value()) + "\"";
            String description = getTokenDescription(token);
            
            System.out.printf("%-4d %-20s %-15s %-30s %s%n", 
                             tokenCount, token.type(), position, value, description);
            
        } while (token.type() != TokenType.EOF);
        
        System.out.println("-".repeat(85));
        System.out.println("Total tokens: " + tokenCount);
    }
    
    private static void printSourceWithLineNumbers(String source) {
        String[] lines = source.split("\\n", -1);
        
        for (int i = 0; i < lines.length; i++) {
            System.out.printf("%3d: %s%n", i + 1, lines[i]);
        }
    }
    
    private static String escapeString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\t", "\\t")
                  .replace("\r", "\\r");
    }
    
    private static String getTokenDescription(Token token) {
        return switch (token.type()) {
            case VAR -> "Variable declaration";
            case IF -> "Conditional statement";
            case THEN -> "If-then clause";
            case ELSE -> "If-else clause";  
            case END -> "Block terminator";
            case WHILE -> "While loop";
            case FOR -> "For loop";
            case IN -> "For-in loop element";
            case LOOP -> "Loop body start";
            case EXIT -> "Loop exit statement";
            case FUNC -> "Function literal";
            case FUNCTION -> "Function declaration";
            case RETURN -> "Return statement";
            case PRINT -> "Print statement";
            case TRUE, FALSE -> "Boolean literal";
            case NONE -> "None literal";
            case INTEGER -> "Integer literal";
            case REAL -> "Real number literal"; 
            case STRING -> "String literal";
            case IDENTIFIER -> "Identifier/variable name";
            case ASSIGN -> "Assignment (=)";
            case ASSIGN_OP -> "Assignment (:=)"; 
            case EQUAL -> "Equality comparison (==)";
            case NOT_EQUAL -> "Inequality (!=)";
            case NOT_EQUAL_ALT -> "Inequality (/=)";
            case LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL -> "Comparison operator";
            case PLUS, MINUS, MULTIPLY, DIVIDE -> "Arithmetic operator";
            case AND, OR, XOR -> "Logical operator";
            case NOT -> "Logical negation";
            case IS -> "Type checking operator";
            case ARROW -> "Function arrow (->)";
            case SHORT_IF -> "Short if (=>)";
            case RANGE -> "Range operator (..)";
            case INT_TYPE, REAL_TYPE, BOOL_TYPE, STRING_TYPE -> "Type indicator";
            case ARRAY_TYPE -> "Array type indicator ([])";
            case TUPLE_TYPE -> "Tuple type indicator ({})";
            case LEFT_PAREN, RIGHT_PAREN -> "Parenthesis";
            case LEFT_BRACE, RIGHT_BRACE -> "Brace";
            case LEFT_BRACKET, RIGHT_BRACKET -> "Bracket";
            case DOT -> "Dot/member access";
            case COMMA -> "Comma separator";
            case SEMICOLON -> "Statement separator";
            case COLON -> "Colon";
            case NEWLINE -> "Line break";
            case EOF -> "End of file";
            default -> "Unknown token";
        };
    }
}
