package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for function literal parsing.
 */
class FunctionLiteralTest {
    
    @Test
    void testFunctionWithStatementBody() {
        Lexer lexer = new Lexer("var f := func(x, y) is print x end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        assertThat(decl.getVariables().get(0).getInitialValue()).isInstanceOf(FunctionLiteralNode.class);
        
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(func.getParameters()).containsExactly("x", "y");
        assertThat(func.isExpressionBody()).isFalse();
        assertThat(func.getStatementBody()).hasSize(1);
        assertThat(func.getStatementBody().get(0)).isInstanceOf(PrintNode.class);
    }
    
    @Test
    void testFunctionWithExpressionBody() {
        Lexer lexer = new Lexer("var square := func(x) => x * x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        assertThat(decl.getVariables().get(0).getInitialValue()).isInstanceOf(FunctionLiteralNode.class);
        
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(func.getParameters()).containsExactly("x");
        assertThat(func.isExpressionBody()).isTrue();
        assertThat(func.getExpressionBody()).isInstanceOf(BinaryOpNode.class);
        
        BinaryOpNode expr = (BinaryOpNode) func.getExpressionBody();
        assertThat(expr.getOperator()).isEqualTo("*");
    }
    
    @Test
    void testFunctionWithNoParameters() {
        Lexer lexer = new Lexer("var answer := func => 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(func.getParameters()).isEmpty();
        assertThat(func.isExpressionBody()).isTrue();
        assertThat(func.getExpressionBody()).isInstanceOf(LiteralNode.class);
        
        LiteralNode lit = (LiteralNode) func.getExpressionBody();
        assertThat(lit.getValue()).isEqualTo(42);
    }
    
    @Test
    void testFunctionWithEmptyParameterList() {
        Lexer lexer = new Lexer("var f := func() => 100");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(func.getParameters()).isEmpty();
        assertThat(func.isExpressionBody()).isTrue();
    }
    
    @Test
    void testFunctionWithMultipleStatements() {
        Lexer lexer = new Lexer("var f := func(a, b) is var sum := a\nprint sum end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(func.getParameters()).containsExactly("a", "b");
        assertThat(func.isExpressionBody()).isFalse();
        assertThat(func.getStatementBody()).hasSize(2);
        assertThat(func.getStatementBody().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(func.getStatementBody().get(1)).isInstanceOf(PrintNode.class);
    }
    
    @Test
    void testNestedFunctions() {
        Lexer lexer = new Lexer("var outer := func(x) => func(y) => x + y");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode outer = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(outer.getParameters()).containsExactly("x");
        assertThat(outer.isExpressionBody()).isTrue();
        assertThat(outer.getExpressionBody()).isInstanceOf(FunctionLiteralNode.class);
        
        FunctionLiteralNode inner = (FunctionLiteralNode) outer.getExpressionBody();
        assertThat(inner.getParameters()).containsExactly("y");
        assertThat(inner.isExpressionBody()).isTrue();
        assertThat(inner.getExpressionBody()).isInstanceOf(BinaryOpNode.class);
    }
    
    @Test
    void testFunctionAsArrayElement() {
        Lexer lexer = new Lexer("var funcs := [func(x) => x + 1, func(x) => x * 2]");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        assertThat(decl.getVariables().get(0).getInitialValue()).isInstanceOf(ArrayLiteralNode.class);
        
        ArrayLiteralNode array = (ArrayLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(array.getElements()).hasSize(2);
        assertThat(array.getElements().get(0)).isInstanceOf(FunctionLiteralNode.class);
        assertThat(array.getElements().get(1)).isInstanceOf(FunctionLiteralNode.class);
    }
    
    @Test
    void testFunctionWithComplexExpression() {
        Lexer lexer = new Lexer("var f := func(x, y, z) => x * y + z");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(func.getParameters()).containsExactly("x", "y", "z");
        assertThat(func.isExpressionBody()).isTrue();
        
        // x * y + z should parse as (x * y) + z due to precedence
        BinaryOpNode addNode = (BinaryOpNode) func.getExpressionBody();
        assertThat(addNode.getOperator()).isEqualTo("+");
        assertThat(addNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        
        BinaryOpNode mulNode = (BinaryOpNode) addNode.getLeft();
        assertThat(mulNode.getOperator()).isEqualTo("*");
    }
    
    @Test
    void testFunctionInPrint() {
        Lexer lexer = new Lexer("print func(x) => x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        PrintNode print = (PrintNode) program.getStatements().get(0);
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(FunctionLiteralNode.class);
    }
    
    @Test
    void testFunctionWithSingleParameter() {
        Lexer lexer = new Lexer("var id := func(value) is print value end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        
        assertThat(func.getParameters()).containsExactly("value");
        assertThat(func.isExpressionBody()).isFalse();
    }
}
