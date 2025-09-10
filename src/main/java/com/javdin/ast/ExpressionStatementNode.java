package com.javdin.ast;

public class ExpressionStatementNode extends StatementNode {
    private final ExpressionNode expression;

    public ExpressionStatementNode(int line, int column, ExpressionNode expression) { 
        super(line, column);
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitExpressionStatement(this); 
    }
}
