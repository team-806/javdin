package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.lexer.Token;
import com.javdin.lexer.TokenType;
import com.javdin.parser.generated.Symbols;
import java_cup.runtime.Symbol;

/**
 * Adapter class that bridges our custom Lexer to CUP's Scanner interface.
 * Converts Token objects from our lexer to Symbol objects expected by CUP.
 */
public class LexerAdapter implements java_cup.runtime.Scanner {
    private final Lexer lexer;
    private Token currentToken;
    
    public LexerAdapter(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = null;
    }
    
    /**
     * Get the next token from the lexer and convert it to a CUP Symbol.
     * This method is called by the CUP-generated parser.
     */
    @Override
    public Symbol next_token() throws Exception {
        currentToken = lexer.nextToken();
        
        int symbolId = mapTokenTypeToSymbol(currentToken.type());
        Object value = extractTokenValue(currentToken);
        
        return new Symbol(symbolId, currentToken.line(), currentToken.column(), value);
    }
    
    /**
     * Maps our TokenType enum to CUP's symbol constants.
     */
    private int mapTokenTypeToSymbol(TokenType tokenType) {
        return switch (tokenType) {
            // Literals with values
            case IDENTIFIER -> Symbols.IDENTIFIER;
            case INTEGER -> Symbols.INTEGER;
            case REAL -> Symbols.REAL;
            case STRING -> Symbols.STRING;
            case BOOL -> Symbols.BOOL;
            
            // Keywords
            case VAR -> Symbols.VAR;
            case IF -> Symbols.IF;
            case THEN -> Symbols.THEN;
            case ELSE -> Symbols.ELSE;
            case END -> Symbols.END;
            case WHILE -> Symbols.WHILE;
            case FOR -> Symbols.FOR;
            case IN -> Symbols.IN;
            case LOOP -> Symbols.LOOP;
            case EXIT -> Symbols.EXIT;
            case FUNC -> Symbols.FUNC;
            case IS -> Symbols.IS;
            case RETURN -> Symbols.RETURN;
            case PRINT -> Symbols.PRINT;
            case TRUE -> Symbols.TRUE;
            case FALSE -> Symbols.FALSE;
            case NONE -> Symbols.NONE;
            
            // Type indicators
            case INT_TYPE -> Symbols.INT_TYPE;
            case REAL_TYPE -> Symbols.REAL_TYPE;
            case BOOL_TYPE -> Symbols.BOOL_TYPE;
            case STRING_TYPE -> Symbols.STRING_TYPE;
            case NONE_TYPE -> Symbols.NONE_TYPE;
            case ARRAY_TYPE -> Symbols.ARRAY_TYPE;
            case TUPLE_TYPE -> Symbols.TUPLE_TYPE;
            case FUNC_TYPE -> Symbols.FUNC_TYPE;
            
            //  Operators
            case PLUS -> Symbols.PLUS;
            case MINUS -> Symbols.MINUS;
            case MULTIPLY -> Symbols.MULTIPLY;
            case DIVIDE -> Symbols.DIVIDE;
            case ASSIGN -> Symbols.ASSIGN_OP;  // Map = to := for backward compatibility
            case ASSIGN_OP -> Symbols.ASSIGN_OP;
            case EQUAL -> Symbols.EQUAL;
            case NOT_EQUAL -> Symbols.NOT_EQUAL;
            case NOT_EQUAL_ALT -> Symbols.NOT_EQUAL_ALT;
            case LESS_THAN -> Symbols.LESS_THAN;
            case LESS_EQUAL -> Symbols.LESS_EQUAL;
            case GREATER_THAN -> Symbols.GREATER_THAN;
            case GREATER_EQUAL -> Symbols.GREATER_EQUAL;
            case AND -> Symbols.AND;
            case OR -> Symbols.OR;
            case XOR -> Symbols.XOR;
            case NOT -> Symbols.NOT;
            case RANGE -> Symbols.RANGE;
            case SHORT_IF -> Symbols.SHORT_IF;
            
            // Delimiters
            case LEFT_PAREN -> Symbols.LEFT_PAREN;
            case RIGHT_PAREN -> Symbols.RIGHT_PAREN;
            case LEFT_BRACE -> Symbols.LEFT_BRACE;
            case RIGHT_BRACE -> Symbols.RIGHT_BRACE;
            case LEFT_BRACKET -> Symbols.LEFT_BRACKET;
            case RIGHT_BRACKET -> Symbols.RIGHT_BRACKET;
            case SEMICOLON -> Symbols.SEMICOLON;
            case COMMA -> Symbols.COMMA;
            case DOT -> Symbols.DOT;
            case COLON -> Symbols.COLON;
            
            // Special
            case NEWLINE -> Symbols.NEWLINE;
            case EOF -> Symbols.EOF;
            
            // Fallback for any unmapped tokens
            default -> throw new RuntimeException(
                "Unmapped token type: " + tokenType + " at line " + 
                currentToken.line() + ", column " + currentToken.column()
            );
        };
    }
    
    /**
     * Extracts the semantic value from a token.
     * For literals and identifiers, returns the actual value.
     * For keywords and operators, returns null (they're recognized by their symbol ID).
     */
    private Object extractTokenValue(Token token) {
        return switch (token.type()) {
            case IDENTIFIER -> token.value(); // String
            case INTEGER -> {
                // Convert string to Integer
                try {
                    yield Integer.parseInt(token.value());
                } catch (NumberFormatException e) {
                    throw new RuntimeException(
                        "Invalid integer literal: " + token.value() + 
                        " at line " + token.line() + ", column " + token.column()
                    );
                }
            }
            case REAL -> {
                // Convert string to Double
                try {
                    yield Double.parseDouble(token.value());
                } catch (NumberFormatException e) {
                    throw new RuntimeException(
                        "Invalid real literal: " + token.value() + 
                        " at line " + token.line() + ", column " + token.column()
                    );
                }
            }
            case STRING -> token.value(); // String (without quotes)
            case TRUE -> Boolean.TRUE;
            case FALSE -> Boolean.FALSE;
            // For all other tokens (keywords, operators, etc.), no semantic value needed
            default -> null;
        };
    }
}
