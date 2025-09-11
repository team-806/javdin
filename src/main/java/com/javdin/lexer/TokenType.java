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
    CHAR,
    NONE,           // none literal
    
    // Identifiers
    IDENTIFIER,
    
    // Keywords
    VAR,            // var
    IF,             // if
    THEN,           // then
    ELSE,           // else
    END,            // end
    WHILE,          // while
    FOR,            // for
    IN,             // in
    LOOP,           // loop
    EXIT,           // exit
    FUNCTION,       // function
    FUNC,           // func (function literals)
    RETURN,         // return
    PRINT,          // print
    INPUT,          // input
    TRUE,           // true
    FALSE,          // false
    LAMBDA,         // lambda
    BREAK,          // break
    CONTINUE,       // continue
    IS,             // is
    
    // Type indicators
    INT_TYPE,       // int
    REAL_TYPE,      // real
    BOOL_TYPE,      // bool
    STRING_TYPE,    // string
    NONE_TYPE,      // none
    ARRAY_TYPE,     // []
    TUPLE_TYPE,     // {}
    FUNC_TYPE,      // func
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    ASSIGN,         // =
    ASSIGN_OP,      // :=
    EQUAL,          // ==
    NOT_EQUAL,      // !=
    NOT_EQUAL_ALT,  // /=
    LESS_THAN,      // <
    LESS_EQUAL,     // <=
    GREATER_THAN,   // >
    GREATER_EQUAL,  // >=
    AND,            // and
    OR,             // or
    XOR,            // xor
    NOT,            // not
    RANGE,          // ..
    SHORT_IF,       // =>
    
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
