package com.javdin.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * AST node for variable declarations.
 * Per Project D spec: var VariableDefinition { , VariableDefinition }
 * Example: var x := 1, y := 2, z
 */
public class DeclarationNode extends StatementNode {
    private final List<VariableDefinition> variables;
    
    /**
     * Represents a single variable definition within a declaration.
     */
    public static class VariableDefinition {
        private final String name;
        private final ExpressionNode initialValue; // null if not initialized
        
        public VariableDefinition(String name, ExpressionNode initialValue) {
            this.name = name;
            this.initialValue = initialValue;
        }
        
        public String getName() {
            return name;
        }
        
        public ExpressionNode getInitialValue() {
            return initialValue;
        }
    }
    
    /**
     * Constructor for multi-variable declarations.
     * @param variables List of variable definitions (must not be empty)
     */
    public DeclarationNode(List<VariableDefinition> variables, int line, int column) {
        super(line, column);
        if (variables == null || variables.isEmpty()) {
            throw new IllegalArgumentException("Declaration must have at least one variable");
        }
        this.variables = variables;
    }
    
    /**
     * Convenience constructor for single-variable declarations (backward compatibility).
     */
    public DeclarationNode(String variableName, ExpressionNode initialValue, int line, int column) {
        super(line, column);
        this.variables = new ArrayList<>();
        this.variables.add(new VariableDefinition(variableName, initialValue));
    }
    
    public List<VariableDefinition> getVariables() {
        return variables;
    }
    
    /**
     * Get the first (or only) variable name - for backward compatibility with existing code.
     * @deprecated Use getVariables() instead for multi-variable support
     */
    @Deprecated
    public String getVariableName() {
        return variables.get(0).getName();
    }
    
    /**
     * Get the initial value of the first (or only) variable - for backward compatibility.
     * @deprecated Use getVariables() instead for multi-variable support
     */
    @Deprecated
    public ExpressionNode getInitialValue() {
        return variables.get(0).getInitialValue();
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitDeclaration(this);
    }
}
