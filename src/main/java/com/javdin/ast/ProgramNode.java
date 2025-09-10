package com.javdin.ast;

import java.util.List;

/**
 * Root node of the AST representing a complete program.
 */
public class ProgramNode implements AstNode {
    private final List<StatementNode> statements;
    private final int line;
    private final int column;
    
    public ProgramNode(List<StatementNode> statements, int line, int column) {
        this.statements = statements;
        this.line = line;
        this.column = column;
    }
    
    public List<StatementNode> getStatements() {
        return statements;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitProgram(this);
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
