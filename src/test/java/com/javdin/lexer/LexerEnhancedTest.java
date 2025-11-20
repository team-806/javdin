package com.javdin.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Additional comprehensive tests for the enhanced Javdin lexer.
 * Tests all tokens required by the Project D specification.
 */
class LexerEnhancedTest {
    
    private Lexer lexer;
    
    @Test
    void testNewKeywords() {
        lexer = new Lexer("then end loop exit in is func none xor");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.THEN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.END);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LOOP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EXIT);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNC);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.NONE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.XOR);
    }
    
    @Test
    void testTypeIndicators() {
        lexer = new Lexer("int real bool string");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INT_TYPE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.REAL_TYPE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.BOOL_TYPE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.STRING_TYPE);
    }
    
    @Test
    void testNewOperators() {
        lexer = new Lexer(":= => /= ..");
        
        Token assign = lexer.nextToken();
        assertThat(assign.type()).isEqualTo(TokenType.ASSIGN_OP);
        assertThat(assign.value()).isEqualTo("");
        
        Token shortIf = lexer.nextToken();
        assertThat(shortIf.type()).isEqualTo(TokenType.SHORT_IF);
        
        Token notEqualAlt = lexer.nextToken();
        assertThat(notEqualAlt.type()).isEqualTo(TokenType.NOT_EQUAL_ALT);
        
        Token range = lexer.nextToken();
        assertThat(range.type()).isEqualTo(TokenType.RANGE);
    }
    
    @Test
    void testSpecialTypeTokens() {
        lexer = new Lexer("[] {}");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACKET);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACKET);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACE);
    }
    
    @Test
    void testProjectDVariableDeclaration() {
        lexer = new Lexer("var x := 42");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
    }
    
    @Test
    void testProjectDIfStatement() {
        lexer = new Lexer("if x > 0 then print \"positive\" else print \"non-positive\" end");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IF);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 0
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.THEN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PRINT);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.STRING);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ELSE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PRINT);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.STRING);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.END);
    }
    
    @Test
    void testProjectDShortIfStatement() {
        lexer = new Lexer("if x > 100 => exit");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IF);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.SHORT_IF);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EXIT);
    }
    
    @Test
    void testProjectDForLoop() {
        lexer = new Lexer("for i in 1..10 loop print i end");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FOR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // i
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 1
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RANGE); // ..
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 10
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LOOP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PRINT);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // i
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.END);
    }
    
    @Test
    void testProjectDWhileLoop() {
        lexer = new Lexer("while x > 0 loop x := x - 1 end");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.WHILE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LOOP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MINUS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.END);
    }
    
    @Test
    void testProjectDFunctionLiteral() {
        lexer = new Lexer("func(x, y) is x + y end");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNC);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // y
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.PLUS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // y
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.END);
    }
    
    @Test
    void testProjectDFunctionExpression() {
        lexer = new Lexer("func(x) -> x * x");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNC);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ARROW);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.MULTIPLY);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
    }
    
    @Test
    void testProjectDTypeChecking() {
        lexer = new Lexer("x is int");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IS);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INT_TYPE);
    }
    
    @Test
    void testArrayLiterals() {
        lexer = new Lexer("[1, 2, 3, 'hello', true]");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACKET);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.STRING);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.TRUE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACKET);
    }
    
    @Test
    void testTupleLiterals() {
        lexer = new Lexer("{a := 5, b := 'test', 3.14}");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // a
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP); // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 5
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // b
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP); // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.STRING); // 'test'
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.COMMA);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.REAL); // 3.14
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACE);
    }
    
    @Test
    void testLogicalOperators() {
        lexer = new Lexer("true and false or true xor false");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.TRUE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.AND);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FALSE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.OR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.TRUE);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.XOR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FALSE);
    }
    
    @Test
    void testComparisonOperators() {
        lexer = new Lexer("x < y <= z > a >= b = c /= d");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // x
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LESS_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // y
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LESS_EQUAL);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // z
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_THAN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // a
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.GREATER_EQUAL);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // b
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.EQUAL); // = (equality comparison)
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // c
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.NOT_EQUAL_ALT); // /= (not-equal in Project D)
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // d
    }
    
    @Test
    void testIndividualLiterals() {
        // String literal
        lexer = new Lexer("'Hello World!'");
        Token stringToken = lexer.nextToken();
        assertThat(stringToken.type()).isEqualTo(TokenType.STRING);
        assertThat(stringToken.value()).isEqualTo("Hello World!");
        
        // Double quote string
        lexer = new Lexer("\"Also a string\"");
        Token doubleQuoteToken = lexer.nextToken();
        assertThat(doubleQuoteToken.type()).isEqualTo(TokenType.STRING);
        assertThat(doubleQuoteToken.value()).isEqualTo("Also a string");
        
        // Integer
        lexer = new Lexer("42");
        Token intToken = lexer.nextToken();
        assertThat(intToken.type()).isEqualTo(TokenType.INTEGER);
        assertThat(intToken.value()).isEqualTo("42");
        
        // Real
        lexer = new Lexer("3.14159");
        Token realToken = lexer.nextToken();
        assertThat(realToken.type()).isEqualTo(TokenType.REAL);
        assertThat(realToken.value()).isEqualTo("3.14159");
        
        // Boolean true
        lexer = new Lexer("true");
        Token trueToken = lexer.nextToken();
        assertThat(trueToken.type()).isEqualTo(TokenType.TRUE);
        assertThat(trueToken.value()).isEqualTo("true");
        
        // Boolean false
        lexer = new Lexer("false");
        Token falseToken = lexer.nextToken();
        assertThat(falseToken.type()).isEqualTo(TokenType.FALSE);
        assertThat(falseToken.value()).isEqualTo("false");
        
        // None literal
        lexer = new Lexer("none");
        Token noneToken = lexer.nextToken();
        assertThat(noneToken.type()).isEqualTo(TokenType.NONE);
        assertThat(noneToken.value()).isEqualTo("none");
    }
    
    @Test
    void testEmptyStrings() {
        lexer = new Lexer("'' \"\"");
        
        Token token1 = lexer.nextToken();
        assertThat(token1.type()).isEqualTo(TokenType.STRING);
        assertThat(token1.value()).isEqualTo("");
        
        Token token2 = lexer.nextToken();
        assertThat(token2.type()).isEqualTo(TokenType.STRING);
        assertThat(token2.value()).isEqualTo("");
    }
    
    @Test
    void testComplexProjectDProgram() {
        String program = """
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
        
        lexer = new Lexer(program);
        
        // Test a few key tokens from the program
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.VAR);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // factorial
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP); // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.FUNC);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // n
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_PAREN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IS);
        
        // Skip to the interesting part with newlines
        skipUntil(TokenType.FOR);
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // i
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IN);
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 1
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RANGE); // ..
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 5
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LOOP);
    }
    
    private void skipUntil(TokenType targetType) {
        Token token;
        do {
            token = lexer.nextToken();
        } while (token.type() != targetType && token.type() != TokenType.EOF);
    }
    
    @Test 
    void testProjectDArrayAccess() {
        lexer = new Lexer("arr[index] := value");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // arr
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.LEFT_BRACKET); // [
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // index
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.RIGHT_BRACKET); // ]
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP); // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // value
    }
    
    @Test
    void testProjectDTupleAccess() {
        lexer = new Lexer("tup.field := tup.1");
        
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // tup
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.DOT); // .
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // field
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.ASSIGN_OP); // :=
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.IDENTIFIER); // tup
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.DOT); // .
        assertThat(lexer.nextToken().type()).isEqualTo(TokenType.INTEGER); // 1
    }
}
