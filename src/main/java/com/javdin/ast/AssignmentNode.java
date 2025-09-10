package com.javdin.ast;

public class AssignmentNode extends StatementNode {
    private final String variable;
    private final ExpressionNode value;

    public AssignmentNode(int line, int column, String variable, ExpressionNode value) { 
        super(line, column);
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() { return variable; }
    public ExpressionNode getValue() { return value; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitAssignment(this); 
    }
}
