package com.javdin.semantics;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;

/**
 * Semantic analyzer for the Javdin language.
 * Performs symbol table construction and semantic checks.
 */
public class SemanticAnalyzer implements AstVisitor<Void> {
    private final ErrorHandler errorHandler;
    private final SymbolTable symbolTable;
    
    public SemanticAnalyzer(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.symbolTable = new SymbolTable();
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
    public Void visitLiteral(LiteralNode node) {
        // Literals are always valid
        return null;
    }
    
    // Stub implementations for other visitor methods
    @Override public Void visitAssignment(AssignmentNode node) { return null; }
    @Override public Void visitIf(IfNode node) { return null; }
    @Override public Void visitWhile(WhileNode node) { return null; }
    @Override public Void visitFor(ForNode node) { return null; }
    @Override public Void visitReturn(ReturnNode node) { return null; }
    @Override public Void visitBreak(BreakNode node) { return null; }
    @Override public Void visitContinue(ContinueNode node) { return null; }
    @Override public Void visitPrint(PrintNode node) { return null; }
    @Override public Void visitBlock(BlockNode node) { return null; }
    @Override public Void visitExpressionStatement(ExpressionStatementNode node) { return null; }
    @Override public Void visitReference(ReferenceNode node) { return null; }
    @Override public Void visitBinaryOp(BinaryOpNode node) { return null; }
    @Override public Void visitUnaryOp(UnaryOpNode node) { return null; }
    @Override public Void visitFunctionCall(FunctionCallNode node) { return null; }
    @Override public Void visitArrayAccess(ArrayAccessNode node) { return null; }
    @Override public Void visitFunctionLiteral(FunctionLiteralNode node) { return null; }
    @Override public Void visitArrayLiteral(ArrayLiteralNode node) { return null; }
    @Override public Void visitTupleLiteral(TupleLiteralNode node) { return null; }
    @Override public Void visitTypeCheck(TypeCheckNode node) { return null; }
    @Override public Void visitTupleMemberAccess(TupleMemberAccessNode node) { return null; }
}
