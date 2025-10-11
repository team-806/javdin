package com.javdin.ast;

/**
 * AST node for type checking expressions using the 'is' operator.
 * Per Project D spec: Reference is TypeIndicator
 * Example: x is int, arr is [], func is func
 */
public class TypeCheckNode extends ExpressionNode {
    private final ExpressionNode expression;
    private final String typeIndicator;

    public TypeCheckNode(int line, int column, ExpressionNode expression, String typeIndicator) { 
        super(line, column);
        this.expression = expression;
        this.typeIndicator = typeIndicator;
    }

    public ExpressionNode getExpression() { 
        return expression; 
    }
    
    public String getTypeIndicator() { 
        return typeIndicator; 
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitTypeCheck(this); 
    }
}
