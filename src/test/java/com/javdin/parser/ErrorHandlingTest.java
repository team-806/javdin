package com.javdin.parser;

import com.javdin.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for Milestone 10: Error Handling and Recovery.
 * Tests parser error detection and error messages.
 */
class ErrorHandlingTest {

    // ========== Basic Syntax Errors ==========

    @Test
    void testMissingAssignmentOperator() {
        Lexer lexer = new Lexer("var x 5");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("error");
    }

    @Test
    void testMissingVariableName() {
        Lexer lexer = new Lexer("var := 5");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingExpression() {
        Lexer lexer = new Lexer("var x :=");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidToken() {
        Lexer lexer = new Lexer("var @invalid := 5");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(Exception.class); // Could be LexicalException or ParseException
    }

    // ========== Control Flow Errors ==========

    @Test
    void testMissingThenInIf() {
        Lexer lexer = new Lexer("if x > 0 var y := 1 end");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingEndInIf() {
        Lexer lexer = new Lexer("if x > 0 then var y := 1");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingConditionInIf() {
        Lexer lexer = new Lexer("if then var y := 1 end");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingLoopInWhile() {
        Lexer lexer = new Lexer("while x > 0 var y := 1 end");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingEndInWhile() {
        Lexer lexer = new Lexer("while x > 0 loop var y := 1");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingEndInFor() {
        Lexer lexer = new Lexer("for i in 1..10 loop var y := 1");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    // ========== Expression Errors ==========

    @Test
    void testUnmatchedLeftParen() {
        Lexer lexer = new Lexer("var x := (1 + 2");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testUnmatchedRightParen() {
        Lexer lexer = new Lexer("var x := 1 + 2)");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingOperand() {
        Lexer lexer = new Lexer("var x := 1 +");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidOperator() {
        // The ++ is not a single token, it will be parsed as two + operators
        // which is actually valid as unary plus: var x := 1 + (+2)
        // So let's test a truly invalid sequence
        Lexer lexer = new Lexer("var x := 1 @ 2");  // @ is invalid
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(Exception.class);
    }

    // ========== Array/Tuple Errors ==========

    @Test
    void testUnmatchedLeftBracket() {
        Lexer lexer = new Lexer("var x := [1, 2, 3");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testUnmatchedLeftBrace() {
        Lexer lexer = new Lexer("var x := {a := 1, b := 2");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingCommaInArray() {
        Lexer lexer = new Lexer("var x := [1 2 3]");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    // ========== Function Errors ==========

    @Test
    void testMissingEndInFunction() {
        Lexer lexer = new Lexer("var f := func(x) is return x");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingIsInFunction() {
        Lexer lexer = new Lexer("var f := func(x) return x end");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingExpressionAfterShortIf() {
        Lexer lexer = new Lexer("var f := func(x) =>");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    // ========== Assignment Errors ==========

    @Test
    void testInvalidLeftHandSide() {
        Lexer lexer = new Lexer("5 := x");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingRightHandSide() {
        Lexer lexer = new Lexer("x :=");
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    // ========== Error Message Quality Tests ==========

    @Test
    void testErrorMessageContainsUsefulInfo() {
        Lexer lexer = new Lexer("var x 5"); // Missing :=
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class)
            .satisfies(e -> {
                String message = e.getMessage().toLowerCase();
                // Message should indicate some kind of error
                assertThat(message).containsAnyOf("error", "unexpected", "expected", "syntax");
            });
    }

    @Test
    void testMultipleErrors() {
        // Program with multiple syntax errors
        String code = """
            var x 5
            var y
            z := 
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        // Should fail on first error
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }

    // ========== Valid Edge Cases (should NOT throw) ==========

    @Test
    void testEmptyProgram() {
        Lexer lexer = new Lexer("");
        Parser parser = new Parser(lexer);
        
        // Empty program should be valid
        assertThat(parser.parse()).isNotNull();
    }

    @Test
    void testComplexValidProgram() {
        String code = """
            var x := 1
            if x > 0 then
                print x
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        // Should parse successfully
        assertThat(parser.parse()).isNotNull();
    }
}
