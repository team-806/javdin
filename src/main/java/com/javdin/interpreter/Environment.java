package com.javdin.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime environment for variable storage and lexical scope management.
 */
public class Environment {
    private RuntimeScope currentScope;
    
    public Environment() {
        this.currentScope = new RuntimeScope(null);
    }
    
    public RuntimeScope enterScope() {
        currentScope = new RuntimeScope(currentScope);
        return currentScope;
    }
    
    public void exitScope() {
        if (currentScope.parent == null) {
            throw new IllegalStateException("Cannot exit global scope");
        }
        currentScope = currentScope.parent;
    }
    
    public RuntimeScope captureCurrentScope() {
        return currentScope;
    }
    
    public RuntimeScope pushFunctionScope(RuntimeScope parentScope) {
        RuntimeScope previous = currentScope;
        currentScope = new RuntimeScope(parentScope);
        return previous;
    }
    
    public void restoreScope(RuntimeScope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("Previous scope cannot be null");
        }
        currentScope = scope;
    }
    
    public void define(String name, Value value) {
        currentScope.values.put(name, value);
    }
    
    public Value lookup(String name) {
        RuntimeScope scope = resolveScope(name);
        return scope != null ? scope.values.get(name) : null;
    }
    
    public boolean assign(String name, Value value) {
        RuntimeScope scope = resolveScope(name);
        if (scope == null) {
            return false;
        }
        scope.values.put(name, value);
        return true;
    }
    
    public boolean isDefined(String name) {
        return resolveScope(name) != null;
    }
    
    public RuntimeScope getCurrentScope() {
        return currentScope;
    }
    
    private RuntimeScope resolveScope(String name) {
        RuntimeScope scope = currentScope;
        while (scope != null) {
            if (scope.values.containsKey(name)) {
                return scope;
            }
            scope = scope.parent;
        }
        return null;
    }
    
    /**
     * Single lexical scope frame with pointer to parent scope.
     */
    public static final class RuntimeScope {
        private final RuntimeScope parent;
        private final Map<String, Value> values = new HashMap<>();
        
        private RuntimeScope(RuntimeScope parent) {
            this.parent = parent;
        }
        
        public RuntimeScope getParent() {
            return parent;
        }
    }
}
