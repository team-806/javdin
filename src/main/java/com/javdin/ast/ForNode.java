package com.javdin.ast;

import java.util.List;

/**
 * AST node representing a for loop in Project D.
 * Supports multiple forms:
 * - for var in iterable loop ... end
 * - for var in start..end loop ... end
 * - for start..end loop ... end (anonymous iteration)
 * - for iterable loop ... end (anonymous iteration)
 * - loop ... end (infinite loop, no header)
 */
public class ForNode extends StatementNode {
    private final String variable;      // Loop variable name (null for anonymous or infinite loop)
    private final ExpressionNode iterable; // Expression to iterate over (array/tuple) or range start
    private final ExpressionNode rangeEnd; // End of range (null if not a range loop)
    private final StatementNode body;

    // Constructor for for-in loops: for var in iterable
    public ForNode(int line, int column, String variable, ExpressionNode iterable, StatementNode body) {
        super(line, column);
        this.variable = variable;
        this.iterable = iterable;
        this.rangeEnd = null;
        this.body = body;
    }

    // Constructor for range loops: for var in start..end
    public ForNode(int line, int column, String variable, ExpressionNode rangeStart, ExpressionNode rangeEnd, StatementNode body) {
        super(line, column);
        this.variable = variable;
        this.iterable = rangeStart;
        this.rangeEnd = rangeEnd;
        this.body = body;
    }

    // Constructor for infinite loops: loop ... end
    public ForNode(int line, int column, StatementNode body) {
        super(line, column);
        this.variable = null;
        this.iterable = null;
        this.rangeEnd = null;
        this.body = body;
    }

    public String getVariable() { return variable; }
    public ExpressionNode getIterable() { return iterable; }
    public ExpressionNode getRangeEnd() { return rangeEnd; }
    public StatementNode getBody() { return body; }
    
    public boolean isInfiniteLoop() { return variable == null && iterable == null; }
    public boolean isRangeLoop() { return rangeEnd != null; }
    public boolean isIterableLoop() { return iterable != null && rangeEnd == null; }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitFor(this); 
    }
}
