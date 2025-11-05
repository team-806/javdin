package com.javdin.semantics;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SemanticAnalyzer
 */
public class SemanticAnalyzerTest {
    
    private ErrorHandler errorHandler;
    private SemanticAnalyzer analyzer;
    
    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
        analyzer = new SemanticAnalyzer(errorHandler);
    }
    
    @Test
    public void testVariableDeclarationAndUse() {
        // var x := 5
        // print x
        List<StatementNode> statements = new ArrayList<>();
        
        DeclarationNode.VariableDefinition varDef = new DeclarationNode.VariableDefinition(
            "x", 
            new LiteralNode(5, LiteralNode.LiteralType.INTEGER, 1, 5)
        );
        statements.add(new DeclarationNode(List.of(varDef), 1, 1));
        
        List<ExpressionNode> printExprs = new ArrayList<>();
        printExprs.add(new ReferenceNode(2, 7, "x"));
        statements.add(new PrintNode(2, 1, printExprs));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertFalse(errorHandler.hasErrors(), "Should not have errors for valid code");
    }
    
    @Test
    public void testUndeclaredVariable() {
        // print x  (x is not declared)
        List<StatementNode> statements = new ArrayList<>();
        
        List<ExpressionNode> printExprs = new ArrayList<>();
        printExprs.add(new ReferenceNode(1, 7, "x"));
        statements.add(new PrintNode(1, 1, printExprs));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertTrue(errorHandler.hasErrors(), "Should have error for undeclared variable");
        assertEquals(1, errorHandler.getErrors().size());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("not declared"));
    }
    
    @Test
    public void testReturnOutsideFunction() {
        // return 5  (at global scope)
        List<StatementNode> statements = new ArrayList<>();
        
        statements.add(new ReturnNode(1, 1, new LiteralNode(5, LiteralNode.LiteralType.INTEGER, 1, 8)));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertTrue(errorHandler.hasErrors(), "Should have error for return outside function");
        assertEquals(1, errorHandler.getErrors().size());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("outside function"));
    }
    
    @Test
    public void testBreakOutsideLoop() {
        // exit  (at global scope)
        List<StatementNode> statements = new ArrayList<>();
        
        statements.add(new BreakNode(1, 1));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertTrue(errorHandler.hasErrors(), "Should have error for break outside loop");
        assertEquals(1, errorHandler.getErrors().size());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("outside loop"));
    }
    
    @Test
    public void testValidReturnInFunction() {
        // func() is return 5 end
        List<StatementNode> statements = new ArrayList<>();
        
        List<StatementNode> funcBody = new ArrayList<>();
        funcBody.add(new ReturnNode(1, 15, new LiteralNode(5, LiteralNode.LiteralType.INTEGER, 1, 22)));
        
        FunctionLiteralNode funcLiteral = new FunctionLiteralNode(
            1, 1, 
            new ArrayList<>(), 
            funcBody, 
            false
        );
        
        statements.add(new ExpressionStatementNode(1, 1, funcLiteral));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertFalse(errorHandler.hasErrors(), "Should not have errors for return inside function");
    }
    
    @Test
    public void testValidBreakInLoop() {
        // while true loop exit end
        List<StatementNode> statements = new ArrayList<>();
        
        List<StatementNode> loopBody = new ArrayList<>();
        loopBody.add(new BreakNode(1, 17));
        
        WhileNode whileNode = new WhileNode(
            1, 1,
            new LiteralNode(true, LiteralNode.LiteralType.BOOLEAN, 1, 7),
            new BlockNode(1, 12, loopBody)
        );
        
        statements.add(whileNode);
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        analyzer.analyze(program);
        
        assertFalse(errorHandler.hasErrors(), "Should not have errors for break inside loop");
    }
}
