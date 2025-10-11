package com.javdin.ast;

import java.util.List;

public class FunctionLiteralNode extends ExpressionNode {
    private final List<String> parameters;
    private final Object body; // Can be List<StatementNode> or ExpressionNode
    private final boolean isExpressionBody;

    // Constructor for statement body: func(params) is ... end
    public FunctionLiteralNode(int line, int column, List<String> parameters, List<StatementNode> body, boolean isExpressionBody) { 
        super(line, column);
        this.parameters = parameters;
        this.body = body;
        this.isExpressionBody = isExpressionBody;
    }

    // Constructor for expression body: func(params) => expr
    public FunctionLiteralNode(int line, int column, List<String> parameters, ExpressionNode body, boolean isExpressionBody) { 
        super(line, column);
        this.parameters = parameters;
        this.body = body;
        this.isExpressionBody = isExpressionBody;
    }

    public List<String> getParameters() { return parameters; }
    public Object getBody() { return body; }
    public boolean isExpressionBody() { return isExpressionBody; }

    @SuppressWarnings("unchecked")
    public List<StatementNode> getStatementBody() {
        if (isExpressionBody) {
            throw new IllegalStateException("Function has expression body, not statement body");
        }
        return (List<StatementNode>) body;
    }

    public ExpressionNode getExpressionBody() {
        if (!isExpressionBody) {
            throw new IllegalStateException("Function has statement body, not expression body");
        }
        return (ExpressionNode) body;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitFunctionLiteral(this); 
    }
}
