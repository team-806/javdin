package com.javdin.interpreter;

import com.javdin.ast.ProgramNode;
import com.javdin.lexer.Lexer;
import com.javdin.parser.Parser;
import com.javdin.semantics.Optimizer;
import com.javdin.semantics.SemanticAnalyzer;
import com.javdin.utils.ErrorHandler;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * High-level execution tests for the Javdin interpreter.
 * These tests parse full programs and execute them end-to-end
 * through semantic analysis, optimization, and interpretation.
 */
class InterpreterExecutionTest {
    @Test
    void executesArithmeticAndPrintStatements() {
        String program = String.join("\n",
            "var label := \"result\"",
            "var x := 40 + 2",
            "print label, x");
        String output = runProgram(program);
        assertThat(output).isEqualTo("result 42");
    }
    
    @Test
    void supportsWhileLoopsWithSelectiveAccumulation() {
        String program = String.join("\n",
            "var i := 0",
            "var sum := 0",
            "while i < 4 loop",
            "    i := i + 1",
            "    if i = 3 then",
            "        sum := sum",
            "    else",
            "        sum := sum + i",
            "    end",
            "end",
            "print sum");
        String output = runProgram(program);
        assertThat(output).isEqualTo("7");
    }
    
    @Test
    void iteratesOverArraysAndRanges() {
        String program = String.join("\n",
            "var total := 0",
            "var arr := [1,2,3,4]",
            "for value in arr loop",
            "    total := total + value",
            "end",
            "var rangeSum := 0",
            "for idx in 1..4 loop",
            "    rangeSum := rangeSum + idx",
            "end",
            "print total, rangeSum");
        String output = runProgram(program);
        assertThat(output).isEqualTo("10 10");
    }
    
    @Test
    void evaluatesFunctionsClosuresAndRecursion() {
        String program = String.join("\n",
            "var makeAdder := func(x) => func(y) => x + y",
            "var add10 := makeAdder(10)",
            "var result := add10(5)",
            "var factorial := func(n) is",
            "    if n <= 1 then",
            "        return 1",
            "    else",
            "        return n * factorial(n - 1)",
            "    end",
            "end",
            "print result, factorial(5)");
        String output = runProgram(program);
        assertThat(output).isEqualTo("15 120");
    }
    
    @Test
    void handlesArraysTuplesAndTypeChecks() {
        String program = String.join("\n",
            "var arr := [0]",
            "arr[1] := 10",
            "arr[3] := 30",
            "var tup := {a := 1, 2, c := 3}",
            "print arr[1], tup.a, tup.2, (arr is []), (tup is {}), arr[2]");
        String output = runProgram(program);
        assertThat(output).isEqualTo("10 1 2 true true none");
    }
    
    @Test
    void detectsNoneValuesWithTypeIndicator() {
        String program = String.join("\n",
            "var missing",
            "var present := 42",
            "print (missing is none), (present is none)",
            "missing := 100",
            "present := none",
            "print (missing is none), (present is none)");
        String output = runProgram(program);
        assertThat(output).isEqualTo("true false\nfalse true");
    }
    
    private String runProgram(String source) {
        ErrorHandler errorHandler = new ErrorHandler();
        ProgramNode ast = parse(source);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
        analyzer.analyze(ast);
        assertThat(errorHandler.hasErrors()).as("Semantic analysis produced errors").isFalse();
        int errorsAfterSemantics = errorHandler.getErrors().size();
        Optimizer optimizer = new Optimizer(errorHandler);
        ProgramNode optimized = optimizer.optimize(ast);
        int errorsAfterOptimizer = errorHandler.getErrors().size();
        Interpreter interpreter = new Interpreter(errorHandler);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            interpreter.interpret(optimized);
        } finally {
            System.setOut(originalOut);
        }
        assertThat(errorsAfterSemantics).as("Semantic error count mismatch").isZero();
        List<ErrorHandler.Error> finalErrors = errorHandler.getErrors();
        int finalErrorCount = finalErrors.size();
        if (finalErrorCount != errorsAfterOptimizer) {
            StringBuilder details = new StringBuilder();
            for (int i = errorsAfterOptimizer; i < finalErrorCount; i++) {
                if (details.length() > 0) {
                    details.append(" | ");
                }
                details.append(finalErrors.get(i));
            }
            assertThat(finalErrorCount)
                .as("Runtime produced errors: " + details)
                .isEqualTo(errorsAfterOptimizer);
        } else {
            assertThat(finalErrorCount)
                .as("Runtime produced errors")
                .isEqualTo(errorsAfterOptimizer);
        }
        return outputStream.toString().strip();
    }
    
    private ProgramNode parse(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        return parser.parse();
    }
}
