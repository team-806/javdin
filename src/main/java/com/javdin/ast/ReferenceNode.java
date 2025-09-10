package com.javdin.ast;

public class ReferenceNode extends ExpressionNode {
    private final String name;

    public ReferenceNode(int line, int column, String name) { 
        super(line, column);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitReference(this); 
    }
}
