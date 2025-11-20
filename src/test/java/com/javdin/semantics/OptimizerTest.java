package com.javdin.semantics;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Optimizer
 */
public class OptimizerTest {
    
    private ErrorHandler errorHandler;
    private Optimizer optimizer;
    
    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
        optimizer = new Optimizer(errorHandler);
    }
    
    @Test
    public void testConstantFolding() {
        // 2 + 3 should become 5
        BinaryOpNode expr = new BinaryOpNode(
            1, 1,
            new LiteralNode(2, LiteralNode.LiteralType.INTEGER, 1, 1),
            "+",
            new LiteralNode(3, LiteralNode.LiteralType.INTEGER, 1, 5)
        );
        
        AstNode optimized = expr.accept(optimizer);
        
        assertTrue(optimized instanceof LiteralNode, "Should fold to literal");
        LiteralNode literal = (LiteralNode) optimized;
        assertEquals(5, literal.getValue());
        assertEquals(LiteralNode.LiteralType.INTEGER, literal.getType());
    }
    
    @Test
    public void testConstantFoldingMultiplication() {
        // 4 * 5 should become 20
        BinaryOpNode expr = new BinaryOpNode(
            1, 1,
            new LiteralNode(4, LiteralNode.LiteralType.INTEGER, 1, 1),
            "*",
            new LiteralNode(5, LiteralNode.LiteralType.INTEGER, 1, 5)
        );
        
        AstNode optimized = expr.accept(optimizer);
        
        assertTrue(optimized instanceof LiteralNode, "Should fold to literal");
        LiteralNode literal = (LiteralNode) optimized;
        assertEquals(20, literal.getValue());
    }
    
    @Test
    public void testBooleanConstantFolding() {
        // true and false should become false
        BinaryOpNode expr = new BinaryOpNode(
            1, 1,
            new LiteralNode(true, LiteralNode.LiteralType.BOOLEAN, 1, 1),
            "and",
            new LiteralNode(false, LiteralNode.LiteralType.BOOLEAN, 1, 10)
        );
        
        AstNode optimized = expr.accept(optimizer);
        
        assertTrue(optimized instanceof LiteralNode, "Should fold to literal");
        LiteralNode literal = (LiteralNode) optimized;
        assertEquals(false, literal.getValue());
        assertEquals(LiteralNode.LiteralType.BOOLEAN, literal.getType());
    }
    
    @Test
    public void testDeadBranchElimination() {
        // if true then x := 1 else x := 2 end  -> should keep only then branch
        List<StatementNode> statements = new ArrayList<>();
        
        IfNode ifNode = new IfNode(
            1, 1,
            new LiteralNode(true, LiteralNode.LiteralType.BOOLEAN, 1, 4),
            new AssignmentNode(1, 10, 
                new ReferenceNode(1, 10, "x"),
                new LiteralNode(1, LiteralNode.LiteralType.INTEGER, 1, 15)),
            new AssignmentNode(1, 22, 
                new ReferenceNode(1, 22, "x"),
                new LiteralNode(2, LiteralNode.LiteralType.INTEGER, 1, 27))
        );
        
        statements.add(ifNode);
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        ProgramNode optimized = optimizer.optimize(program);
        
        // The if statement should be replaced with just the then branch
        assertEquals(1, optimized.getStatements().size());
        assertTrue(optimized.getStatements().get(0) instanceof AssignmentNode);
        AssignmentNode assignment = (AssignmentNode) optimized.getStatements().get(0);
        assertTrue(assignment.getTarget() instanceof ReferenceNode);
        assertEquals("x", ((ReferenceNode) assignment.getTarget()).getName());
    }
    
    @Test
    public void testUnreachableCodeDetection() {
        // return 1; print x;  -> should detect unreachable print
        List<StatementNode> statements = new ArrayList<>();
        
        statements.add(new ReturnNode(1, 1, new LiteralNode(1, LiteralNode.LiteralType.INTEGER, 1, 8)));
        
        List<ExpressionNode> printExprs = new ArrayList<>();
        printExprs.add(new ReferenceNode(2, 7, "x"));
        statements.add(new PrintNode(2, 1, printExprs));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        ProgramNode optimized = optimizer.optimize(program);
        
        assertTrue(errorHandler.hasInfo(), "Should detect unreachable code");
        assertTrue(errorHandler.getInfoMessages().get(0).getMessage().contains("Unreachable"));
        
        // The print statement should be removed
        assertEquals(1, optimized.getStatements().size());
        assertTrue(optimized.getStatements().get(0) instanceof ReturnNode);
    }
    
    @Test
    public void testUnusedVariableDetection() {
        // var x := 5  (x is never used)
        List<StatementNode> statements = new ArrayList<>();
        
        DeclarationNode.VariableDefinition varDef = new DeclarationNode.VariableDefinition(
            "x", 
            new LiteralNode(5, LiteralNode.LiteralType.INTEGER, 1, 10)
        );
        statements.add(new DeclarationNode(List.of(varDef), 1, 1));
        
        ProgramNode program = new ProgramNode(statements, 1, 1);
        
        ProgramNode optimized = optimizer.optimize(program);
        
        assertTrue(errorHandler.hasErrors(), "Should detect unused variable");
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("Unused"));
        
        // The declaration should be removed
        assertEquals(0, optimized.getStatements().size());
    }
}
