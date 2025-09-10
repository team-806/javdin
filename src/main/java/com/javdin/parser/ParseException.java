package com.javdin.parser;

/**
 * Exception thrown when parsing encounters a syntax error.
 */
public class ParseException extends RuntimeException {
    private final int line;
    private final int column;
    
    public ParseException(String message, int line, int column) {
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
        return String.format("Parse error at line %d, column %d: %s", line, column, getMessage());
    }
}
