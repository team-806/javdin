package com.javdin.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mutable runtime representation of Project D tuples.
 * Preserves declaration order and allows member lookup by name or 1-based index.
 */
public final class TupleValue {
    private final List<TupleEntry> entries = new ArrayList<>();
    private final Map<String, Integer> nameToIndex = new LinkedHashMap<>();
    
    public TupleValue() {
    }
    
    public TupleValue(List<TupleEntry> initialEntries) {
        if (initialEntries != null) {
            for (TupleEntry entry : initialEntries) {
                append(entry.name(), entry.value());
            }
        }
    }
    
    public int size() {
        return entries.size();
    }
    
    public void append(String name, Value value) {
        TupleEntry entry = new TupleEntry(name, value);
        entries.add(entry);
        if (name != null) {
            nameToIndex.put(name, entries.size() - 1);
        }
    }
    
    public Value getByIndex(int index) {
        verifyIndex(index);
        return entries.get(index - 1).value();
    }
    
    public Value getByName(String name) {
        Integer idx = nameToIndex.get(name);
        if (idx == null) {
            throw new RuntimeError("Tuple has no member named '" + name + "'");
        }
        return entries.get(idx).value();
    }
    
    public void setByIndex(int index, Value value) {
        verifyIndex(index);
        entries.get(index - 1).setValue(value);
    }
    
    public void setByName(String name, Value value) {
        Integer idx = nameToIndex.get(name);
        if (idx == null) {
            throw new RuntimeError("Tuple has no member named '" + name + "'");
        }
        entries.get(idx).setValue(value);
    }
    
    public TupleValue concat(TupleValue other) {
        TupleValue result = new TupleValue();
        this.entries.forEach(entry -> result.append(entry.name(), entry.value()));
        other.entries.forEach(entry -> result.append(entry.name(), entry.value()));
        return result;
    }
    
    public List<TupleEntry> snapshot() {
        return Collections.unmodifiableList(entries);
    }
    
    private void verifyIndex(int index) {
        if (index <= 0 || index > entries.size()) {
            throw new RuntimeError("Tuple index out of bounds: " + index);
        }
    }
    
    @Override
    public String toString() {
        return entries.stream()
            .map(entry -> entry.name() != null
                ? entry.name() + ":=" + entry.value().asString()
                : entry.value().asString())
            .collect(Collectors.joining(", ", "{", "}"));
    }
    
    /**
     * Tuple element with optional name.
     */
    public static final class TupleEntry {
        private final String name;
        private Value value;
        
        public TupleEntry(String name, Value value) {
            this.name = name;
            this.value = value;
        }
        
        public String name() {
            return name;
        }
        
        public Value value() {
            return value;
        }
        
        private void setValue(Value value) {
            this.value = value;
        }
    }
}
