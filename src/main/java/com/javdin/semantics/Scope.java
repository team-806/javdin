package com.javdin.semantics;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single scope in the symbol table.
 */
public class Scope {
    private final Map<String, String> symbols;
    
    public Scope() {
        this.symbols = new HashMap<>();
    }
    
    public void declare(String name, String type) {
        symbols.put(name, type);
    }
    
    public boolean isDeclared(String name) {
        return symbols.containsKey(name);
    }
    
    public String getType(String name) {
        return symbols.get(name);
    }
    
    public Map<String, String> getSymbols() {
        return new HashMap<>(symbols);
    }
}
