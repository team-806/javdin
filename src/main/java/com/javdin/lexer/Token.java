package com.javdin.lexer;

/**
 * Immutable token class representing a lexical unit.
 */
public record Token(
    TokenType type,
    String value,
    int line,
    int column
) {
    
    public Token(TokenType type, int line, int column) {
        this(type, "", line, column);
    }
    
    @Override
    public String toString() {
        if (value.isEmpty()) {
            return String.format("%s at %d:%d", type, line, column);
        }
        return String.format("%s('%s') at %d:%d", type, value, line, column);
    }
}
