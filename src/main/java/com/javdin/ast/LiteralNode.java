package com.javdin.ast;

/**
 * AST node for literal values (integers, reals, booleans, strings).
 */
public class LiteralNode extends ExpressionNode {
    private final Object value;
    private final LiteralType type;
    
    public enum LiteralType {
        INTEGER, REAL, BOOLEAN, STRING
    }
    
    public LiteralNode(Object value, LiteralType type, int line, int column) {
        super(line, column);
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public LiteralType getType() {
        return type;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
