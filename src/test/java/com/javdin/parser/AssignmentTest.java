package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for assignment statement parsing.
 */
class AssignmentTest {
    
    @Test
    void testSimpleVariableAssignment() {
        Lexer lexer = new Lexer("x := 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(AssignmentNode.class);
        
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        assertThat(assignment.getTarget()).isInstanceOf(ReferenceNode.class);
        
        ReferenceNode target = (ReferenceNode) assignment.getTarget();
        assertThat(target.getName()).isEqualTo("x");
        
        assertThat(assignment.getValue()).isInstanceOf(LiteralNode.class);
        LiteralNode value = (LiteralNode) assignment.getValue();
        assertThat(value.getValue()).isEqualTo(42);
    }
    
    @Test
    void testAssignmentWithExpression() {
        Lexer lexer = new Lexer("result := x + y * 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getTarget()).isInstanceOf(ReferenceNode.class);
        assertThat(assignment.getValue()).isInstanceOf(BinaryOpNode.class);
        
        BinaryOpNode expr = (BinaryOpNode) assignment.getValue();
        assertThat(expr.getOperator()).isEqualTo("+");
    }
    
    @Test
    void testArrayElementAssignment() {
        Lexer lexer = new Lexer("arr[0] := 100");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getTarget()).isInstanceOf(ArrayAccessNode.class);
        
        ArrayAccessNode target = (ArrayAccessNode) assignment.getTarget();
        assertThat(target.getArray()).isInstanceOf(ReferenceNode.class);
        assertThat(target.getIndex()).isInstanceOf(LiteralNode.class);
        
        LiteralNode index = (LiteralNode) target.getIndex();
        assertThat(index.getValue()).isEqualTo(0);
    }
    
    @Test
    void testNestedArrayAssignment() {
        Lexer lexer = new Lexer("matrix[i][j] := value");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getTarget()).isInstanceOf(ArrayAccessNode.class);
        
        ArrayAccessNode outer = (ArrayAccessNode) assignment.getTarget();
        assertThat(outer.getArray()).isInstanceOf(ArrayAccessNode.class);
        
        ArrayAccessNode inner = (ArrayAccessNode) outer.getArray();
        assertThat(inner.getArray()).isInstanceOf(ReferenceNode.class);
        
        ReferenceNode matrixRef = (ReferenceNode) inner.getArray();
        assertThat(matrixRef.getName()).isEqualTo("matrix");
    }
    
    @Test
    void testTupleMemberAssignment() {
        Lexer lexer = new Lexer("point.x := 10");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getTarget()).isInstanceOf(TupleMemberAccessNode.class);
        
        TupleMemberAccessNode target = (TupleMemberAccessNode) assignment.getTarget();
        assertThat(target.getTuple()).isInstanceOf(ReferenceNode.class);
        assertThat(target.getMemberName()).isEqualTo("x");
    }
    
    @Test
    void testTupleNumericMemberAssignment() {
        Lexer lexer = new Lexer("tup.0 := 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getTarget()).isInstanceOf(TupleMemberAccessNode.class);
        
        TupleMemberAccessNode target = (TupleMemberAccessNode) assignment.getTarget();
        assertThat(target.isNumericIndex()).isTrue();
        assertThat(Integer.parseInt(target.getMemberName())).isEqualTo(0);
    }
    
    @Test
    void testComplexLeftHandSide() {
        Lexer lexer = new Lexer("obj.field[5].data := 999");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        // Should be: TupleMemberAccess(ArrayAccess(TupleMemberAccess(ReferenceNode)))
        assertThat(assignment.getTarget()).isInstanceOf(TupleMemberAccessNode.class);
        
        TupleMemberAccessNode outerMember = (TupleMemberAccessNode) assignment.getTarget();
        assertThat(outerMember.getMemberName()).isEqualTo("data");
        assertThat(outerMember.getTuple()).isInstanceOf(ArrayAccessNode.class);
        
        ArrayAccessNode arrayAccess = (ArrayAccessNode) outerMember.getTuple();
        assertThat(arrayAccess.getArray()).isInstanceOf(TupleMemberAccessNode.class);
        
        TupleMemberAccessNode innerMember = (TupleMemberAccessNode) arrayAccess.getArray();
        assertThat(innerMember.getMemberName()).isEqualTo("field");
    }
    
    @Test
    void testMultipleAssignments() {
        Lexer lexer = new Lexer("x := 1\ny := 2\nz := 3");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(3);
        for (StatementNode stmt : program.getStatements()) {
            assertThat(stmt).isInstanceOf(AssignmentNode.class);
        }
    }
    
    @Test
    void testAssignmentWithFunctionCall() {
        Lexer lexer = new Lexer("result := compute(x, y)");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getValue()).isInstanceOf(FunctionCallNode.class);
        
        FunctionCallNode call = (FunctionCallNode) assignment.getValue();
        assertThat(call.getArguments()).hasSize(2);
    }
    
    @Test
    void testAssignmentWithArrayLiteral() {
        Lexer lexer = new Lexer("numbers := [1, 2, 3, 4, 5]");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getValue()).isInstanceOf(ArrayLiteralNode.class);
        
        ArrayLiteralNode array = (ArrayLiteralNode) assignment.getValue();
        assertThat(array.getElements()).hasSize(5);
    }
    
    @Test
    void testAssignmentWithTupleLiteral() {
        Lexer lexer = new Lexer("point := {x := 10, y := 20}");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getValue()).isInstanceOf(TupleLiteralNode.class);
        
        TupleLiteralNode tuple = (TupleLiteralNode) assignment.getValue();
        assertThat(tuple.getElements()).hasSize(2);
    }
    
    @Test
    void testAssignmentWithFunctionLiteral() {
        Lexer lexer = new Lexer("handler := func(x) => x * 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(0);
        
        assertThat(assignment.getValue()).isInstanceOf(FunctionLiteralNode.class);
    }
    
    @Test
    void testDeclarationFollowedByAssignment() {
        Lexer lexer = new Lexer("var x := 10\nx := 20");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(AssignmentNode.class);
        
        AssignmentNode assignment = (AssignmentNode) program.getStatements().get(1);
        ReferenceNode target = (ReferenceNode) assignment.getTarget();
        assertThat(target.getName()).isEqualTo("x");
        
        LiteralNode value = (LiteralNode) assignment.getValue();
        assertThat(value.getValue()).isEqualTo(20);
    }
}
