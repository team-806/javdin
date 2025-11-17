package com.javdin.interpreter;

import com.javdin.ast.ExpressionNode;
import com.javdin.ast.FunctionLiteralNode;
import com.javdin.ast.StatementNode;

import java.util.List;

/**
 * Runtime representation of a Project D function literal, including
 * the parameter list, body form, and the lexical scope captured when
 * the literal was created (closure).
 */
public final class FunctionValue {
    private final List<String> parameters;
    private final boolean expressionBody;
    private final List<StatementNode> statementBody;
    private final ExpressionNode expressionBodyNode;
    private final Environment.RuntimeScope closureScope;
    private final int line;
    private final int column;
    
    public FunctionValue(FunctionLiteralNode node, Environment.RuntimeScope closureScope) {
        this.parameters = List.copyOf(node.getParameters());
        this.expressionBody = node.isExpressionBody();
        if (expressionBody) {
            this.expressionBodyNode = node.getExpressionBody();
            this.statementBody = null;
        } else {
            this.statementBody = List.copyOf(node.getStatementBody());
            this.expressionBodyNode = null;
        }
        this.closureScope = closureScope;
        this.line = node.getLine();
        this.column = node.getColumn();
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public boolean isExpressionBody() {
        return expressionBody;
    }
    
    public List<StatementNode> getStatementBody() {
        return statementBody;
    }
    
    public ExpressionNode getExpressionBody() {
        return expressionBodyNode;
    }
    
    public Environment.RuntimeScope getClosureScope() {
        return closureScope;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return "func(" + String.join(", ", parameters) + ")";
    }
}
