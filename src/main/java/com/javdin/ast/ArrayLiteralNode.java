package com.javdin.ast;

import java.util.List;

/**
 * AST node for array literal expressions.
 * Represents arrays like: [1, 2, 3] or ["a", "b", "c"]
 */
public class ArrayLiteralNode extends ExpressionNode {
    private final List<ExpressionNode> elements;
    
    public ArrayLiteralNode(List<ExpressionNode> elements, int line, int column) {
        super(line, column);
        this.elements = elements != null ? elements : List.of();
    }
    
    public List<ExpressionNode> getElements() {
        return elements;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitArrayLiteral(this);
    }
}
