package com.javdin.ast;

import java.util.List;

public class BlockNode extends StatementNode {
    private final List<StatementNode> statements;

    public BlockNode(int line, int column, List<StatementNode> statements) { 
        super(line, column);
        this.statements = statements;
    }

    public List<StatementNode> getStatements() { return statements; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitBlock(this); 
    }
}
