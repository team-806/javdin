package com.javdin.semantics;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;

import java.util.*;

/**
 * Semantic analyzer for the Javdin language.
 * Performs symbol table construction and semantic checks.
 */
public class SemanticAnalyzer implements AstVisitor<Void> {
    private final ErrorHandler errorHandler;
    private final SymbolTable symbolTable;
    private final Stack<String> context; // Track function/loop context
    
    // Context types
    private static final String CONTEXT_GLOBAL = "global";
    private static final String CONTEXT_FUNCTION = "function";
    private static final String CONTEXT_LOOP = "loop";
    
    public SemanticAnalyzer(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.symbolTable = new SymbolTable();
        this.context = new Stack<>();
        this.context.push(CONTEXT_GLOBAL);
    }
    
    public void analyze(ProgramNode program) {
        program.accept(this);
    }
    
    @Override
    public Void visitProgram(ProgramNode node) {
        symbolTable.enterScope();
        
        for (StatementNode statement : node.getStatements()) {
            statement.accept(this);
        }
        
        symbolTable.exitScope();
        return null;
    }
    
    @Override
    public Void visitDeclaration(DeclarationNode node) {
        // Check if variable is already declared in current scope
        if (symbolTable.isDeclaredInCurrentScope(node.getVariableName())) {
            errorHandler.addError("Variable '" + node.getVariableName() + "' is already declared", 
                                node.getLine(), node.getColumn());
            return null;
        }
        
        // Add variable to symbol table
        symbolTable.declare(node.getVariableName(), "var");
        
        // Analyze initial value if present
        if (node.getInitialValue() != null) {
            node.getInitialValue().accept(this);
        }
        
        return null;
    }
    
    @Override
    public Void visitAssignment(AssignmentNode node) {
        // Check if the reference is declared
        checkReference(node.getReference());
        node.getValue().accept(this);
        return null;
    }
    
    @Override
    public Void visitIf(IfNode node) {
        node.getCondition().accept(this);
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitWhile(WhileNode node) {
        node.getCondition().accept(this);
        
        // Enter loop context
        context.push(CONTEXT_LOOP);
        node.getBody().accept(this);
        context.pop();
        
        return null;
    }
    
    @Override
    public Void visitFor(ForNode node) {
        // Enter loop context
        context.push(CONTEXT_LOOP);
        
        // Handle loop variable scope
        symbolTable.enterScope();
        if (node.getVariable() != null) {
            symbolTable.declare(node.getVariable(), "loop_var");
        }
        
        if (node.getStart() != null) {
            node.getStart().accept(this);
        }
        if (node.getEnd() != null) {
            node.getEnd().accept(this);
        }
        
        node.getBody().accept(this);
        
        symbolTable.exitScope();
        context.pop();
        return null;
    }
    
    @Override
    public Void visitReturn(ReturnNode node) {
        // Check 1: Return statement outside function
        if (!isInFunctionContext()) {
            errorHandler.addError("Return statement outside function", 
                                node.getLine(), node.getColumn());
        }
        
        if (node.getValue() != null) {
            node.getValue().accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitBreak(BreakNode node) {
        // Check 2: Break statement outside loop
        if (!isInLoopContext()) {
            errorHandler.addError("Break statement outside loop", 
                                node.getLine(), node.getColumn());
        }
        return null;
    }
    
    @Override
    public Void visitContinue(ContinueNode node) {
        // Check 2: Continue statement outside loop
        if (!isInLoopContext()) {
            errorHandler.addError("Continue statement outside loop", 
                                node.getLine(), node.getColumn());
        }
        return null;
    }
    
    @Override
    public Void visitPrint(PrintNode node) {
        for (ExpressionNode expr : node.getExpressions()) {
            expr.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitBlock(BlockNode node) {
        symbolTable.enterScope();
        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
        symbolTable.exitScope();
        return null;
    }
    
    @Override
    public Void visitExpressionStatement(ExpressionStatementNode node) {
        node.getExpression().accept(this);
        return null;
    }
    
    @Override
    public Void visitReference(ReferenceNode node) {
        // Check 3: Variable use before declaration
        if (!symbolTable.isDeclared(node.getName())) {
            errorHandler.addError("Variable '" + node.getName() + "' is not declared", 
                                node.getLine(), node.getColumn());
        }
        return null;
    }
    
    @Override
    public Void visitBinaryOp(BinaryOpNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        return null;
    }
    
    @Override
    public Void visitUnaryOp(UnaryOpNode node) {
        node.getOperand().accept(this);
        return null;
    }
    
    @Override
    public Void visitFunctionCall(FunctionCallNode node) {
        node.getFunction().accept(this);
        for (ExpressionNode arg : node.getArguments()) {
            arg.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitArrayAccess(ArrayAccessNode node) {
        node.getArray().accept(this);
        node.getIndex().accept(this);
        return null;
    }
    
    @Override
    public Void visitFunctionLiteral(FunctionLiteralNode node) {
        // Enter function context
        context.push(CONTEXT_FUNCTION);
        symbolTable.enterScope();
        
        // Declare parameters
        for (String param : node.getParameters()) {
            symbolTable.declare(param, "parameter");
        }
        
        node.getBody().accept(this);
        
        symbolTable.exitScope();
        context.pop();
        return null;
    }
    
    @Override
    public Void visitArrayLiteral(ArrayLiteralNode node) {
        for (ExpressionNode element : node.getElements()) {
            element.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitTupleLiteral(TupleLiteralNode node) {
        for (TupleLiteralNode.TupleElement element : node.getElements()) {
            if (element.expression() != null) {
                element.expression().accept(this);
            }
        }
        return null;
    }
    
    @Override
    public Void visitTypeCheck(TypeCheckNode node) {
        node.getExpression().accept(this);
        return null;
    }
    
    @Override
    public Void visitTupleMemberAccess(TupleMemberAccessNode node) {
        node.getTuple().accept(this);
        return null;
    }
    
    @Override
    public Void visitLiteral(LiteralNode node) {
        // Literals are always valid
        return null;
    }
    
    // Helper methods for context checking
    private boolean isInFunctionContext() {
        return context.contains(CONTEXT_FUNCTION);
    }
    
    private boolean isInLoopContext() {
        return context.contains(CONTEXT_LOOP);
    }
    
    private void checkReference(ReferenceNode reference) {
        if (!symbolTable.isDeclared(reference.getName())) {
            errorHandler.addError("Variable '" + reference.getName() + "' is not declared", 
                                reference.getLine(), reference.getColumn());
        }
    }
}