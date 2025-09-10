package com.javdin.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects and manages compilation and runtime errors.
 */
public class ErrorHandler {
    private final List<Error> errors;
    
    public ErrorHandler() {
        this.errors = new ArrayList<>();
    }
    
    public void addError(String message, int line, int column) {
        errors.add(new Error(message, line, column));
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public void printErrors() {
        for (Error error : errors) {
            System.err.println(error);
        }
    }
    
    public List<Error> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public void clear() {
        errors.clear();
    }
    
    public static class Error {
        private final String message;
        private final int line;
        private final int column;
        
        public Error(String message, int line, int column) {
            this.message = message;
            this.line = line;
            this.column = column;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        @Override
        public String toString() {
            if (line > 0 && column > 0) {
                return String.format("Error at line %d, column %d: %s", line, column, message);
            } else {
                return "Error: " + message;
            }
        }
    }
}
