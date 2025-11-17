package com.javdin.interpreter;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Interpreter for the Javdin language.
 * Executes the AST and produces runtime behavior.
 */
public class Interpreter implements AstVisitor<Value> {
    private final ErrorHandler errorHandler;
    private final Environment environment;
    
    public Interpreter(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.environment = new Environment();
    }
    
    public void interpret(ProgramNode program) {
        try {
            program.accept(this);
        } catch (RuntimeError error) {
            errorHandler.addError("Runtime error: " + error.getMessage(), error.getLine(), error.getColumn());
        } catch (ReturnSignal signal) {
            errorHandler.addError("Return statement outside function", program.getLine(), program.getColumn());
        } catch (BreakSignal | ContinueSignal signal) {
            errorHandler.addError("Loop control statement outside loop", program.getLine(), program.getColumn());
        }
    }
    
    @Override
    public Value visitProgram(ProgramNode node) {
        Value lastValue = Value.VOID;
        for (StatementNode statement : node.getStatements()) {
            lastValue = statement.accept(this);
        }
        return lastValue;
    }
    
    @Override
    public Value visitDeclaration(DeclarationNode node) {
        // Predeclare all variables to support mutual recursion and self references
        for (DeclarationNode.VariableDefinition definition : node.getVariables()) {
            environment.define(definition.getName(), Value.VOID);
        }
        for (DeclarationNode.VariableDefinition definition : node.getVariables()) {
            if (definition.getInitialValue() != null) {
                Value value = evaluate(definition.getInitialValue());
                environment.assign(definition.getName(), value);
            }
        }
        return Value.VOID;
    }
    
    @Override
    public Value visitAssignment(AssignmentNode node) {
        Value value = evaluate(node.getValue());
        assignTarget(node.getTarget(), value);
        return value;
    }
    
    @Override
    public Value visitIf(IfNode node) {
        if (evaluate(node.getCondition()).isTruthy()) {
            return node.getThenStatement().accept(this);
        } else if (node.getElseStatement() != null) {
            return node.getElseStatement().accept(this);
        }
        return Value.VOID;
    }
    
    @Override
    public Value visitWhile(WhileNode node) {
        Value last = Value.VOID;
        while (evaluate(node.getCondition()).isTruthy()) {
            try {
                last = node.getBody().accept(this);
            } catch (ContinueSignal ignore) {
                continue;
            } catch (BreakSignal ignore) {
                break;
            }
        }
        return last;
    }
    
    @Override
    public Value visitFor(ForNode node) {
        if (node.isInfiniteLoop()) {
            return executeInfiniteLoop(node.getBody());
        } else if (node.isRangeLoop()) {
            return executeRangeLoop(node);
        } else if (node.isIterableLoop()) {
            return executeIterableLoop(node);
        }
        throw runtimeError("Unsupported for-loop form", node);
    }
    
    @Override
    public Value visitReturn(ReturnNode node) {
        Value value = node.getValue() != null ? evaluate(node.getValue()) : Value.VOID;
        throw new ReturnSignal(value);
    }
    
    @Override
    public Value visitBreak(BreakNode node) {
        throw new BreakSignal();
    }
    
    @Override
    public Value visitContinue(ContinueNode node) {
        throw new ContinueSignal();
    }
    
    @Override
    public Value visitBlock(BlockNode node) {
        environment.enterScope();
        try {
            Value last = Value.VOID;
            for (StatementNode statement : node.getStatements()) {
                last = statement.accept(this);
            }
            return last;
        } finally {
            environment.exitScope();
        }
    }
    
    @Override
    public Value visitExpressionStatement(ExpressionStatementNode node) {
        return evaluate(node.getExpression());
    }
    
    @Override
    public Value visitLiteral(LiteralNode node) {
        return switch (node.getType()) {
            case INTEGER -> Value.integer(((Number) node.getValue()).intValue());
            case REAL -> Value.real(((Number) node.getValue()).doubleValue());
            case BOOLEAN -> Value.bool((Boolean) node.getValue());
            case STRING -> Value.string((String) node.getValue());
            case NONE -> Value.VOID;
        };
    }
    
