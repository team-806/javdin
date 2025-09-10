package com.javdin.ast;

public class BinaryOpNode extends ExpressionNode {
    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;

    public BinaryOpNode(int line, int column, ExpressionNode left, String operator, ExpressionNode right) { 
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExpressionNode getRight() { return right; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitBinaryOp(this); 
    }
}
