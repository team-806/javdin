package com.javdin.semantics;

import com.javdin.ast.*;
import com.javdin.utils.ErrorHandler;

import java.util.*;

/**
 * AST optimizer that performs various optimizations.
 */
public class Optimizer implements AstVisitor<AstNode> {
    private final ErrorHandler errorHandler;
    private final Set<String> usedVariables;
    private boolean hasReturnStatement;
    
    public Optimizer(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.usedVariables = new HashSet<>();
        this.hasReturnStatement = false;
    }
    
    public ProgramNode optimize(ProgramNode program) {
        // First pass: collect used variables
        collectUsedVariables(program);
        
        // Second pass: apply optimizations
        return (ProgramNode) program.accept(this);
    }
    
    private void collectUsedVariables(AstNode node) {
        if (node instanceof ReferenceNode) {
            usedVariables.add(((ReferenceNode) node).getName());
        } else if (node instanceof ProgramNode) {
            ((ProgramNode) node).getStatements().forEach(this::collectUsedVariables);
        } else if (node instanceof BlockNode) {
            ((BlockNode) node).getStatements().forEach(this::collectUsedVariables);
        } else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            collectUsedVariables(ifNode.getCondition());
            collectUsedVariables(ifNode.getThenStatement());
            if (ifNode.getElseStatement() != null) {
                collectUsedVariables(ifNode.getElseStatement());
            }
        } else if (node instanceof WhileNode) {
            WhileNode whileNode = (WhileNode) node;
            collectUsedVariables(whileNode.getCondition());
            collectUsedVariables(whileNode.getBody());
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assignment = (AssignmentNode) node;
            collectUsedVariables(assignment.getTarget());
            collectUsedVariables(assignment.getValue());
        } else if (node instanceof BinaryOpNode) {
            BinaryOpNode binaryOp = (BinaryOpNode) node;
            collectUsedVariables(binaryOp.getLeft());
            collectUsedVariables(binaryOp.getRight());
        } else if (node instanceof UnaryOpNode) {
            collectUsedVariables(((UnaryOpNode) node).getOperand());
        } else if (node instanceof FunctionCallNode) {
            FunctionCallNode callNode = (FunctionCallNode) node;
            collectUsedVariables(callNode.getFunction());
            callNode.getArguments().forEach(this::collectUsedVariables);
        } else if (node instanceof ArrayAccessNode) {
            ArrayAccessNode arrayAccess = (ArrayAccessNode) node;
            collectUsedVariables(arrayAccess.getArray());
            collectUsedVariables(arrayAccess.getIndex());
        } else if (node instanceof ArrayLiteralNode) {
            ((ArrayLiteralNode) node).getElements().forEach(this::collectUsedVariables);
        } else if (node instanceof TupleLiteralNode) {
            ((TupleLiteralNode) node).getElements().forEach(e -> {
                if (e.getValue() != null) collectUsedVariables(e.getValue());
            });
        } else if (node instanceof DeclarationNode) {
            DeclarationNode decl = (DeclarationNode) node;
            for (DeclarationNode.VariableDefinition varDef : decl.getVariables()) {
                if (varDef.getInitialValue() != null) {
                    collectUsedVariables(varDef.getInitialValue());
                }
            }
        } else if (node instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) node;
            if (returnNode.getValue() != null) {
                collectUsedVariables(returnNode.getValue());
            }
        } else if (node instanceof PrintNode) {
            ((PrintNode) node).getExpressions().forEach(this::collectUsedVariables);
        } else if (node instanceof ForNode) {
            ForNode forNode = (ForNode) node;
            if (forNode.getIterable() != null) {
                collectUsedVariables(forNode.getIterable());
            }
            if (forNode.getRangeEnd() != null) {
                collectUsedVariables(forNode.getRangeEnd());
            }
            collectUsedVariables(forNode.getBody());
        } else if (node instanceof ExpressionStatementNode) {
            collectUsedVariables(((ExpressionStatementNode) node).getExpression());
        } else if (node instanceof FunctionLiteralNode) {
            FunctionLiteralNode funcNode = (FunctionLiteralNode) node;
            if (funcNode.isExpressionBody()) {
                collectUsedVariables(funcNode.getExpressionBody());
            } else {
                funcNode.getStatementBody().forEach(this::collectUsedVariables);
            }
        } else if (node instanceof TypeCheckNode) {
            collectUsedVariables(((TypeCheckNode) node).getExpression());
        } else if (node instanceof TupleMemberAccessNode) {
            collectUsedVariables(((TupleMemberAccessNode) node).getTuple());
        }
        // Add more cases as needed
    }
    
    @Override
    public AstNode visitProgram(ProgramNode node) {
        List<StatementNode> optimizedStatements = new ArrayList<>();
        hasReturnStatement = false;
        
        for (StatementNode stmt : node.getStatements()) {
            if (!hasReturnStatement) {
                StatementNode optimized = (StatementNode) stmt.accept(this);
                if (optimized != null) {
                    optimizedStatements.add(optimized);
                }
            } else {
                // Optimization 4: Remove unreachable code after return
                errorHandler.addError("Unreachable code detected after return", 
                                    stmt.getLine(), stmt.getColumn());
            }
        }
        
        return new ProgramNode(optimizedStatements, node.getLine(), node.getColumn());
    }
    
    @Override
    public AstNode visitDeclaration(DeclarationNode node) {
        // Handle multi-variable declarations
        List<DeclarationNode.VariableDefinition> optimizedVars = new ArrayList<>();
        
        for (DeclarationNode.VariableDefinition varDef : node.getVariables()) {
            // Optimization 2: Remove unused variables
            if (!usedVariables.contains(varDef.getName()) && 
                !varDef.getName().startsWith("_")) { // Keep variables starting with _
                errorHandler.addError("Unused variable '" + varDef.getName() + "'", 
                                    node.getLine(), node.getColumn());
                // Skip this variable
                continue;
            }
            
            ExpressionNode optimizedInitialValue = null;
            if (varDef.getInitialValue() != null) {
                optimizedInitialValue = (ExpressionNode) varDef.getInitialValue().accept(this);
            }
            
            optimizedVars.add(new DeclarationNode.VariableDefinition(varDef.getName(), optimizedInitialValue));
        }
        
        // If all variables were removed, return null
        if (optimizedVars.isEmpty()) {
            return null;
        }
        
        return new DeclarationNode(optimizedVars, node.getLine(), node.getColumn());
    }
    
    @Override
    public AstNode visitBinaryOp(BinaryOpNode node) {
        ExpressionNode left = (ExpressionNode) node.getLeft().accept(this);
        ExpressionNode right = (ExpressionNode) node.getRight().accept(this);
        
        // Optimization 1: Constant expression simplification
        if (left instanceof LiteralNode && right instanceof LiteralNode) {
            LiteralNode result = foldConstants((LiteralNode) left, (LiteralNode) right, node.getOperator());
            if (result != null) {
                return result;
            }
        }
        
        return new BinaryOpNode(node.getLine(), node.getColumn(), left, node.getOperator(), right);
    }
    
    @Override
    public AstNode visitIf(IfNode node) {
        ExpressionNode condition = (ExpressionNode) node.getCondition().accept(this);
        
        // Optimization 3: Simplify conditional structures
        if (condition instanceof LiteralNode) {
            LiteralNode literal = (LiteralNode) condition;
            if (literal.getType() == LiteralNode.LiteralType.BOOLEAN) {
                boolean value = (Boolean) literal.getValue();
                if (value) {
                    // Always true, keep only then branch
                    return node.getThenStatement().accept(this);
                } else {
                    // Always false, keep only else branch if exists
                    if (node.getElseStatement() != null) {
                        return node.getElseStatement().accept(this);
                    } else {
                        return new BlockNode(node.getLine(), node.getColumn(), new ArrayList<>());
                    }
                }
            }
        }
        
        StatementNode thenBranch = (StatementNode) node.getThenStatement().accept(this);
        StatementNode elseBranch = node.getElseStatement() != null ? 
            (StatementNode) node.getElseStatement().accept(this) : null;
            
        return new IfNode(node.getLine(), node.getColumn(), condition, thenBranch, elseBranch);
    }
    
    @Override
    public AstNode visitReturn(ReturnNode node) {
        hasReturnStatement = true;
        
        ExpressionNode optimizedValue = null;
        if (node.getValue() != null) {
            optimizedValue = (ExpressionNode) node.getValue().accept(this);
        }
        
        return new ReturnNode(node.getLine(), node.getColumn(), optimizedValue);
    }
    
    @Override
    public AstNode visitBlock(BlockNode node) {
        List<StatementNode> optimizedStatements = new ArrayList<>();
        boolean localHasReturn = false;
        
        for (StatementNode stmt : node.getStatements()) {
            if (!localHasReturn) {
                StatementNode optimized = (StatementNode) stmt.accept(this);
                if (optimized != null) {
                    optimizedStatements.add(optimized);
                    if (optimized instanceof ReturnNode) {
                        localHasReturn = true;
                    }
                }
            } else {
                // Remove unreachable code in blocks
                errorHandler.addError("Unreachable code in block after return", 
                                    stmt.getLine(), stmt.getColumn());
            }
        }
        
        return new BlockNode(node.getLine(), node.getColumn(), optimizedStatements);
    }
    
    // Constant folding helper
    private LiteralNode foldConstants(LiteralNode left, LiteralNode right, String operator) {
        try {
            // Handle arithmetic operations
            if (isNumeric(left) && isNumeric(right)) {
                return foldNumericConstants(left, right, operator);
            }
            
            // Handle boolean operations
            if (left.getType() == LiteralNode.LiteralType.BOOLEAN && 
                right.getType() == LiteralNode.LiteralType.BOOLEAN) {
                return foldBooleanConstants(left, right, operator);
            }
            
            // Handle comparisons
            if (isNumeric(left) && isNumeric(right)) {
                return foldComparisonConstants(left, right, operator);
            }
            
        } catch (Exception e) {
            // If folding fails, return null to keep original expression
        }
        
        return null;
    }
    
    private boolean isNumeric(LiteralNode node) {
        return node.getType() == LiteralNode.LiteralType.INTEGER || 
               node.getType() == LiteralNode.LiteralType.REAL;
    }
    
    private LiteralNode foldNumericConstants(LiteralNode left, LiteralNode right, String operator) {
        double leftVal = left.getType() == LiteralNode.LiteralType.INTEGER ? 
            ((Number) left.getValue()).doubleValue() : ((Number) left.getValue()).doubleValue();
        double rightVal = right.getType() == LiteralNode.LiteralType.INTEGER ? 
            ((Number) right.getValue()).doubleValue() : ((Number) right.getValue()).doubleValue();
            
        double result = 0;
        switch (operator) {
            case "+": result = leftVal + rightVal; break;
            case "-": result = leftVal - rightVal; break;
            case "*": result = leftVal * rightVal; break;
            case "/": 
                if (rightVal == 0) return null; // Division by zero
                result = leftVal / rightVal; 
                break;
            default: return null;
        }
        
        // Return as integer if both were integers and result is integer
        if (left.getType() == LiteralNode.LiteralType.INTEGER && 
            right.getType() == LiteralNode.LiteralType.INTEGER &&
            result == (int)result) {
            return new LiteralNode((int)result, LiteralNode.LiteralType.INTEGER, 
                                 left.getLine(), left.getColumn());
        }
        
        return new LiteralNode(result, LiteralNode.LiteralType.REAL, 
                             left.getLine(), left.getColumn());
    }
    
    private LiteralNode foldBooleanConstants(LiteralNode left, LiteralNode right, String operator) {
        boolean leftVal = (Boolean) left.getValue();
        boolean rightVal = (Boolean) right.getValue();
        boolean result = false;
        
        switch (operator) {
            case "and": result = leftVal && rightVal; break;
            case "or": result = leftVal || rightVal; break;
            case "xor": result = leftVal != rightVal; break;
            default: return null;
        }
        
        return new LiteralNode(result, LiteralNode.LiteralType.BOOLEAN, 
                             left.getLine(), left.getColumn());
    }
    
    private LiteralNode foldComparisonConstants(LiteralNode left, LiteralNode right, String operator) {
        double leftVal = left.getType() == LiteralNode.LiteralType.INTEGER ? 
            ((Number) left.getValue()).doubleValue() : ((Number) left.getValue()).doubleValue();
        double rightVal = right.getType() == LiteralNode.LiteralType.INTEGER ? 
            ((Number) right.getValue()).doubleValue() : ((Number) right.getValue()).doubleValue();
            
        boolean result = false;
        switch (operator) {
            case "<": result = leftVal < rightVal; break;
            case "<=": result = leftVal <= rightVal; break;
            case ">": result = leftVal > rightVal; break;
            case ">=": result = leftVal >= rightVal; break;
            case "=": result = leftVal == rightVal; break;
            case "!=": result = leftVal != rightVal; break;
            default: return null;
        }
        
        return new LiteralNode(result, LiteralNode.LiteralType.BOOLEAN, 
                             left.getLine(), left.getColumn());
    }
    
    // Default implementations for other nodes (pass-through)
    @Override public AstNode visitAssignment(AssignmentNode node) { 
        ExpressionNode target = (ExpressionNode) node.getTarget().accept(this);
        ExpressionNode value = (ExpressionNode) node.getValue().accept(this);
        return new AssignmentNode(node.getLine(), node.getColumn(), target, value);
    }
    
    @Override public AstNode visitWhile(WhileNode node) {
        ExpressionNode condition = (ExpressionNode) node.getCondition().accept(this);
        StatementNode body = (StatementNode) node.getBody().accept(this);
        return new WhileNode(node.getLine(), node.getColumn(), condition, body);
    }
    
    @Override public AstNode visitFor(ForNode node) {
        ExpressionNode iterable = node.getIterable() != null ? (ExpressionNode) node.getIterable().accept(this) : null;
        ExpressionNode rangeEnd = node.getRangeEnd() != null ? (ExpressionNode) node.getRangeEnd().accept(this) : null;
        StatementNode body = (StatementNode) node.getBody().accept(this);
        
        // Use appropriate constructor based on what's available
        if (rangeEnd != null) {
            // Range loop
            return new ForNode(node.getLine(), node.getColumn(), node.getVariable(), iterable, rangeEnd, body);
        } else if (iterable != null) {
            // For-in loop
            return new ForNode(node.getLine(), node.getColumn(), node.getVariable(), iterable, body);
        } else {
            // Infinite loop
            return new ForNode(node.getLine(), node.getColumn(), body);
        }
    }
    
    @Override public AstNode visitBreak(BreakNode node) { return node; }
    @Override public AstNode visitContinue(ContinueNode node) { return node; }
    @Override public AstNode visitPrint(PrintNode node) { 
        List<ExpressionNode> optimizedExprs = new ArrayList<>();
        for (ExpressionNode expr : node.getExpressions()) {
            optimizedExprs.add((ExpressionNode) expr.accept(this));
        }
        return new PrintNode(node.getLine(), node.getColumn(), optimizedExprs);
    }
    
    @Override public AstNode visitExpressionStatement(ExpressionStatementNode node) {
        ExpressionNode expr = (ExpressionNode) node.getExpression().accept(this);
        return new ExpressionStatementNode(node.getLine(), node.getColumn(), expr);
    }
    
    @Override public AstNode visitReference(ReferenceNode node) { return node; }
    @Override public AstNode visitUnaryOp(UnaryOpNode node) {
        ExpressionNode operand = (ExpressionNode) node.getOperand().accept(this);
        return new UnaryOpNode(node.getLine(), node.getColumn(), node.getOperator(), operand);
    }
    
    @Override public AstNode visitFunctionCall(FunctionCallNode node) {
        ExpressionNode function = (ExpressionNode) node.getFunction().accept(this);
        List<ExpressionNode> optimizedArgs = new ArrayList<>();
        for (ExpressionNode arg : node.getArguments()) {
            optimizedArgs.add((ExpressionNode) arg.accept(this));
        }
        return new FunctionCallNode(node.getLine(), node.getColumn(), function, optimizedArgs);
    }
    
    @Override public AstNode visitArrayAccess(ArrayAccessNode node) {
        ExpressionNode array = (ExpressionNode) node.getArray().accept(this);
        ExpressionNode index = (ExpressionNode) node.getIndex().accept(this);
        return new ArrayAccessNode(node.getLine(), node.getColumn(), array, index);
    }
    
    @Override public AstNode visitFunctionLiteral(FunctionLiteralNode node) {
        if (node.isExpressionBody()) {
            ExpressionNode body = (ExpressionNode) node.getExpressionBody().accept(this);
            return new FunctionLiteralNode(node.getLine(), node.getColumn(), node.getParameters(), body, true);
        } else {
            List<StatementNode> optimizedBody = new ArrayList<>();
            for (StatementNode stmt : node.getStatementBody()) {
                AstNode optimized = stmt.accept(this);
                if (optimized != null) {
                    optimizedBody.add((StatementNode) optimized);
                }
            }
            return new FunctionLiteralNode(node.getLine(), node.getColumn(), node.getParameters(), optimizedBody, false);
        }
    }
    
    @Override public AstNode visitArrayLiteral(ArrayLiteralNode node) {
        List<ExpressionNode> optimizedElements = new ArrayList<>();
        for (ExpressionNode element : node.getElements()) {
            optimizedElements.add((ExpressionNode) element.accept(this));
        }
        return new ArrayLiteralNode(optimizedElements, node.getLine(), node.getColumn());
    }
    
    @Override public AstNode visitTupleLiteral(TupleLiteralNode node) {
        List<TupleLiteralNode.TupleElement> optimizedElements = new ArrayList<>();
        for (TupleLiteralNode.TupleElement element : node.getElements()) {
            ExpressionNode optimizedExpr = element.getValue() != null ? 
                (ExpressionNode) element.getValue().accept(this) : null;
            optimizedElements.add(new TupleLiteralNode.TupleElement(element.getName(), optimizedExpr));
        }
        return new TupleLiteralNode(optimizedElements, node.getLine(), node.getColumn());
    }
    
    @Override public AstNode visitTypeCheck(TypeCheckNode node) {
        ExpressionNode expr = (ExpressionNode) node.getExpression().accept(this);
        return new TypeCheckNode(node.getLine(), node.getColumn(), expr, node.getTypeIndicator());
    }
    
    @Override public AstNode visitTupleMemberAccess(TupleMemberAccessNode node) {
        ExpressionNode tuple = (ExpressionNode) node.getTuple().accept(this);
        return new TupleMemberAccessNode(node.getLine(), node.getColumn(), tuple, node.getMemberName());
    }
    
    @Override public AstNode visitLiteral(LiteralNode node) { return node; }
}