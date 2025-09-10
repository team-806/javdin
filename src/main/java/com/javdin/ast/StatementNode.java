package com.javdin.ast;

/**
 * Base class for all statement nodes.
 */
public abstract class StatementNode implements AstNode {
    protected final int line;
    protected final int column;
    
    protected StatementNode(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public int getLine() {
        return line;
    }
    
    @Override
    public int getColumn() {
        return column;
    }
}
