package com.javdin.ast;

/**
 * AST node for tuple member access.
 * Per Project D spec: Reference.IDENT or Reference.INTEGER
 * Examples: tuple.name, tuple.1
 */
public class TupleMemberAccessNode extends ExpressionNode {
    private final ExpressionNode tuple;
    private final String memberName;  // Can be identifier or numeric index as string
    private final boolean isNumericIndex;

    /**
     * Constructor for named member access (tuple.name)
     */
    public TupleMemberAccessNode(int line, int column, ExpressionNode tuple, String memberName) { 
        super(line, column);
        this.tuple = tuple;
        this.memberName = memberName;
        this.isNumericIndex = false;
    }
    
    /**
     * Constructor for numeric index access (tuple.1)
     */
    public TupleMemberAccessNode(int line, int column, ExpressionNode tuple, int index) { 
        super(line, column);
        this.tuple = tuple;
        this.memberName = String.valueOf(index);
        this.isNumericIndex = true;
    }

    public ExpressionNode getTuple() { 
        return tuple; 
    }
    
    public String getMemberName() { 
        return memberName; 
    }
    
    public boolean isNumericIndex() {
        return isNumericIndex;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) { 
        return visitor.visitTupleMemberAccess(this); 
    }
}
