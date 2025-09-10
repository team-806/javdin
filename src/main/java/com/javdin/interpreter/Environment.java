package com.javdin.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Runtime environment for variable storage and scope management.
 */
public class Environment {
    private final Stack<Map<String, Value>> scopes;
    
    public Environment() {
        this.scopes = new Stack<>();
        this.scopes.push(new HashMap<>()); // Global scope
    }
    
    public void enterScope() {
        scopes.push(new HashMap<>());
    }
    
    public void exitScope() {
        if (scopes.size() > 1) { // Keep global scope
            scopes.pop();
        }
    }
    
    public void define(String name, Value value) {
        scopes.peek().put(name, value);
    }
    
    public Value get(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, Value> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new RuntimeException("Undefined variable: " + name);
    }
    
    public void assign(String name, Value value) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, Value> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        throw new RuntimeException("Undefined variable: " + name);
    }
    
    public boolean isDefined(String name) {
        for (Map<String, Value> scope : scopes) {
            if (scope.containsKey(name)) {
                return true;
            }
        }
        return false;
    }
}
