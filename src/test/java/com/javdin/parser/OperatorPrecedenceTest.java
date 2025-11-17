package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for operator precedence and associativity.
 * Ensures the parser correctly follows Project D specification.
 */
class OperatorPrecedenceTest {
    
    // ========== Logical Operator Tests ==========
    
    @Test
    void testLogicalOperatorsHaveEqualPrecedence_OrThenAnd() {
        // Per Project D spec: "Expression : Relation { ( or | and | xor ) Relation }"
        // This suggests equal precedence, left-associative
        // So "a or b and c" should parse as "((a or b) and c)"
        Lexer lexer = new Lexer("var x := a or b and c");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level should be: (something) AND c
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("and");
        
        // Left side of AND should be: a OR b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode leftNode = (BinaryOpNode) topNode.getLeft();
        assertThat(leftNode.getOperator()).isEqualTo("or");
        
        // Result: ((a or b) and c) 
    }
    
    @Test
    void testLogicalOperatorsHaveEqualPrecedence_AndThenXor() {
        // Test: "a and b xor c" should parse as "((a and b) xor c)"
        Lexer lexer = new Lexer("var x := a and b xor c");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (something) XOR c
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("xor");
        
        // Left side: a AND b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode leftNode = (BinaryOpNode) topNode.getLeft();
        assertThat(leftNode.getOperator()).isEqualTo("and");
        
        // Result: ((a and b) xor c) 
    }
    
    @Test
    void testLogicalOperatorsHaveEqualPrecedence_XorThenOr() {
        // Test: "a xor b or c" should parse as "((a xor b) or c)"
        Lexer lexer = new Lexer("var x := a xor b or c");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (something) OR c
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("or");
        
        // Left side: a XOR b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode leftNode = (BinaryOpNode) topNode.getLeft();
        assertThat(leftNode.getOperator()).isEqualTo("xor");
        
        // Result: ((a xor b) or c) 
    }
    
    @Test
    void testLogicalOperatorsLeftAssociative() {
        // Test: "a or b or c" should parse as "((a or b) or c)"
        Lexer lexer = new Lexer("var x := a or b or c");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (something) OR c
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("or");
        assertThat(topNode.getRight()).isInstanceOf(ReferenceNode.class);
        
        // Left side: a OR b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode leftNode = (BinaryOpNode) topNode.getLeft();
        assertThat(leftNode.getOperator()).isEqualTo("or");
        
        // Result: ((a or b) or c) 
    }
    
    // ========== Comparison vs Logical Tests ==========
    
    @Test
    void testComparisonBindsTighterThanLogical() {
        // Test: "a < b and c > d" should parse as "(a < b) and (c > d)"
        Lexer lexer = new Lexer("var x := a < b and c > d");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (a < b) AND (c > d)
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("and");
        
        // Both sides should be comparisons
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getLeft()).getOperator()).isEqualTo("<");
        
        assertThat(topNode.getRight()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getRight()).getOperator()).isEqualTo(">");
        
        // Result: (a < b) and (c > d) 
    }
    
    // ========== Arithmetic vs Comparison Tests ==========
    
    @Test
    void testArithmeticBindsTighterThanComparison() {
        // Test: "a + b < c * d" should parse as "(a + b) < (c * d)"
        Lexer lexer = new Lexer("var x := a + b < c * d");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (a + b) < (c * d)
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("<");
        
        // Left side: a + b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getLeft()).getOperator()).isEqualTo("+");
        
        // Right side: c * d
        assertThat(topNode.getRight()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getRight()).getOperator()).isEqualTo("*");
        
        // Result: (a + b) < (c * d) 
    }
    
    // ========== Multiplication vs Addition Tests ==========
    
    @Test
    void testMultiplicationBindsTighterThanAddition() {
        // Test: "a + b * c" should parse as "a + (b * c)"
        Lexer lexer = new Lexer("var x := a + b * c");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: a + (b * c)
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("+");
        
        // Right side: b * c
        assertThat(topNode.getRight()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getRight()).getOperator()).isEqualTo("*");
        
        // Result: a + (b * c) 
    }
    
    // ========== Complex Expression Tests ==========
    
    @Test
    void testComplexPrecedence() {
        // Test: "a or b and c < d + e * f"
        // Expected: (a or ((b and (c < (d + (e * f)))))
        // Reading right-to-left by precedence:
        // - e * f (multiply)
        // - d + (e * f) (addition)
        // - c < (d + (e * f)) (comparison)
        // - b and (c < ...) (logical and)
        // - a or (b and ...) (logical or)
        
        Lexer lexer = new Lexer("var x := a or b and c < d + e * f");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // With equal precedence for logical operators, this should be:
        // ((a or b) and (c < (d + (e * f))))
        
        // Top level: (a or b) AND (c < ...)
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("and");
        
        // Left: a or b
        assertThat(topNode.getLeft()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getLeft()).getOperator()).isEqualTo("or");
        
        // Right: c < (d + (e * f))
        assertThat(topNode.getRight()).isInstanceOf(BinaryOpNode.class);
        assertThat(((BinaryOpNode) topNode.getRight()).getOperator()).isEqualTo("<");
    }
    
    // ========== Unary Operator Tests ==========
    
    @Test
    void testUnaryBindsTighterThanBinary() {
        // Test: "not a and b" should parse as "(not a) and b"
        Lexer lexer = new Lexer("var x := not a and b");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: (not a) AND b
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("and");
        
        // Left side: not a
        assertThat(topNode.getLeft()).isInstanceOf(UnaryOpNode.class);
        assertThat(((UnaryOpNode) topNode.getLeft()).getOperator()).isEqualTo("not");
        
        // Result: (not a) and b 
    }
    
    @Test
    void testUnaryMinusBindsTighterThanMultiplication() {
        // Test: "a * -b" should parse as "a * (-b)"
        Lexer lexer = new Lexer("var x := a * -b");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        ExpressionNode expr = decl.getVariables().get(0).getInitialValue();
        
        // Top level: a * (-b)
        assertThat(expr).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode topNode = (BinaryOpNode) expr;
        assertThat(topNode.getOperator()).isEqualTo("*");
        
        // Right side: -b
        assertThat(topNode.getRight()).isInstanceOf(UnaryOpNode.class);
        assertThat(((UnaryOpNode) topNode.getRight()).getOperator()).isEqualTo("-");
        
        // Result: a * (-b) 
    }
}
