package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for control flow statements (Milestone 7).
 */
class ControlFlowTest {
    
    // ========== If Statement Tests ==========
    
    @Test
    void testSimpleIfThenEnd() {
        Lexer lexer = new Lexer("if x then print 1 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(IfNode.class);
        
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        assertThat(ifNode.getCondition()).isInstanceOf(ReferenceNode.class);
        assertThat(ifNode.getThenStatement()).isInstanceOf(BlockNode.class);
        assertThat(ifNode.getElseStatement()).isNull();
        
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(1);
        assertThat(thenBlock.getStatements().get(0)).isInstanceOf(PrintNode.class);
    }
    
    @Test
    void testIfThenElseEnd() {
        Lexer lexer = new Lexer("if x == 5 then print 1 else print 2 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        
        assertThat(ifNode.getCondition()).isInstanceOf(BinaryOpNode.class);
        assertThat(ifNode.getThenStatement()).isInstanceOf(BlockNode.class);
        assertThat(ifNode.getElseStatement()).isInstanceOf(BlockNode.class);
        
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(1);
        
        BlockNode elseBlock = (BlockNode) ifNode.getElseStatement();
        assertThat(elseBlock.getStatements()).hasSize(1);
    }
    
    @Test
    void testShortIfArrow() {
        Lexer lexer = new Lexer("if x => print 1");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        
        assertThat(ifNode.getCondition()).isInstanceOf(ReferenceNode.class);
        assertThat(ifNode.getThenStatement()).isInstanceOf(BlockNode.class);
        assertThat(ifNode.getElseStatement()).isNull();
        
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(1);
    }
    
    @Test
    void testIfWithMultipleStatements() {
        Lexer lexer = new Lexer("if true then var x := 1; print x; x := 2 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        IfNode ifNode = (IfNode) program.getStatements().get(0);
        BlockNode thenBlock = (BlockNode) ifNode.getThenStatement();
        assertThat(thenBlock.getStatements()).hasSize(3);
        assertThat(thenBlock.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(thenBlock.getStatements().get(1)).isInstanceOf(PrintNode.class);
        assertThat(thenBlock.getStatements().get(2)).isInstanceOf(AssignmentNode.class);
    }
    
    @Test
    void testNestedIf() {
        Lexer lexer = new Lexer("if x then if y then print 1 end end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        IfNode outerIf = (IfNode) program.getStatements().get(0);
        BlockNode outerThen = (BlockNode) outerIf.getThenStatement();
        assertThat(outerThen.getStatements()).hasSize(1);
        assertThat(outerThen.getStatements().get(0)).isInstanceOf(IfNode.class);
    }
    
    // ========== While Statement Tests ==========
    
    @Test
    void testSimpleWhileLoop() {
        Lexer lexer = new Lexer("while x < 10 loop print x end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(WhileNode.class);
        
        WhileNode whileNode = (WhileNode) program.getStatements().get(0);
        assertThat(whileNode.getCondition()).isInstanceOf(BinaryOpNode.class);
        assertThat(whileNode.getBody()).isInstanceOf(BlockNode.class);
        
        BlockNode body = (BlockNode) whileNode.getBody();
        assertThat(body.getStatements()).hasSize(1);
        assertThat(body.getStatements().get(0)).isInstanceOf(PrintNode.class);
    }
    
    @Test
    void testWhileWithMultipleStatements() {
        Lexer lexer = new Lexer("while true loop var i := 0; i := i + 1; print i end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        WhileNode whileNode = (WhileNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) whileNode.getBody();
        assertThat(body.getStatements()).hasSize(3);
    }
    
    @Test
    void testNestedWhile() {
        Lexer lexer = new Lexer("while x loop while y loop print 1 end end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        WhileNode outerWhile = (WhileNode) program.getStatements().get(0);
        BlockNode outerBody = (BlockNode) outerWhile.getBody();
        assertThat(outerBody.getStatements()).hasSize(1);
        assertThat(outerBody.getStatements().get(0)).isInstanceOf(WhileNode.class);
    }
    
    // ========== For Statement Tests ==========
    
    @Test
    void testForInLoop() {
        Lexer lexer = new Lexer("for i in arr loop print i end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ForNode.class);
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        assertThat(forNode.getVariable()).isEqualTo("i");
        assertThat(forNode.getIterable()).isInstanceOf(ReferenceNode.class);
        assertThat(forNode.getRangeEnd()).isNull();
        assertThat(forNode.isIterableLoop()).isTrue();
        assertThat(forNode.isRangeLoop()).isFalse();
        assertThat(forNode.isInfiniteLoop()).isFalse();
        
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements()).hasSize(1);
    }
    
    @Test
    void testForInRangeLoop() {
        Lexer lexer = new Lexer("for i in 1..10 loop print i end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        assertThat(forNode.getVariable()).isEqualTo("i");
        assertThat(forNode.getIterable()).isInstanceOf(LiteralNode.class);
        assertThat(forNode.getRangeEnd()).isInstanceOf(LiteralNode.class);
        assertThat(forNode.isRangeLoop()).isTrue();
        assertThat(forNode.isIterableLoop()).isFalse();
        
        LiteralNode start = (LiteralNode) forNode.getIterable();
        assertThat(start.getValue()).isEqualTo(1);
        
        LiteralNode end = (LiteralNode) forNode.getRangeEnd();
        assertThat(end.getValue()).isEqualTo(10);
    }
    
    @Test
    void testForRangeWithoutVariable() {
        Lexer lexer = new Lexer("for 1..5 loop print 42 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        assertThat(forNode.getVariable()).isNull();
        assertThat(forNode.getIterable()).isInstanceOf(LiteralNode.class);
        assertThat(forNode.getRangeEnd()).isInstanceOf(LiteralNode.class);
        assertThat(forNode.isRangeLoop()).isTrue();
    }
    
    @Test
    void testForIterableWithoutVariable() {
        Lexer lexer = new Lexer("for arr loop print 42 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        assertThat(forNode.getVariable()).isNull();
        assertThat(forNode.getIterable()).isInstanceOf(ReferenceNode.class);
        assertThat(forNode.getRangeEnd()).isNull();
        assertThat(forNode.isIterableLoop()).isTrue();
    }
    
    @Test
    void testInfiniteLoop() {
        Lexer lexer = new Lexer("loop print 42 end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ForNode.class);
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        assertThat(forNode.getVariable()).isNull();
        assertThat(forNode.getIterable()).isNull();
        assertThat(forNode.getRangeEnd()).isNull();
        assertThat(forNode.isInfiniteLoop()).isTrue();
        assertThat(forNode.isRangeLoop()).isFalse();
        assertThat(forNode.isIterableLoop()).isFalse();
    }
    
    @Test
    void testNestedForLoops() {
        Lexer lexer = new Lexer("for i in 1..3 loop for j in 1..3 loop print i end end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode outerFor = (ForNode) program.getStatements().get(0);
        BlockNode outerBody = (BlockNode) outerFor.getBody();
        assertThat(outerBody.getStatements()).hasSize(1);
        assertThat(outerBody.getStatements().get(0)).isInstanceOf(ForNode.class);
    }
    
    // ========== Exit Statement Tests ==========
    
    @Test
    void testExitStatement() {
        Lexer lexer = new Lexer("exit");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(BreakNode.class);
    }
    
    @Test
    void testExitInLoop() {
        Lexer lexer = new Lexer("loop print 1; exit end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements()).hasSize(2);
        assertThat(body.getStatements().get(0)).isInstanceOf(PrintNode.class);
        assertThat(body.getStatements().get(1)).isInstanceOf(BreakNode.class);
    }
    
    @Test
    void testConditionalExit() {
        Lexer lexer = new Lexer("loop if x => exit end");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ForNode forNode = (ForNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements()).hasSize(1);
        
        IfNode ifNode = (IfNode) body.getStatements().get(0);
        BlockNode ifBody = (BlockNode) ifNode.getThenStatement();
        assertThat(ifBody.getStatements().get(0)).isInstanceOf(BreakNode.class);
    }
    
    // ========== Return Statement Tests ==========
    
    @Test
    void testReturnWithValue() {
        Lexer lexer = new Lexer("return 42");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(ReturnNode.class);
        
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        assertThat(returnNode.getValue()).isInstanceOf(LiteralNode.class);
        
        LiteralNode value = (LiteralNode) returnNode.getValue();
        assertThat(value.getValue()).isEqualTo(42);
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
    void testReturnWithExpression() {
        Lexer lexer = new Lexer("return x + 5");
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        ReturnNode returnNode = (ReturnNode) program.getStatements().get(0);
        assertThat(returnNode.getValue()).isInstanceOf(BinaryOpNode.class);
    }
    
    // ========== Integration Tests ==========
    
    @Test
    void testComplexControlFlow() {
        String code = """
            var i := 0
            while i < 10 loop
                if i == 5 then
                    exit
                end
                print i
                i := i + 1
            end
            """;
        
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(2);
        assertThat(program.getStatements().get(0)).isInstanceOf(DeclarationNode.class);
        assertThat(program.getStatements().get(1)).isInstanceOf(WhileNode.class);
    }
    
    @Test
    void testForLoopWithComplexBody() {
        String code = """
            for i in 1..10 loop
                var squared := i * i
                if squared > 50 then
                    print squared
                    return
                end
            end
            """;
        
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        ForNode forNode = (ForNode) program.getStatements().get(0);
        BlockNode body = (BlockNode) forNode.getBody();
        assertThat(body.getStatements()).hasSize(2);
    }
    
    @Test
    void testAllControlFlowTogether() {
        String code = """
            if x then
                while y loop
                    for i in 1..5 loop
                        if i == 3 => exit
                        print i
                    end
                end
            else
                return 0
            end
            """;
        
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        ProgramNode program = parser.parse();
        
        assertThat(program.getStatements()).hasSize(1);
        assertThat(program.getStatements().get(0)).isInstanceOf(IfNode.class);
    }
}