    @Override
    public Value visitPrint(PrintNode node) {
        List<String> outputs = new ArrayList<>();
        for (ExpressionNode expression : node.getExpressions()) {
            outputs.add(evaluate(expression).asString());
        }
        System.out.println(String.join(" ", outputs));
        return Value.VOID;
    }
    
    @Override
    public Value visitReference(ReferenceNode node) {
        Value value = environment.lookup(node.getName());
        if (value == null) {
            throw runtimeError("Undefined variable: " + node.getName(), node);
        }
        return value;
    }
    
    @Override
    public Value visitBinaryOp(BinaryOpNode node) {
        Value left = evaluate(node.getLeft());
        Value right = evaluate(node.getRight());
        String operator = node.getOperator();
        return switch (operator) {
            case "+" -> add(left, right, node);
            case "-" -> subtract(left, right, node);
            case "*" -> multiply(left, right, node);
            case "/" -> divide(left, right, node);
            case "<", "<=", ">", ">=" -> compare(left, right, operator, node);
            case "=", "==" -> Value.bool(equals(left, right));
            case "!=", "/=" -> Value.bool(!equals(left, right));
            case "and" -> Value.bool(requireBoolean(left, node) && requireBoolean(right, node));
            case "or" -> Value.bool(requireBoolean(left, node) || requireBoolean(right, node));
            case "xor" -> Value.bool(requireBoolean(left, node) ^ requireBoolean(right, node));
            default -> throw runtimeError("Unsupported operator '" + operator + "'", node);
        };
    }
    
    @Override
    public Value visitUnaryOp(UnaryOpNode node) {
        Value operand = evaluate(node.getOperand());
        return switch (node.getOperator()) {
            case "+" -> {
                if (operand.getType() == Value.ValueType.REAL) {
                    yield Value.real(operand.asReal());
                }
                if (operand.getType() == Value.ValueType.INTEGER) {
                    yield Value.integer(operand.asInteger());
                }
                throw runtimeError("Unary '+' expects numeric operand", node);
            }
            case "-" -> {
                if (operand.getType() == Value.ValueType.REAL) {
                    yield Value.real(-operand.asReal());
                }
                if (operand.getType() == Value.ValueType.INTEGER) {
                    yield Value.integer(-operand.asInteger());
                }
                throw runtimeError("Unary '-' expects numeric operand", node);
            }
            case "not" -> Value.bool(!requireBoolean(operand, node));
            default -> throw runtimeError("Unsupported unary operator '" + node.getOperator() + "'", node);
        };
    }
    
    @Override
    public Value visitFunctionCall(FunctionCallNode node) {
        Value functionValue = evaluate(node.getFunction());
        if (functionValue.getType() != Value.ValueType.FUNCTION) {
            throw runtimeError("Attempted to call a non-function value", node.getFunction());
        }
        FunctionValue function = functionValue.asFunction();
        List<Value> arguments = new ArrayList<>();
        for (ExpressionNode argument : node.getArguments()) {
            arguments.add(evaluate(argument));
        }
        return invokeFunction(function, arguments, node);
    }
    
    @Override
    public Value visitArrayAccess(ArrayAccessNode node) {
        Value arrayValue = evaluate(node.getArray());
        if (arrayValue.getType() != Value.ValueType.ARRAY) {
            throw runtimeError("Attempted to index non-array value", node.getArray());
        }
        int index = evaluate(node.getIndex()).asInteger();
        return arrayValue.asArray().get(index);
    }
    
    @Override
    public Value visitFunctionLiteral(FunctionLiteralNode node) {
        FunctionValue functionValue = new FunctionValue(node, environment.captureCurrentScope());
        return Value.function(functionValue);
    }
    
    @Override
    public Value visitArrayLiteral(ArrayLiteralNode node) {
        List<Value> elements = new ArrayList<>();
        for (ExpressionNode element : node.getElements()) {
            elements.add(evaluate(element));
        }
        return Value.array(new ArrayValue(elements));
    }
    
    @Override
    public Value visitTupleLiteral(TupleLiteralNode node) {
        TupleValue tuple = new TupleValue();
        for (TupleLiteralNode.TupleElement element : node.getElements()) {
            Value value = element.getValue() != null ? evaluate(element.getValue()) : Value.VOID;
            tuple.append(element.getName(), value);
        }
        return Value.tuple(tuple);
    }
    
