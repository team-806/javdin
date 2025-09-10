package com.javdin.ast;

/**
 * AST node for variable declarations.
 */
public class DeclarationNode extends StatementNode {
    private final String variableName;
    private final ExpressionNode initialValue;
    
    public DeclarationNode(String variableName, ExpressionNode initialValue, int line, int column) {
        super(line, column);
        this.variableName = variableName;
        this.initialValue = initialValue;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public ExpressionNode getInitialValue() {
        return initialValue;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitDeclaration(this);
    }
}
