package com.javdin.ast;

import java.util.List;

/**
 * AST node for tuple literal expressions.
 * Represents tuples like: {a := 1, b := 2} or {1, 2, 3}
 */
public class TupleLiteralNode extends ExpressionNode {
    private final List<TupleElement> elements;
    
    public TupleLiteralNode(List<TupleElement> elements, int line, int column) {
        super(line, column);
        this.elements = elements != null ? elements : List.of();
    }
    
    public List<TupleElement> getElements() {
        return elements;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitTupleLiteral(this);
    }
    
    /**
     * Represents a single element in a tuple.
     * Can be named (name := value) or unnamed (just value).
     */
    public static class TupleElement {
        private final String name;  // null for unnamed elements
        private final ExpressionNode value;
        
        public TupleElement(String name, ExpressionNode value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public ExpressionNode getValue() {
            return value;
        }
        
        public boolean isNamed() {
            return name != null;
        }
    }
}