    @Override
    public Value visitTypeCheck(TypeCheckNode node) {
        Value value = evaluate(node.getExpression());
        String indicator = node.getTypeIndicator().toLowerCase();
        boolean result = switch (indicator) {
            case "int" -> value.getType() == Value.ValueType.INTEGER;
            case "real" -> value.getType() == Value.ValueType.REAL;
            case "bool" -> value.getType() == Value.ValueType.BOOLEAN;
            case "string" -> value.getType() == Value.ValueType.STRING;
            case "none" -> value.getType() == Value.ValueType.VOID;
            case "array", "[]" -> value.getType() == Value.ValueType.ARRAY;
            case "tuple", "{}" -> value.getType() == Value.ValueType.TUPLE;
            case "func" -> value.getType() == Value.ValueType.FUNCTION;
            default -> throw runtimeError("Unknown type indicator '" + indicator + "'", node);
        };
        return Value.bool(result);
    }
    
    @Override
    public Value visitTupleMemberAccess(TupleMemberAccessNode node) {
        Value tupleValue = evaluate(node.getTuple());
        if (tupleValue.getType() != Value.ValueType.TUPLE) {
            throw runtimeError("Attempted to access member on non-tuple", node.getTuple());
        }
        TupleValue tuple = tupleValue.asTuple();
        return node.isNumericIndex()
            ? tuple.getByIndex(Integer.parseInt(node.getMemberName()))
            : tuple.getByName(node.getMemberName());
    }
    
    private Value evaluate(ExpressionNode node) {
        return node.accept(this);
    }
    
    private void assignTarget(ExpressionNode target, Value value) {
        if (target instanceof ReferenceNode reference) {
            if (!environment.assign(reference.getName(), value)) {
                throw runtimeError("Undefined variable: " + reference.getName(), target);
            }
            return;
        }
        if (target instanceof ArrayAccessNode arrayAccess) {
            Value arrayValue = evaluate(arrayAccess.getArray());
            if (arrayValue.getType() != Value.ValueType.ARRAY) {
                throw runtimeError("Attempted to index non-array value", arrayAccess.getArray());
            }
            int index = evaluate(arrayAccess.getIndex()).asInteger();
            arrayValue.asArray().set(index, value);
            return;
        }
        if (target instanceof TupleMemberAccessNode tupleAccess) {
            Value tupleValue = evaluate(tupleAccess.getTuple());
            if (tupleValue.getType() != Value.ValueType.TUPLE) {
                throw runtimeError("Attempted to access member on non-tuple", tupleAccess.getTuple());
            }
            TupleValue tuple = tupleValue.asTuple();
            if (tupleAccess.isNumericIndex()) {
                tuple.setByIndex(Integer.parseInt(tupleAccess.getMemberName()), value);
            } else {
                tuple.setByName(tupleAccess.getMemberName(), value);
            }
            return;
        }
        throw runtimeError("Invalid assignment target", target);
    }
    
    private Value executeInfiniteLoop(StatementNode body) {
        Value last = Value.VOID;
        while (true) {
            try {
                last = body.accept(this);
            } catch (ContinueSignal ignore) {
                continue;
            } catch (BreakSignal ignore) {
                break;
            }
        }
        return last;
    }
    
    private Value executeRangeLoop(ForNode node) {
        int start = evaluate(node.getIterable()).asInteger();
        int end = evaluate(node.getRangeEnd()).asInteger();
        int step = start <= end ? 1 : -1;
        Value last = Value.VOID;
        for (int current = start; step > 0 ? current <= end : current >= end; current += step) {
            try {
                if (node.getVariable() != null) {
                    environment.enterScope();
                    environment.define(node.getVariable(), Value.integer(current));
                    last = node.getBody().accept(this);
                } else {
                    last = node.getBody().accept(this);
                }
            } catch (ContinueSignal ignore) {
                continue;
            } catch (BreakSignal ignore) {
                break;
            } finally {
                if (node.getVariable() != null) {
                    environment.exitScope();
                }
            }
        }
        return last;
    }
    
