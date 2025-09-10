package com.javdin.ast;

public class UnaryOpNode extends ExpressionNode {
    private final String operator;
    private final ExpressionNode operand;

    public UnaryOpNode(int line, int column, String operator, ExpressionNode operand) { 
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    public String getOperator() { return operator; }
    public ExpressionNode getOperand() { return operand; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitUnaryOp(this); 
    }
}
