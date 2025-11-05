package com.javdin.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Symbol table for tracking variable declarations and scopes.
 */
public class SymbolTable {
    private final Stack<Map<String, String>> scopes;
    
    public SymbolTable() {
        this.scopes = new Stack<>();
        // Start with global scope
        enterScope();
    }
    
    public void enterScope() {
        scopes.push(new HashMap<>());
    }
    
    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        }
    }
    
    public void declare(String name, String type) {
        scopes.peek().put(name, type);
    }
    
    public boolean isDeclaredInCurrentScope(String name) {
        return scopes.peek().containsKey(name);
    }
    
    public boolean isDeclared(String name) {
        // Check all scopes from current to global
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String getScopeType(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return null;
    }
    
    public int getCurrentScopeLevel() {
        return scopes.size();
    }
}