    private Value executeIterableLoop(ForNode node) {
        Value iterable = evaluate(node.getIterable());
        Value last = Value.VOID;
        if (iterable.getType() == Value.ValueType.ARRAY) {
            List<Value> elements = iterable.asArray().snapshot();
            for (int i = 0; i < elements.size(); i++) {
                try {
                    Value elementValue = elements.get(i);
                    last = executeLoopBody(node, elementValue != null ? elementValue : Value.VOID);
                } catch (BreakSignal ignore) {
                    break;
                }
            }
            return last;
        }
        if (iterable.getType() == Value.ValueType.TUPLE) {
            List<TupleValue.TupleEntry> entries = iterable.asTuple().snapshot();
            for (TupleValue.TupleEntry entry : entries) {
                try {
                    last = executeLoopBody(node, entry.value());
                } catch (BreakSignal ignore) {
                    break;
                }
            }
            return last;
        }
        throw runtimeError("For-loop expects array or tuple iterable", node.getIterable());
    }
    
    private Value executeLoopBody(ForNode node, Value loopValue) {
        Value last = Value.VOID;
        try {
            if (node.getVariable() != null) {
                environment.enterScope();
                environment.define(node.getVariable(), loopValue);
            }
            last = node.getBody().accept(this);
        } catch (ContinueSignal ignore) {
            // Continue just proceeds to next iteration
        } catch (BreakSignal e) {
            throw e;
        } finally {
            if (node.getVariable() != null) {
                environment.exitScope();
            }
        }
        return last;
    }
    
    private Value invokeFunction(FunctionValue function, List<Value> arguments, AstNode callSite) {
        List<String> parameters = function.getParameters();
        if (arguments.size() != parameters.size()) {
            throw runtimeError(
                "Function expected " + parameters.size() + " arguments but received " + arguments.size(),
                callSite);
        }
        Environment.RuntimeScope previousScope = environment.pushFunctionScope(function.getClosureScope());
        try {
            for (int i = 0; i < parameters.size(); i++) {
                environment.define(parameters.get(i), arguments.get(i));
            }
            if (function.isExpressionBody()) {
                return evaluate(function.getExpressionBody());
            }
            Value last = Value.VOID;
            try {
                for (StatementNode statement : function.getStatementBody()) {
                    last = statement.accept(this);
                }
                return last;
            } catch (ReturnSignal signal) {
                return signal.value;
            }
        } finally {
            environment.restoreScope(previousScope);
        }
    }
    
    private Value add(Value left, Value right, AstNode node) {
        if (left.isNumeric() && right.isNumeric()) {
            if (left.getType() == Value.ValueType.REAL || right.getType() == Value.ValueType.REAL) {
                return Value.real(left.asReal() + right.asReal());
            }
            return Value.integer(left.asInteger() + right.asInteger());
        }
        if (left.getType() == Value.ValueType.STRING && right.getType() == Value.ValueType.STRING) {
            return Value.string(left.asString() + right.asString());
        }
        if (left.getType() == Value.ValueType.ARRAY && right.getType() == Value.ValueType.ARRAY) {
            return Value.array(left.asArray().concat(right.asArray()));
        }
        if (left.getType() == Value.ValueType.TUPLE && right.getType() == Value.ValueType.TUPLE) {
            return Value.tuple(left.asTuple().concat(right.asTuple()));
        }
        throw runtimeError("Unsupported operand types for '+': " + left.getType() + " and " + right.getType(), node);
    }
    
    private Value subtract(Value left, Value right, AstNode node) {
        if (!left.isNumeric() || !right.isNumeric()) {
            throw runtimeError("'-' expects numeric operands", node);
        }
        if (left.getType() == Value.ValueType.REAL || right.getType() == Value.ValueType.REAL) {
            return Value.real(left.asReal() - right.asReal());
        }
        return Value.integer(left.asInteger() - right.asInteger());
    }
    
    private Value multiply(Value left, Value right, AstNode node) {
        if (!left.isNumeric() || !right.isNumeric()) {
            throw runtimeError("'*' expects numeric operands", node);
        }
        if (left.getType() == Value.ValueType.REAL || right.getType() == Value.ValueType.REAL) {
            return Value.real(left.asReal() * right.asReal());
        }
        return Value.integer(left.asInteger() * right.asInteger());
    }
    
