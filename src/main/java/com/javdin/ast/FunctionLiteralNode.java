package com.javdin.ast;

import java.util.List;

public class FunctionLiteralNode extends ExpressionNode {
    private final List<String> parameters;
    private final StatementNode body;

    public FunctionLiteralNode(int line, int column, List<String> parameters, StatementNode body) { 
        super(line, column);
        this.parameters = parameters;
        this.body = body;
    }

    public List<String> getParameters() { return parameters; }
    public StatementNode getBody() { return body; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitFunctionLiteral(this); 
    }
}
