package com.javdin.ast;

/**
 * Visitor interface for traversing AST nodes.
 */
public interface AstVisitor<T> {
    // Program and statements
    T visitProgram(ProgramNode node);
    T visitDeclaration(DeclarationNode node);
    T visitAssignment(AssignmentNode node);
    T visitIf(IfNode node);
    T visitWhile(WhileNode node);
    T visitFor(ForNode node);
    T visitReturn(ReturnNode node);
    T visitBreak(BreakNode node);
    T visitContinue(ContinueNode node);
    T visitPrint(PrintNode node);
    T visitBlock(BlockNode node);
    T visitExpressionStatement(ExpressionStatementNode node);
    
    // Expressions
    T visitLiteral(LiteralNode node);
    T visitReference(ReferenceNode node);
    T visitBinaryOp(BinaryOpNode node);
    T visitUnaryOp(UnaryOpNode node);
    T visitFunctionCall(FunctionCallNode node);
    T visitArrayAccess(ArrayAccessNode node);
    T visitFunctionLiteral(FunctionLiteralNode node);
    T visitArrayLiteral(ArrayLiteralNode node);
    T visitTupleLiteral(TupleLiteralNode node);
}
