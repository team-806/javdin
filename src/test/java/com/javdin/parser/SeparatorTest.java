package com.javdin.parser;

import com.javdin.ast.*;
import com.javdin.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for Milestone 9: Statement Organization and Separators.
 * Tests all aspects of statement separators (semicolons and newlines).
 */
class SeparatorTest {

    // ========== Basic Separator Tests ==========

    @Test
    void testSingleStatementNoSeparator() {
        Lexer lexer = new Lexer("var x := 1");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
    }

    @Test
    void testTwoStatementsWithNewline() {
        Lexer lexer = new Lexer("var x := 1\nvar y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(DeclarationNode.class);
    }

    @Test
    void testTwoStatementsWithSemicolon() {
        Lexer lexer = new Lexer("var x := 1; var y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(DeclarationNode.class);
    }

    @Test
    void testMixedSeparators() {
        Lexer lexer = new Lexer("var x := 1; var y := 2\nvar z := 3");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(3);
    }

    @Test
    void testMultipleNewlines() {
        Lexer lexer = new Lexer("var x := 1\n\n\nvar y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
    }

    @Test
    void testMultipleSemicolons() {
        Lexer lexer = new Lexer("var x := 1;; var y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
    }

    @Test
    void testTrailingSemicolon() {
        Lexer lexer = new Lexer("var x := 1; var y := 2;");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
    }

    @Test
    void testTrailingNewline() {
        Lexer lexer = new Lexer("var x := 1\nvar y := 2\n");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
    }

    @Test
    void testLeadingNewline() {
        Lexer lexer = new Lexer("\nvar x := 1");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
    }

    @Test
    void testLeadingSemicolon() {
        Lexer lexer = new Lexer("; var x := 1");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
    }

    // ========== Separators in Control Flow ==========

    @Test
    void testSeparatorsInIfBlock() {
        String code = """
            if true then
                var x := 1
                print x
                var y := 2
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(3);
    }

    @Test
    void testSeparatorsInIfWithSemicolons() {
        String code = """
            if true then
                var x := 1; print x; var y := 2
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(3);
    }

    @Test
    void testSeparatorsInWhileLoop() {
        String code = """
            while true loop
                var x := 1
                print x
                var y := 2
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        WhileNode whileNode = (WhileNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) whileNode.getBody();
        assertThat(body.getStatements()).hasSize(3);
    }

    @Test
    void testSeparatorsInForLoop() {
        String code = """
            for i in 1..10 loop
                var x := i
                print x
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements()).hasSize(2);
    }

    @Test
    void testSeparatorsInFunction() {
        String code = """
            var f := func(x) is
                var y := x + 1
                print y
                return y
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(func.getStatementBody()).hasSize(3);
    }

    // ========== Complex Separator Scenarios ==========

    @Test
    void testComplexMixedSeparators() {
        String code = """
            var x := 1;
            var y := 2
            
            var z := 3;;
            print x; print y
            print z
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(6);
    }

    @Test
    void testSeparatorsWithAllStatementTypes() {
        String code = """
            var x := 1
            x := 2
            print x
            if x > 0 then
                return x
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(4);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(AssignmentNode.class);
        assertThat(program.getStatements().get(2)).isInstanceOf(PrintNode.class);
        assertThat(program.getStatements().get(3)).isInstanceOf(IfNode.class);
    }

    @Test
    void testNestedBlocksWithDifferentSeparators() {
        String code = """
            if true then
                var x := 1
                var y := 2
                if x > 0 then
                    print x
                    print y
                end
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        IfNode outerIf = (IfNode) program.getStatements().get(0);
        BlockNode outerThen = (BlockNode) outerIf.getThenStatement();
        assertThat(outerThen.getStatements()).hasSize(3); // var x, var y, inner if
        
        IfNode innerIf = (IfNode) outerThen.getStatements().get(2);
        BlockNode innerThen = (BlockNode) innerIf.getThenStatement();
        assertThat(innerThen.getStatements()).hasSize(2); // two prints
    }

    @Test
    void testSeparatorsInElseBlock() {
        String code = """
            if false then
                var x := 1
            else
                var y := 2
                var z := 3
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        BlockNode elseBlock = (BlockNode) ifNode.getElseStatement();
        assertThat(elseBlock.getStatements()).hasSize(2);
    }

    @Test
    void testManyStatementsWithMixedSeparators() {
        String code = """
            var a := 1; var b := 2
            var c := 3
            a := 10;;; b := 20
            c := 30
            
            print a; print b; print c
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(9);
    }

    @Test
    void testSingleLineProgram() {
        Lexer lexer = new Lexer("var x := 1; x := 2; print x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(3);
    }

    @Test
    void testMultiLineProgram() {
        String code = """
            var x := 1
            var y := 2
            var z := 3
            print x
            print y
            print z
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(6);
    }
}
