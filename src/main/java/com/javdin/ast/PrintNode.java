package com.javdin.ast;

public class PrintNode extends StatementNode {
    private final ExpressionNode expression;

    public PrintNode(int line, int column, ExpressionNode expression) { 
        super(line, column);
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitPrint(this); 
    }
}
