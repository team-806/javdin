package com.javdin.ast;

public class BreakNode extends StatementNode {
    public BreakNode(int line, int column) { 
        super(line, column);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitBreak(this); 
    }
}
