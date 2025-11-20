package com.javdin.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mutable runtime representation of Project D arrays.
 * Implements 1-based indexing semantics and supports sparse-style updates
 * by automatically growing the backing storage with void placeholders.
 */
public final class ArrayValue {
    private final List<Value> elements;
    
    public ArrayValue() {
        this.elements = new ArrayList<>();
    }
    
    public ArrayValue(List<Value> initialElements) {
        this.elements = initialElements != null
            ? new ArrayList<>(initialElements)
            : new ArrayList<>();
    }
    
    public int size() {
        return elements.size();
    }
    
    public Value get(int index) {
        verifyIndex(index);
        if (index > elements.size()) {
            return Value.VOID;
        }
        Value value = elements.get(index - 1);
        return value != null ? value : Value.VOID;
    }
    
    public void set(int index, Value value) {
        verifyIndex(index);
        ensureCapacity(index);
        elements.set(index - 1, value);
    }
    
    public List<Value> snapshot() {
        return Collections.unmodifiableList(elements);
    }
    
    public ArrayValue concat(ArrayValue other) {
        List<Value> combined = new ArrayList<>(this.elements.size() + other.elements.size());
        combined.addAll(this.elements);
        combined.addAll(other.elements);
        return new ArrayValue(combined);
    }
    
    private void ensureCapacity(int index) {
        while (elements.size() < index) {
            elements.add(Value.VOID);
        }
    }
    
    private void verifyIndex(int index) {
        if (index <= 0) {
            throw new RuntimeError("Array index out of bounds: " + index + " (indices must be >= 1)");
        }
    }
    
    @Override
    public String toString() {
        return elements.stream()
            .map(value -> value != null ? value.asString() : Value.VOID.asString())
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
