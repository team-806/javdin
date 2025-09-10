package com.javdin.lexer;

/**
 * Exception thrown when lexical analysis encounters an invalid token.
 */
public class LexicalException extends RuntimeException {
    private final int line;
    private final int column;
    
    public LexicalException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("Lexical error at line %d, column %d: %s", line, column, getMessage());
    }
}
