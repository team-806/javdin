package com.javdin.interpreter;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;

/**
 * Interpreter for the Javdin language.
 * Executes the AST and produces runtime behavior.
 */
public class Interpreter implements AstVisitor<Value> {
    private final ErrorHandler errorHandler;
    private final Environment environment;
    
    public Interpreter(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.environment = new Environment();
    }
    
    public void interpret(ProgramNode program) {
        try {
            program.accept(this);
        } catch (RuntimeException e) {
            errorHandler.addError("Runtime error: " + e.getMessage(), 0, 0);
        }
    }
    
    @Override
    public Value visitProgram(ProgramNode node) {
        Value lastValue = Value.VOID;
        
        for (StatementNode statement : node.getStatements()) {
            lastValue = statement.accept(this);
        }
        
        return lastValue;
    }
    
    @Override
    public Value visitDeclaration(DeclarationNode node) {
        Value initialValue = Value.VOID;
        
        if (node.getInitialValue() != null) {
            initialValue = node.getInitialValue().accept(this);
        }
        
        environment.define(node.getVariableName(), initialValue);
        return Value.VOID;
    }
    
    @Override
    public Value visitLiteral(LiteralNode node) {
        return switch (node.getType()) {
            case INTEGER -> new Value(node.getValue());
            case REAL -> new Value(node.getValue());
            case BOOLEAN -> new Value(node.getValue());
            case STRING -> new Value(node.getValue());
        };
    }
    
    @Override
    public Value visitPrint(PrintNode node) {
        // For now, just print a placeholder
        System.out.println("Print statement executed");
        return Value.VOID;
    }
    
    // Stub implementations for other visitor methods
    @Override public Value visitAssignment(AssignmentNode node) { return Value.VOID; }
    @Override public Value visitIf(IfNode node) { return Value.VOID; }
    @Override public Value visitWhile(WhileNode node) { return Value.VOID; }
    @Override public Value visitFor(ForNode node) { return Value.VOID; }
    @Override public Value visitReturn(ReturnNode node) { return Value.VOID; }
    @Override public Value visitBreak(BreakNode node) { return Value.VOID; }
    @Override public Value visitContinue(ContinueNode node) { return Value.VOID; }
    @Override public Value visitBlock(BlockNode node) { return Value.VOID; }
    @Override public Value visitExpressionStatement(ExpressionStatementNode node) { return Value.VOID; }
    @Override public Value visitReference(ReferenceNode node) { return Value.VOID; }
    @Override public Value visitBinaryOp(BinaryOpNode node) { return Value.VOID; }
    @Override public Value visitUnaryOp(UnaryOpNode node) { return Value.VOID; }
    @Override public Value visitFunctionCall(FunctionCallNode node) { return Value.VOID; }
    @Override public Value visitArrayAccess(ArrayAccessNode node) { return Value.VOID; }
    @Override public Value visitFunctionLiteral(FunctionLiteralNode node) { return Value.VOID; }
}
