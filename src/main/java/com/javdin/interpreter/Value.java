package com.javdin.interpreter;

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
    
    public static Value integer(int value) {
        return new Value(value);
    }
    
    public static Value real(double value) {
        return new Value(value);
    }
    
    public static Value bool(boolean value) {
        return new Value(value);
    }
    
    public static Value string(String value) {
        return new Value(value);
    }
    
    public static Value array(ArrayValue value) {
        return new Value(value);
    }
    
    public static Value tuple(TupleValue value) {
        return new Value(value);
    }
    
    public static Value function(FunctionValue value) {
        return new Value(value);
    }
    
    private ValueType determineType(Object value) {
        if (value == null) return ValueType.VOID;
        if (value instanceof Integer) return ValueType.INTEGER;
        if (value instanceof Double) return ValueType.REAL;
        if (value instanceof Boolean) return ValueType.BOOLEAN;
        if (value instanceof String) return ValueType.STRING;
        if (value instanceof ArrayValue) return ValueType.ARRAY;
        if (value instanceof TupleValue) return ValueType.TUPLE;
        if (value instanceof FunctionValue) return ValueType.FUNCTION;
        return ValueType.VOID;
    }
    
    public Object getValue() {
        return value;
    }
    
    public ValueType getType() {
        return type;
    }
    
    public boolean isTruthy() {
        return switch (type) {
            case BOOLEAN -> (Boolean) value;
            case INTEGER -> (Integer) value != 0;
            case REAL -> (Double) value != 0.0;
            case STRING -> !((String) value).isEmpty();
            case ARRAY -> ((ArrayValue) value).size() > 0;
            case TUPLE -> ((TupleValue) value).size() > 0;
            case FUNCTION -> true;
            case VOID -> false;
        };
    }
    
    public int asInteger() {
        return switch (type) {
            case INTEGER -> (Integer) value;
            case REAL -> ((Double) value).intValue();
            case BOOLEAN -> (Boolean) value ? 1 : 0;
            case STRING -> parseInteger((String) value);
            default -> throw new RuntimeError("Cannot convert " + type + " to integer");
        };
    }
    
    public double asReal() {
        return switch (type) {
            case INTEGER -> ((Integer) value).doubleValue();
            case REAL -> (Double) value;
            case BOOLEAN -> (Boolean) value ? 1.0 : 0.0;
            case STRING -> parseReal((String) value);
            default -> throw new RuntimeError("Cannot convert " + type + " to real");
        };
    }
    
    public boolean asBoolean() {
        if (type != ValueType.BOOLEAN) {
            throw new RuntimeError("Expected boolean but got " + type);
        }
        return (Boolean) value;
    }
    
    public ArrayValue asArray() {
        if (type != ValueType.ARRAY) {
            throw new RuntimeError("Expected array but got " + type);
        }
        return (ArrayValue) value;
    }
    
    public TupleValue asTuple() {
        if (type != ValueType.TUPLE) {
            throw new RuntimeError("Expected tuple but got " + type);
        }
        return (TupleValue) value;
    }
    
    public FunctionValue asFunction() {
        if (type != ValueType.FUNCTION) {
            throw new RuntimeError("Expected function but got " + type);
        }
        return (FunctionValue) value;
    }
    
    public String asString() {
        if (value == null) {
            return "none";
        }
        return value.toString();
    }
    
    public boolean isNumeric() {
        return type == ValueType.INTEGER || type == ValueType.REAL;
    }
    
    private int parseInteger(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new RuntimeError("Cannot convert string '" + raw + "' to integer");
        }
    }
    
    private double parseReal(String raw) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new RuntimeError("Cannot convert string '" + raw + "' to real");
        }
    }
    
    @Override
    public String toString() {
        return asString();
    }
}