    private Value divide(Value left, Value right, AstNode node) {
        if (!left.isNumeric() || !right.isNumeric()) {
            throw runtimeError("'/' expects numeric operands", node);
        }
        if ((right.getType() == Value.ValueType.INTEGER && right.asInteger() == 0)
            || (right.getType() == Value.ValueType.REAL && right.asReal() == 0.0)) {
            throw runtimeError("Division by zero", node);
        }
        if (left.getType() == Value.ValueType.INTEGER && right.getType() == Value.ValueType.INTEGER) {
            return Value.integer(Math.floorDiv(left.asInteger(), right.asInteger()));
        }
        return Value.real(left.asReal() / right.asReal());
    }
    
    private Value compare(Value left, Value right, String operator, AstNode node) {
        if (!left.isNumeric() || !right.isNumeric()) {
            throw runtimeError("Comparison operators expect numeric operands", node);
        }
        double l = left.asReal();
        double r = right.asReal();
        return switch (operator) {
            case "<" -> Value.bool(l < r);
            case "<=" -> Value.bool(l <= r);
            case ">" -> Value.bool(l > r);
            case ">=" -> Value.bool(l >= r);
            default -> throw runtimeError("Unsupported comparison operator", node);
        };
    }
    
    private boolean equals(Value left, Value right) {
        if (left.isNumeric() && right.isNumeric()) {
            return Double.compare(left.asReal(), right.asReal()) == 0;
        }
        if (left.getType() != right.getType()) {
            return false;
        }
        return switch (left.getType()) {
            case BOOLEAN, STRING -> left.getValue().equals(right.getValue());
            case ARRAY -> arraysEqual(left.asArray(), right.asArray());
            case TUPLE -> tuplesEqual(left.asTuple(), right.asTuple());
            case FUNCTION -> left.getValue() == right.getValue();
            case VOID -> true;
            default -> left.getValue().equals(right.getValue());
        };
    }
    
    private boolean arraysEqual(ArrayValue left, ArrayValue right) {
        List<Value> leftElements = left.snapshot();
        List<Value> rightElements = right.snapshot();
        if (leftElements.size() != rightElements.size()) {
            return false;
        }
        for (int i = 0; i < leftElements.size(); i++) {
            Value leftValue = leftElements.get(i) != null ? leftElements.get(i) : Value.VOID;
            Value rightValue = rightElements.get(i) != null ? rightElements.get(i) : Value.VOID;
            if (!equals(leftValue, rightValue)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean tuplesEqual(TupleValue left, TupleValue right) {
        List<TupleValue.TupleEntry> leftEntries = left.snapshot();
        List<TupleValue.TupleEntry> rightEntries = right.snapshot();
        if (leftEntries.size() != rightEntries.size()) {
            return false;
        }
        for (int i = 0; i < leftEntries.size(); i++) {
            TupleValue.TupleEntry leftEntry = leftEntries.get(i);
            TupleValue.TupleEntry rightEntry = rightEntries.get(i);
            if (leftEntry.name() == null ? rightEntry.name() != null : !leftEntry.name().equals(rightEntry.name())) {
                return false;
            }
            if (!equals(leftEntry.value(), rightEntry.value())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean requireBoolean(Value value, AstNode context) {
        if (value.getType() != Value.ValueType.BOOLEAN) {
            throw runtimeError("Expected boolean value but got " + value.getType(), context);
        }
        return value.asBoolean();
    }
    
    private RuntimeError runtimeError(String message, AstNode node) {
        return new RuntimeError(message, node.getLine(), node.getColumn());
    }
    
    private static final class BreakSignal extends RuntimeException {
        private BreakSignal() {
            super(null, null, false, false);
        }
    }
    
    private static final class ContinueSignal extends RuntimeException {
        private ContinueSignal() {
            super(null, null, false, false);
        }
    }
    
    private static final class ReturnSignal extends RuntimeException {
        private final Value value;
        private ReturnSignal(Value value) {
            super(null, null, false, false);
            this.value = value;
        }
    }
}
