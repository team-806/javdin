package com.javdin.ast;

/**
 * Base interface for all AST nodes.
 * Uses the visitor pattern for traversal.
 */
public interface AstNode {
    /**
     * Accept a visitor for processing this node.
     */
    <T> T accept(AstVisitor<T> visitor);
    
    /**
     * Get the line number where this node appears in the source.
     */
    int getLine();
    
    /**
     * Get the column number where this node appears in the source.
     */
    int getColumn();
}
