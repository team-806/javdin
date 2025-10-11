package com.javdin.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * AST node for print statements.
 * Per Project D spec: print Expression { , Expression }
 * Example: print "Hello", x, y + z
 */
public class PrintNode extends StatementNode {
    private final List<ExpressionNode> expressions;

    /**
     * Constructor for multi-expression print statements.
     * @param line Line number
     * @param column Column number
     * @param expressions List of expressions to print (must not be empty per spec)
     */
    public PrintNode(int line, int column, List<ExpressionNode> expressions) { 
        super(line, column);
        if (expressions == null || expressions.isEmpty()) {
            throw new IllegalArgumentException("Print statement must have at least one expression");
        }
        this.expressions = expressions;
    }
    
    /**
     * Convenience constructor for single-expression print (backward compatibility).
     */
    public PrintNode(int line, int column, ExpressionNode expression) {
        super(line, column);
        if (expression == null) {
            throw new IllegalArgumentException("Print statement must have at least one expression");
        }
        this.expressions = new ArrayList<>();
        this.expressions.add(expression);
    }

    public List<ExpressionNode> getExpressions() { 
        return expressions; 
    }
    
    /**
     * Get the first (or only) expression - for backward compatibility.
     * @deprecated Use getExpressions() instead for multi-expression support
     */
    @Deprecated
    public ExpressionNode getExpression() { 
        return expressions.get(0); 
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitPrint(this); 
    }
}
