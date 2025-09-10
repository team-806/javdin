package com.javdin.ast;

public class ReturnNode extends StatementNode {
    private final ExpressionNode value;

    public ReturnNode(int line, int column, ExpressionNode value) { 
        super(line, column);
        this.value = value;
    }

    public ExpressionNode getValue() { return value; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitReturn(this); 
    }
}
