package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Parser class.
 */
class ParserTest {
    
    @Test
    void testSimpleDeclaration() {
        Lexer lexer = new Lexer("var x := 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        
        DeclarationNode declaration = (DeclarationNode) program.getStatements().get(0);
        assertThat(declaration.getVariableName()).isEqualTo("x");
        assertThat(declaration.getInitialValue()).isInstanceOf(LiteralNode.class);
        
        LiteralNode literal = (LiteralNode) declaration.getInitialValue();
        assertThat(literal.getValue()).isEqualTo(42);
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.INTEGER);
    }
    
    @Test
    void testDeclarationWithoutInitializer() {
        Lexer lexer = new Lexer("var x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode declaration = (DeclarationNode) program.getStatements().get(0);
        assertThat(declaration.getVariableName()).isEqualTo("x");
        assertThat(declaration.getInitialValue()).isNull();
    }
    
    @Test
    void testMultipleStatements() {
        Lexer lexer = new Lexer("var x := 1; var y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(DeclarationNode.class);
    }
    
    @Test
    void testDifferentLiterals() {
        Lexer lexer = new Lexer("var a := 42; var b := 3.14; var c := true; var d := \"hello\"");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(4);
        
        // Integer literal
        DeclarationNode decl1 = (DeclarationNode) program.getStatements().get(0);
        LiteralNode lit1 = (LiteralNode) decl1.getInitialValue();
        assertThat(lit1.getType()).isEqualTo(LiteralNode.LiteralType.INTEGER);
        assertThat(lit1.getValue()).isEqualTo(42);
        
        // Real literal
        DeclarationNode decl2 = (DeclarationNode) program.getStatements().get(1);
        LiteralNode lit2 = (LiteralNode) decl2.getInitialValue();
        assertThat(lit2.getType()).isEqualTo(LiteralNode.LiteralType.REAL);
        assertThat(lit2.getValue()).isEqualTo(3.14);
        
        // Boolean literal
        DeclarationNode decl3 = (DeclarationNode) program.getStatements().get(2);
        LiteralNode lit3 = (LiteralNode) decl3.getInitialValue();
        assertThat(lit3.getType()).isEqualTo(LiteralNode.LiteralType.BOOLEAN);
        assertThat(lit3.getValue()).isEqualTo(true);
        
        // String literal
        DeclarationNode decl4 = (DeclarationNode) program.getStatements().get(3);
        LiteralNode lit4 = (LiteralNode) decl4.getInitialValue();
        assertThat(lit4.getType()).isEqualTo(LiteralNode.LiteralType.STRING);
        assertThat(lit4.getValue()).isEqualTo("hello");
    }
    
    @Test
    void testPrintStatement() {
        Lexer lexer = new Lexer("print 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(PrintNode.class);
        
        PrintNode print = (PrintNode) program.getStatements().get(0);
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(LiteralNode.class);
    }
    
    @Test
    void testParseError() {
        Lexer lexer = new Lexer("var;"); // Missing identifier
        Parser parser = new Parser(lexer);
        
        assertThatThrownBy(() -> parser.parse())
            .isInstanceOf(ParseException.class);
    }
    
    @Test
    void testEmptyProgram() {
        Lexer lexer = new Lexer("");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).isEmpty();
    }
    
    @Test
    void testMultipleVariableDeclaration() {
        Lexer lexer = new Lexer("var x := 1, y := 2, z");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode declaration = (DeclarationNode) program.getStatements().get(0);
        
        assertThat(declaration.getVariables()).hasSize(3);
        assertThat(declaration.getVariables().get(0).getName()).isEqualTo("x");
        assertThat(declaration.getVariables().get(0).getInitialValue()).isInstanceOf(LiteralNode.class);
        
        assertThat(declaration.getVariables().get(1).getName()).isEqualTo("y");
        assertThat(declaration.getVariables().get(1).getInitialValue()).isInstanceOf(LiteralNode.class);
        
        assertThat(declaration.getVariables().get(2).getName()).isEqualTo("z");
        assertThat(declaration.getVariables().get(2).getInitialValue()).isNull();
    }
    
    @Test
    void testMultipleExpressionPrint() {
        Lexer lexer = new Lexer("print 1, 2, 3");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(3);
        assertThat(print.getExpressions().get(0)).isInstanceOf(LiteralNode.class);
        assertThat(print.getExpressions().get(1)).isInstanceOf(LiteralNode.class);
        assertThat(print.getExpressions().get(2)).isInstanceOf(LiteralNode.class);
        
        LiteralNode lit1 = (LiteralNode) print.getExpressions().get(0);
        assertThat(lit1.getValue()).isEqualTo(1);
    }
    
    @Test
    void testOptionalSemicolons() {
        Lexer lexer = new Lexer("var x := 1\nvar y := 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
    }
    
    @Test
    void testMixedSeparators() {
        Lexer lexer = new Lexer("var x := 1; var y := 2\nvar z := 3");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(3);
    }
}
