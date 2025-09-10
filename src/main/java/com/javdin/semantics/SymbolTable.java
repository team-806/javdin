package com.javdin.semantics;

import java.util.*;

/**
 * Symbol table for tracking variable declarations and scopes.
 */
public class SymbolTable {
    private final Stack<Scope> scopes;
    
    public SymbolTable() {
        this.scopes = new Stack<>();
    }
    
    public void enterScope() {
        scopes.push(new Scope());
    }
    
    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }
    
    public void declare(String name, String type) {
        if (!scopes.isEmpty()) {
            scopes.peek().declare(name, type);
        }
    }
    
    public boolean isDeclaredInCurrentScope(String name) {
        return !scopes.isEmpty() && scopes.peek().isDeclared(name);
    }
    
    public boolean isDeclared(String name) {
        for (Scope scope : scopes) {
            if (scope.isDeclared(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String getType(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Scope scope = scopes.get(i);
            if (scope.isDeclared(name)) {
                return scope.getType(name);
            }
        }
        return null;
    }
}
