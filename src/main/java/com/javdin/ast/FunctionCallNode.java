package com.javdin.ast;

import java.util.List;

public class FunctionCallNode extends ExpressionNode {
    private final ExpressionNode function;
    private final List<ExpressionNode> arguments;

    public FunctionCallNode(int line, int column, ExpressionNode function, List<ExpressionNode> arguments) { 
        super(line, column);
        this.function = function;
        this.arguments = arguments;
    }

    public ExpressionNode getFunction() { return function; }
    public List<ExpressionNode> getArguments() { return arguments; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitFunctionCall(this); 
    }
}
