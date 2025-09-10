package com.javdin.ast;

import java.util.List;

public class ForNode extends StatementNode {
    private final StatementNode init;
    private final ExpressionNode condition;
    private final ExpressionNode update;
    private final StatementNode body;

    public ForNode(int line, int column, StatementNode init, ExpressionNode condition, ExpressionNode update, StatementNode body) { 
        super(line, column);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public StatementNode getInit() { return init; }
    public ExpressionNode getCondition() { return condition; }
    public ExpressionNode getUpdate() { return update; }
    public StatementNode getBody() { return body; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitFor(this); 
    }
}
