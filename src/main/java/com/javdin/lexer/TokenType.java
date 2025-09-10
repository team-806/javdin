package com.javdin.lexer;

/**
 * Token types for the Javdin language.
 */
public enum TokenType {
    // Literals
    INTEGER,
    REAL,
    BOOL,
    STRING,
    
    // Identifiers
    IDENTIFIER,
    
    // Keywords
    VAR,
    IF,
    ELSE,
    WHILE,
    FOR,
    FUNCTION,
    RETURN,
    PRINT,
    INPUT,
    TRUE,
    FALSE,
    LAMBDA,
    BREAK,
    CONTINUE,
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    ASSIGN,         // =
    EQUAL,          // ==
    NOT_EQUAL,      // !=
    LESS_THAN,      // <
    LESS_EQUAL,     // <=
    GREATER_THAN,   // >
    GREATER_EQUAL,  // >=
    AND,            // and
    OR,             // or
    NOT,            // not
    
    // Delimiters
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .
    COLON,          // :
    ARROW,          // ->
    
    // Special
    NEWLINE,
    EOF,
    UNKNOWN
}
