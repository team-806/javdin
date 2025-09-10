package com.javdin.ast;

/**
 * Base class for all expression nodes.
 */
public abstract class ExpressionNode implements AstNode {
    protected final int line;
    protected final int column;
    
    protected ExpressionNode(int line, int column) {
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
