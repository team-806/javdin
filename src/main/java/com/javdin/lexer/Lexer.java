package com.javdin.lexer;

import java.util.HashMap;
import java.util.Map;

/**
 * Lexical analyzer for the Javdin language.
 * Converts source code into a stream of tokens.
 */
public class Lexer {
    private final String source;
    private int position;
    private int line;
    private int column;
    
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        KEYWORDS.put("var", TokenType.VAR);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("then", TokenType.THEN);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("end", TokenType.END);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("in", TokenType.IN);
        KEYWORDS.put("loop", TokenType.LOOP);
        KEYWORDS.put("exit", TokenType.EXIT);
        KEYWORDS.put("function", TokenType.FUNCTION);
        KEYWORDS.put("func", TokenType.FUNC);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("input", TokenType.INPUT);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("lambda", TokenType.LAMBDA);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
        KEYWORDS.put("and", TokenType.AND);
        KEYWORDS.put("or", TokenType.OR);
        KEYWORDS.put("xor", TokenType.XOR);
        KEYWORDS.put("not", TokenType.NOT);
        KEYWORDS.put("is", TokenType.IS);
        KEYWORDS.put("none", TokenType.NONE);
        // Type indicators
        KEYWORDS.put("int", TokenType.INT_TYPE);
        KEYWORDS.put("real", TokenType.REAL_TYPE);
        KEYWORDS.put("bool", TokenType.BOOL_TYPE);
        KEYWORDS.put("string", TokenType.STRING_TYPE);
        KEYWORDS.put("array", TokenType.ARRAY_TYPE);
        KEYWORDS.put("tuple", TokenType.TUPLE_TYPE);
    }
    
    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }
    
    /**
     * Returns the next token from the source code.
     */
    public Token nextToken() {
        skipWhitespace();
        
        if (position >= source.length()) {
            return new Token(TokenType.EOF, line, column);
        }
        
        char current = source.charAt(position);
        
        // Handle newlines
        if (current == '\n') {
            Token token = new Token(TokenType.NEWLINE, line, column);
            advance();
            return token;
        }
        
        // Handle /= (not equal) before checking for // comments
        if (current == '/' && peek() == '=') {
            Token token = new Token(TokenType.NOT_EQUAL_ALT, line, column);
            advance();
            advance();
            return token;
        }
        
        // Handle comments
        if (current == '/' && peek() == '/') {
            skipLineComment();
            return nextToken();
        }
        
        if (current == '/' && peek() == '*') {
            skipMultiLineComment();
            return nextToken();
        }
        
        // Handle multi-character operators
        if (current == ':' && peek() == '=') {
            Token token = new Token(TokenType.ASSIGN_OP, line, column);
            advance();
            advance();
            return token;
        }
        
        if (current == '=' && peek() == '>') {
            Token token = new Token(TokenType.SHORT_IF, line, column);
            advance();
            advance();
            return token;
        }
        
        if (current == '=' && peek() == '=') {
            Token token = new Token(TokenType.EQUAL, line, column);
            advance();
            advance();
            return token;
        }

        if (current == '!' && peek() == '=') {
            Token token = new Token(TokenType.NOT_EQUAL, line, column);
            advance();
            advance();
            return token;
        }

        if (current == '<' && peek() == '=') {
            Token token = new Token(TokenType.LESS_EQUAL, line, column);
            advance();
            advance();
            return token;
        }

        if (current == '>' && peek() == '=') {
            Token token = new Token(TokenType.GREATER_EQUAL, line, column);
            advance();
            advance();
            return token;
        }

        if (current == '-' && peek() == '>') {
            Token token = new Token(TokenType.ARROW, line, column);
            advance();
            advance();
            return token;
        }
        
        if (current == '.' && peek() == '.') {
            Token token = new Token(TokenType.RANGE, line, column);
            advance();
            advance();
            return token;
        }
        
        // Handle single-character tokens
        TokenType singleChar = getSingleCharToken(current);
        if (singleChar != null) {
            Token token = new Token(singleChar, line, column);
            advance();
            return token;
        }
        
        // Handle numbers
        if (Character.isDigit(current)) {
            return scanNumber();
        }
        
        // Handle strings (both single and double quotes)
        if (current == '"' || current == '\'') {
            return scanString(current);
        }        // Handle identifiers and keywords
        if (Character.isLetter(current) || current == '_') {
            return scanIdentifier();
        }
        
        // Unknown character
        throw new LexicalException("Unexpected character: " + current, line, column);
    }
    
    private void skipWhitespace() {
        while (position < source.length()) {
            char c = source.charAt(position);
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else {
                break;
            }
        }
    }
    
    private void skipLineComment() {
        while (position < source.length() && source.charAt(position) != '\n') {
            advance();
        }
    }
    
    private void skipMultiLineComment() {
        advance(); // Skip '/'
        advance(); // Skip '*'
        
        while (position < source.length() - 1) {
            if (source.charAt(position) == '*' && source.charAt(position + 1) == '/') {
                advance(); // Skip '*'
                advance(); // Skip '/'
                return;
            }
            advance();
        }
        
        // Unterminated comment
        throw new LexicalException("Unterminated multi-line comment", line, column);
    }
    
    private Token scanNumber() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (position < source.length() && Character.isDigit(source.charAt(position))) {
            sb.append(source.charAt(position));
            advance();
        }
        
        // Check for decimal point
        if (position < source.length() && source.charAt(position) == '.' && 
            position + 1 < source.length() && Character.isDigit(source.charAt(position + 1))) {
            sb.append(source.charAt(position));
            advance();
            
            while (position < source.length() && Character.isDigit(source.charAt(position))) {
                sb.append(source.charAt(position));
                advance();
            }
            
            return new Token(TokenType.REAL, sb.toString(), startLine, startColumn);
        }
        
        return new Token(TokenType.INTEGER, sb.toString(), startLine, startColumn);
    }
    
    private Token scanString(char quote) {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        advance(); // Skip opening quote
        
        while (position < source.length() && source.charAt(position) != quote) {
            if (source.charAt(position) == '\\') {
                advance();
                if (position < source.length()) {
                    char escaped = source.charAt(position);
                    switch (escaped) {
                        case 'n' -> sb.append('\n');
                        case 't' -> sb.append('\t');
                        case 'r' -> sb.append('\r');
                        case '\\' -> sb.append('\\');
                        case '"' -> sb.append('"');
                        case '\'' -> sb.append('\'');
                        default -> sb.append(escaped);
                    }
                    advance();
                }
            } else {
                sb.append(source.charAt(position));
                advance();
            }
        }
        
        if (position >= source.length()) {
            throw new LexicalException("Unterminated string literal", startLine, startColumn);
        }
        
        advance(); // Skip closing quote
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }
    
    private Token scanIdentifier() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        
        while (position < source.length() && 
               (Character.isLetterOrDigit(source.charAt(position)) || source.charAt(position) == '_')) {
            sb.append(source.charAt(position));
            advance();
        }
        
        String identifier = sb.toString();
        TokenType type = KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER);
        
        return new Token(type, identifier, startLine, startColumn);
    }
    
    private TokenType getSingleCharToken(char c) {
        return switch (c) {
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '*' -> TokenType.MULTIPLY;
            case '/' -> TokenType.DIVIDE;
            case '%' -> TokenType.MODULO;
            case '=' -> TokenType.EQUAL;
            case '<' -> TokenType.LESS_THAN;
            case '>' -> TokenType.GREATER_THAN;
            case '(' -> TokenType.LEFT_PAREN;
            case ')' -> TokenType.RIGHT_PAREN;
            case '{' -> TokenType.LEFT_BRACE;
            case '}' -> TokenType.RIGHT_BRACE;
            case '[' -> TokenType.LEFT_BRACKET;
            case ']' -> TokenType.RIGHT_BRACKET;
            case ';' -> TokenType.SEMICOLON;
            case ',' -> TokenType.COMMA;
            case '.' -> TokenType.DOT;
            case ':' -> TokenType.COLON;
            default -> null;
        };
    }
    
    private char peek() {
        if (position + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(position + 1);
    }
    
    private void advance() {
        if (position < source.length() && source.charAt(position) == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        position++;
    }
}
