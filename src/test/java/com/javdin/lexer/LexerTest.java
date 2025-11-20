package com.javdin.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Lexer class.
 */
class LexerTest {
    
    private Lexer lexer;
    
    @Test
    void testBasicTokens() {
        lexer = new Lexer("var x := 42;");
        
        Token token1 = lexer.nextToken();
        assertThat(token1.type()).isEqualTo(TokenType.VAR);
        
        Token token2 = lexer.nextToken();
        assertThat(token2.type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(token2.value()).isEqualTo("x");
        
        Token token3 = lexer.nextToken();
        assertThat(token3.type()).isEqualTo(TokenType.ASSIGN_OP);
        
        Token token4 = lexer.nextToken();
        assertThat(token4.type()).isEqualTo(TokenType.INTEGER);
        assertThat(token4.value()).isEqualTo("42");
        
        Token token5 = lexer.nextToken();
        assertThat(token5.type()).isEqualTo(TokenType.SEMICOLON);
        
        Token token6 = lexer.nextToken();
        assertThat(token6.type()).isEqualTo(TokenType.EOF);
    }
    
    @ParameterizedTest
    @CsvSource({
        "123, INTEGER, 123",
        "3.14, REAL, 3.14",
        "true, TRUE, true",
        "false, FALSE, false",
        "\"hello\", STRING, hello",
        "identifier, IDENTIFIER, identifier"
    })
    void testLiterals(String input, String expectedType, String expectedValue) {
        lexer = new Lexer(input);
        Token token = lexer.nextToken();
        
        assertThat(token.type().name()).isEqualTo(expectedType);
        assertThat(token.value()).isEqualTo(expectedValue);
    }
    
    @Test
    void testKeywords() {
        lexer = new Lexer("if else while for function return print");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IF);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ELSE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.WHILE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FOR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNCTION);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RETURN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PRINT);
    }
    
    @Test
    void testOperators() {
        lexer = new Lexer("+ - * / := /= <= >= = < >");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PLUS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MINUS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MULTIPLY);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.DIVIDE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.NOT_EQUAL_ALT);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LESS_EQUAL);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_EQUAL);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EQUAL);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LESS_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN);
    }
    
    @Test
    void testStringLiteralsWithBothQuotes() {
        lexer = new Lexer("\"Hello, World!\" 'Single quote string' \"\" ''");
        
        Token token1 = lexer.nextToken();
        assertThat(token1.type()).isEqualTo(TokenType.STRING);
        assertThat(token1.value()).isEqualTo("Hello, World!");
        
        Token token2 = lexer.nextToken();
        assertThat(token2.type()).isEqualTo(TokenType.STRING);
        assertThat(token2.value()).isEqualTo("Single quote string");
        
        Token token3 = lexer.nextToken();
        assertThat(token3.type()).isEqualTo(TokenType.STRING);
        assertThat(token3.value()).isEqualTo("");
        
        Token token4 = lexer.nextToken();
        assertThat(token4.type()).isEqualTo(TokenType.STRING);
        assertThat(token4.value()).isEqualTo("");
    }
    
    @Test
    void testComments() {
        lexer = new Lexer("var x; // This is a comment\nvar y;");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.NEWLINE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
    }
    
    @Test
    void testLineAndColumnTracking() {
        lexer = new Lexer("var\nx");
        
        Token token1 = lexer.nextToken();
        assertThat(token1.line()).isEqualTo(1);
        assertThat(token1.column()).isEqualTo(1);
        
        Token token2 = lexer.nextToken();
        assertThat(token2.type()).isEqualTo(TokenType.NEWLINE);
        assertThat(token2.line()).isEqualTo(1);
        assertThat(token2.column()).isEqualTo(4);
        
        Token token3 = lexer.nextToken();
        assertThat(token3.type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(token3.line()).isEqualTo(2);
        assertThat(token3.column()).isEqualTo(1);
    }
    
    @Test
    void testLexicalException() {
        lexer = new Lexer("@");
        
        assertThatThrownBy(() -> lexer.nextToken())
            .isInstanceOf(LexicalException.class)
            .hasMessageContaining("Unexpected character: @");
    }
    
    @Test
    void testMultiLineComments() {
        lexer = new Lexer("var x; /* This is a \n multi-line comment */ var y;");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
    }
    
    @Test
    void testUnterminatedMultiLineComment() {
        lexer = new Lexer("var x; /* This is unterminated");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        
        assertThatThrownBy(() -> lexer.nextToken())
            .isInstanceOf(LexicalException.class)
            .hasMessageContaining("Unterminated multi-line comment");
    }
    
    @Test
    void testStringEscapeSequences() {
        lexer = new Lexer("'Hello\\nWorld' \"Line\\tBreak\" 'Quote\\'s here'");
        
        Token token1 = lexer.nextToken();
        assertThat(token1.type()).isEqualTo(TokenType.STRING);
        assertThat(token1.value()).isEqualTo("Hello\nWorld");
        
        Token token2 = lexer.nextToken();
        assertThat(token2.type()).isEqualTo(TokenType.STRING);
        assertThat(token2.value()).isEqualTo("Line\tBreak");
        
        Token token3 = lexer.nextToken();
        assertThat(token3.type()).isEqualTo(TokenType.STRING);
        assertThat(token3.value()).isEqualTo("Quote's here");
    }
    
    @Test
    void testUnterminatedStringLiteral() {
        lexer = new Lexer("'unterminated");
        
        assertThatThrownBy(() -> lexer.nextToken())
            .isInstanceOf(LexicalException.class)
            .hasMessageContaining("Unterminated string literal");
    }
    
    @Test
    void testMixedTokenTypes() {
        lexer = new Lexer("var count := 42; var pi := 3.14159; var flag := true; var ch := 'X';");
        
        // var count := 42;
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        Token intToken = lexer.nextToken();
        assertThat(intToken.type()).isEqualTo(TokenType.INTEGER);
        assertThat(intToken.value()).isEqualTo("42");
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        
        // var pi := 3.14159;
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        Token realToken = lexer.nextToken();
        assertThat(realToken.type()).isEqualTo(TokenType.REAL);
        assertThat(realToken.value()).isEqualTo("3.14159");
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        
        // var flag := true;
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.TRUE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        
        // var ch := 'X';
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        Token stringToken = lexer.nextToken();
        assertThat(stringToken.type()).isEqualTo(TokenType.STRING);
        assertThat(stringToken.value()).isEqualTo("X");
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
    }
    
    @Test
    void testComplexExpression() {
        lexer = new Lexer("result := (a + b) * c - arr[index].property;");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // result
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);  // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN); // (
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // a
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PLUS);       // +
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // b
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);// )
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MULTIPLY);   // *
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // c
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MINUS);      // -
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // arr
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACKET);  // [
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // index
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACKET); // ]
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.DOT);        // .
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // property
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);  // ;
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EOF);
    }
    
    @Test
    void testLogicalOperators() {
        lexer = new Lexer("if (x > 0 and y < 10 or not flag) { }");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IF);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN); // >
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 0
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.AND); // and
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // y
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LESS_THAN); // <
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 10
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.OR); // or
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.NOT); // not
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // flag
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EOF);
    }
    
    @Test
    void testFunctionDefinition() {
        lexer = new Lexer("function factorial(n) -> { if (n <= 1) return 1; else return n * factorial(n - 1); }");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNCTION);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // factorial
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // n
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ARROW); // ->
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACE);
        // ... continue checking tokens
        // The test validates the arrow operator and function syntax
    }
    
    @Test
    void testNestedComments() {
        // Test that we don't support nested /* */ comments (should end at first */)
        lexer = new Lexer("var x; /* outer /* inner */ still outer */ var y;");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SEMICOLON);
        // After the first */ the comment should end
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // "still"
    }
}
