package com.javdin.parser;

import com.javdin.ast.*;
import com.javdin.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for Milestone 8: Return and Print statements.
 * Tests all aspects of the completion criteria.
 */
class ReturnPrintTest {

    // ========== Return Statement Tests ==========

    @Test
    void testReturnWithIntegerLiteral() {
        Lexer lexer = new Lexer("return 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ReturnNode.class);
        
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        assertThat(returnNode.getValue()).isNotNull();
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        
        LiteralNode literal = (LiteralNode) returnNode.getValue();
        assertThat(literal.getValue()).isEqualTo(42);
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.INTEGER);
    }

    @Test
    void testReturnWithRealLiteral() {
        Lexer lexer = new Lexer("return 3.14");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        LiteralNode literal = (LiteralNode) returnNode.getValue();
        assertThat(literal.getValue()).isEqualTo(3.14);
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.REAL);
    }

    @Test
    void testReturnWithStringLiteral() {
        Lexer lexer = new Lexer("return \"hello\"");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        LiteralNode literal = (LiteralNode) returnNode.getValue();
        assertThat(literal.getValue()).isEqualTo("hello");
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.STRING);
    }

    @Test
    void testReturnWithBooleanLiteral() {
        Lexer lexer = new Lexer("return true");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        LiteralNode literal = (LiteralNode) returnNode.getValue();
        assertThat(literal.getValue()).isEqualTo(true);
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.BOOLEAN);
    }

    @Test
    void testReturnWithNone() {
        Lexer lexer = new Lexer("return none");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        LiteralNode literal = (LiteralNode) returnNode.getValue();
        assertThat(literal.getValue()).isNull();
        assertThat(literal.getType()).isEqualTo(LiteralNode.LiteralType.NONE);
    }

    @Test
    void testReturnWithoutValue() {
        Lexer lexer = new Lexer("return");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ReturnNode.class);
        
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        assertThat(returnNode.getValue()).isNull();
    }

    @Test
    void testReturnWithVariable() {
        Lexer lexer = new Lexer("return x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(ReferenceNode.class);
        ReferenceNode ref = (ReferenceNode) returnNode.getValue();
        assertThat(ref.getName()).isEqualTo("x");
    }

    @Test
    void testReturnWithBinaryExpression() {
        Lexer lexer = new Lexer("return x + y");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(BinaryOpNode.class);
        BinaryOpNode binOp = (BinaryOpNode) returnNode.getValue();
        assertThat(binOp.getOperator()).isEqualTo("+");
    }

    @Test
    void testReturnWithComplexExpression() {
        Lexer lexer = new Lexer("return (a + b) * c - d / 2");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(BinaryOpNode.class);
    }

    @Test
    void testReturnWithFunctionCall() {
        Lexer lexer = new Lexer("return factorial(5)");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(FunctionCallNode.class);
        FunctionCallNode call = (FunctionCallNode) returnNode.getValue();
        assertThat(call.getArguments()).hasSize(1);
    }

    @Test
    void testReturnWithArrayLiteral() {
        Lexer lexer = new Lexer("return [1, 2, 3]");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(ArrayLiteralNode.class);
        ArrayLiteralNode array = (ArrayLiteralNode) returnNode.getValue();
        assertThat(array.getElements()).hasSize(3);
    }

    @Test
    void testReturnWithTupleLiteral() {
        Lexer lexer = new Lexer("return {x := 1, y := 2}");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(TupleLiteralNode.class);
        TupleLiteralNode tuple = (TupleLiteralNode) returnNode.getValue();
        assertThat(tuple.getElements()).hasSize(2);
    }

    @Test
    void testReturnWithArrayAccess() {
        Lexer lexer = new Lexer("return arr[5]");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(ArrayAccessNode.class);
    }

    @Test
    void testReturnWithTupleMemberAccess() {
        Lexer lexer = new Lexer("return obj.field");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        
        assertThat(returnNode.getValue()).isInstanceOf(TupleMemberAccessNode.class);
    }

    // ========== Print Statement Tests ==========

    @Test
    void testPrintSingleInteger() {
        Lexer lexer = new Lexer("print 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(PrintNode.class);
        
        PrintNode print = (PrintNode) program.getStatements().get(0);
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(LiteralNode.class);
        
        LiteralNode literal = (LiteralNode) print.getExpressions().get(0);
        assertThat(literal.getValue()).isEqualTo(42);
    }

    @Test
    void testPrintSingleString() {
        Lexer lexer = new Lexer("print \"Hello, World!\"");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        LiteralNode literal = (LiteralNode) print.getExpressions().get(0);
        assertThat(literal.getValue()).isEqualTo("Hello, World!");
    }

    @Test
    void testPrintVariable() {
        Lexer lexer = new Lexer("print x");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(ReferenceNode.class);
        
        ReferenceNode ref = (ReferenceNode) print.getExpressions().get(0);
        assertThat(ref.getName()).isEqualTo("x");
    }

    @Test
    void testPrintMultipleExpressions() {
        Lexer lexer = new Lexer("print 1, 2, 3");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(3);
        
        for (int i = 0; i < 3; i++) {
            assertThat(print.getExpressions().get(i)).isInstanceOf(LiteralNode.class);
            LiteralNode literal = (LiteralNode) print.getExpressions().get(i);
            assertThat(literal.getValue()).isEqualTo(i + 1);
        }
    }

    @Test
    void testPrintMixedTypes() {
        Lexer lexer = new Lexer("print \"Value:\", x, 42, true");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(4);
        
        // "Value:"
        assertThat(print.getExpressions().get(0)).isInstanceOf(LiteralNode.class);
        LiteralNode str = (LiteralNode) print.getExpressions().get(0);
        assertThat(str.getValue()).isEqualTo("Value:");
        
        // x
        assertThat(print.getExpressions().get(1)).isInstanceOf(ReferenceNode.class);
        
        // 42
        assertThat(print.getExpressions().get(2)).isInstanceOf(LiteralNode.class);
        LiteralNode num = (LiteralNode) print.getExpressions().get(2);
        assertThat(num.getValue()).isEqualTo(42);
        
        // true
        assertThat(print.getExpressions().get(3)).isInstanceOf(LiteralNode.class);
        LiteralNode bool = (LiteralNode) print.getExpressions().get(3);
        assertThat(bool.getValue()).isEqualTo(true);
    }

    @Test
    void testPrintWithExpression() {
        Lexer lexer = new Lexer("print x + y");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(BinaryOpNode.class);
    }

    @Test
    void testPrintWithComplexExpressions() {
        Lexer lexer = new Lexer("print a + b, c * d, e / f");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(3);
        assertThat(print.getExpressions().get(0)).isInstanceOf(BinaryOpNode.class);
        assertThat(print.getExpressions().get(1)).isInstanceOf(BinaryOpNode.class);
        assertThat(print.getExpressions().get(2)).isInstanceOf(BinaryOpNode.class);
    }

    @Test
    void testPrintWithFunctionCall() {
        Lexer lexer = new Lexer("print factorial(n)");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(FunctionCallNode.class);
    }

    @Test
    void testPrintWithArrayLiteral() {
        Lexer lexer = new Lexer("print [1, 2, 3]");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(ArrayLiteralNode.class);
    }

    @Test
    void testPrintWithTupleLiteral() {
        Lexer lexer = new Lexer("print {x := 1, y := 2}");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(1);
        assertThat(print.getExpressions().get(0)).isInstanceOf(TupleLiteralNode.class);
    }

    @Test
    void testPrintMultipleComplexExpressions() {
        Lexer lexer = new Lexer("print \"Result:\", arr[i], obj.field, myFunc(x)");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        PrintNode print = (PrintNode) program.getStatements().get(0);
        
        assertThat(print.getExpressions()).hasSize(4);
        assertThat(print.getExpressions().get(0)).isInstanceOf(LiteralNode.class);
        assertThat(print.getExpressions().get(1)).isInstanceOf(ArrayAccessNode.class);
        assertThat(print.getExpressions().get(2)).isInstanceOf(TupleMemberAccessNode.class);
        assertThat(print.getExpressions().get(3)).isInstanceOf(FunctionCallNode.class);
    }

    // ========== Combined Return and Print Tests ==========

    @Test
    void testReturnAndPrintInSequence() {
        String code = """
            print "Starting"
            return 42
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(PrintNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(ReturnNode.class);
    }

    @Test
    void testPrintAndReturnWithSemicolons() {
        String code = "print x; print y; return z";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(3);
        assertThat(program.getStatements().get(0)).isInstanceOf(PrintNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(PrintNode.class);
        assertThat(program.getStatements().get(2)).isInstanceOf(ReturnNode.class);
    }

    @Test
    void testReturnInFunction() {
        String code = "var f := func(x) is return x * x end";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        assertThat(decl.getVariables().get(0).getInitialValue()).isInstanceOf(FunctionLiteralNode.class);
        
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(func.getStatementBody()).hasSize(1);
        assertThat(func.getStatementBody().get(0)).isInstanceOf(ReturnNode.class);
    }

    @Test
    void testPrintInFunction() {
        String code = "var f := func(x) is print x end";
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        
        DeclarationNode decl = (DeclarationNode) program.getStatements().get(0);
        assertThat(decl.getVariables().get(0).getInitialValue()).isInstanceOf(FunctionLiteralNode.class);
        
        FunctionLiteralNode func = (FunctionLiteralNode) decl.getVariables().get(0).getInitialValue();
        assertThat(func.getStatementBody()).hasSize(1);
        assertThat(func.getStatementBody().get(0)).isInstanceOf(PrintNode.class);
    }

    @Test
    void testReturnInControlFlow() {
        String code = """
            if x > 0 then
                return 1
            else
                return -1
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(IfNode.class);
        
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        BlockNode elseBlock = (BlockNode) ifNode.getElseStatement();
        
        assertThat(thenBlock.getStatements().get(0)).isInstanceOf(ReturnNode.class);
        assertThat(elseBlock.getStatements().get(0)).isInstanceOf(ReturnNode.class);
    }

    @Test
    void testPrintInLoop() {
        String code = """
            for i in 1..10 loop
                print i
            end
            """;
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ForNode.class);
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements().get(0)).isInstanceOf(PrintNode.class);
    }
}
