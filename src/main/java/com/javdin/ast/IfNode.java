package com.javdin.ast;

public class IfNode extends StatementNode {
    private final ExpressionNode condition;
    private final StatementNode thenStatement;
    private final StatementNode elseStatement;

    public IfNode(int line, int column, ExpressionNode condition, StatementNode thenStatement, StatementNode elseStatement) { 
        super(line, column);
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public ExpressionNode getCondition() { return condition; }
    public StatementNode getThenStatement() { return thenStatement; }
    public StatementNode getElseStatement() { return elseStatement; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitIf(this); 
    }
}
