package com.javdin.interpreter;

import java.util.List;
import java.util.Map;

/**
 * Runtime value wrapper for dynamic typing.
 */
public class Value {
    public static final Value VOID = new Value(null);
    
    private final Object value;
    private final ValueType type;
    
    public enum ValueType {
        INTEGER, REAL, BOOLEAN, STRING, ARRAY, TUPLE, FUNCTION, VOID
    }
    
    public Value(Object value) {
        this.value = value;
        this.type = determineType(value);
    }
    
    private ValueType determineType(Object value) {
        if (value == null) return ValueType.VOID;
        if (value instanceof Integer) return ValueType.INTEGER;
        if (value instanceof Double) return ValueType.REAL;
        if (value instanceof Boolean) return ValueType.BOOLEAN;
        if (value instanceof String) return ValueType.STRING;
        if (value instanceof List) return ValueType.ARRAY;
        if (value instanceof Map) return ValueType.TUPLE;
        return ValueType.VOID;
    }
    
    public Object getValue() {
        return value;
    }
    
    public ValueType getType() {
        return type;
    }
    
    public boolean isTrue() {
        return switch (type) {
            case BOOLEAN -> (Boolean) value;
            case INTEGER -> (Integer) value != 0;
            case REAL -> (Double) value != 0.0;
            case STRING -> !((String) value).isEmpty();
            case ARRAY -> !((List<?>) value).isEmpty();
            case TUPLE -> !((Map<?, ?>) value).isEmpty();
            case VOID -> false;
            default -> false;
        };
    }
    
    public int asInteger() {
        return switch (type) {
            case INTEGER -> (Integer) value;
            case REAL -> ((Double) value).intValue();
            case BOOLEAN -> (Boolean) value ? 1 : 0;
            case STRING -> {
                try {
                    yield Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }
    
    public double asReal() {
        return switch (type) {
            case INTEGER -> ((Integer) value).doubleValue();
            case REAL -> (Double) value;
            case BOOLEAN -> (Boolean) value ? 1.0 : 0.0;
            case STRING -> {
                try {
                    yield Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    yield 0.0;
                }
            }
            default -> 0.0;
        };
    }
    
    public String asString() {
        if (value == null) return "void";
        return value.toString();
    }
    
    @Override
    public String toString() {
        return asString();
    }
}
