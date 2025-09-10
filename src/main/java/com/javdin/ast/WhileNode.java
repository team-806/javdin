package com.javdin.ast;

public class WhileNode extends StatementNode {
    private final ExpressionNode condition;
    private final StatementNode body;

    public WhileNode(int line, int column, ExpressionNode condition, StatementNode body) { 
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() { return condition; }
    public StatementNode getBody() { return body; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitWhile(this); 
    }
}
