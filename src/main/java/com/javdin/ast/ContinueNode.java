package com.javdin.ast;

public class ContinueNode extends StatementNode {
    public ContinueNode(int line, int column) { 
        super(line, column);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitContinue(this); 
    }
}
