package com.javdin.ast;

public class AssignmentNode extends StatementNode {
    private final ExpressionNode target;  // Can be ReferenceNode, ArrayAccessNode, TupleMemberAccessNode, etc.
    private final ExpressionNode value;

    // Constructor for general reference assignment
    public AssignmentNode(int line, int column, ExpressionNode target, ExpressionNode value) { 
        super(line, column);
        this.target = target;
        this.value = value;
    }

    // Legacy constructor for simple variable assignment (for backward compatibility)
    @Deprecated
    public AssignmentNode(int line, int column, String variable, ExpressionNode value) { 
        super(line, column);
        this.target = new ReferenceNode(line, column, variable);
        this.value = value;
    }

    public ExpressionNode getTarget() { return target; }
    
    // Legacy getter for backward compatibility
    @Deprecated
    public String getVariable() { 
        if (target instanceof ReferenceNode) {
            return ((ReferenceNode) target).getName();
        }
        throw new UnsupportedOperationException("Target is not a simple variable reference");
    }
    
    public ExpressionNode getValue() { return value; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitAssignment(this); 
    }
}

