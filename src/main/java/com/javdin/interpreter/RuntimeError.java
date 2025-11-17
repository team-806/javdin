package com.javdin.interpreter;

/**
 * Unchecked exception used to signal runtime errors detected during
 * interpretation. Carries optional line and column metadata so the
 * error handler can provide precise diagnostics.
 */
public class RuntimeError extends RuntimeException {
    private final int line;
    private final int column;
    
    public RuntimeError(String message) {
        this(message, 0, 0);
    }
    
    public RuntimeError(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
}
