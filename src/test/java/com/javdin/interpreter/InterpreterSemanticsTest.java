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
 * Additional semantic coverage for the Project D interpreter.
 * These tests focus on requirements spelled out in docs/Project D.md
 * such as loop/exit semantics, array and tuple behavior, and operator typing.
 */
class InterpreterSemanticsTest {

    @Test
    void loopExitTerminatesInfiniteLoop() {
        String program = String.join("\n",
            "var count := 0",
            "loop",
            "    count := count + 1",
            "    if count = 3 => exit",
            "end",
            "print count");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("3");
    }

    @Test
    void rangeLoopSupportsDescendingOrder() {
        String program = String.join("\n",
            "var total := 0",
            "for i in 3..1 loop",
            "    total := total + i",
            "end",
            "print total");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("6");
    }

    @Test
    void arraysAreSparseAndOneBased() {
        String program = String.join("\n",
            "var arr := [0]",
            "arr[1] := none",
            "arr[2] := 5",
            "print (arr[1] is none), arr[2]");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("true 5");
    }

    @Test
    void arrayIndexZeroProducesRuntimeError() {
        String program = String.join("\n",
            "var arr := [0]",
            "arr[0] := 99");
        List<ErrorHandler.Error> runtimeErrors = runExpectRuntimeErrors(program);
        assertThat(runtimeErrors).singleElement()
            .satisfies(error -> assertThat(error.getMessage()).contains("Array index out of bounds"));
    }

    @Test
    void tupleConcatenationAndIndexAccessWorkTogether() {
        String program = String.join("\n",
            "var t1 := {a := 1, 2}",
            "var t2 := {c := 3}",
            "var combined := t1 + t2",
            "print combined.a, combined.2, combined.c");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("1 2 3");
    }

    @Test
    void arrayConcatenationViaAdditionPreservesOrder() {
        String program = String.join("\n",
            "var left := [1,2]",
            "var right := [3]",
            "var combined := left + right",
            "print combined[1], combined[2], combined[3]");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("1 2 3");
    }

    @Test
    void stringAdditionPerformsConcatenation() {
        String program = "print \"foo\" + \"bar\"";
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("foobar");
    }

    @Test
    void invalidAdditionBetweenMismatchedTypesFails() {
        String program = "print 1 + \"two\"";
        List<ErrorHandler.Error> runtimeErrors = runExpectRuntimeErrors(program);
        assertThat(runtimeErrors).singleElement()
            .satisfies(error -> assertThat(error.getMessage()).contains("Unsupported operand types for '+'"));
    }

    @Test
    void subtractionMultiplicationAndDivisionFollowNumericRules() {
        String program = String.join("\n",
            "var sub1 := 10 - 3",
            "var sub2 := 5 - 2.5",
            "var sub3 := 5.0 - 2",
            "var mul1 := 2 * 3",
            "var mul2 := 2 * 0.5",
            "var mul3 := 0.25 * 4",
            "var div1 := 5 / 2",
            "var div2 := 5 / 2.0",
            "var div3 := 5.0 / 2",
            "print sub1, sub2, sub3, mul1, mul2, mul3, div1, div2, div3");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("7 2.5 3.0 6 1.0 1.0 2 2.5 2.5");
    }

    @Test
    void comparisonsSupportMixedNumericTypes() {
        String program = String.join("\n",
            "print (1 < 2), (2 <= 2), (3 > 4), (5 >= 5), (1 = 1.0), (2 /= 3)");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("true true false true true true");
    }

    @Test
    void logicalOperatorsRequireBooleans() {
        String program = String.join("\n",
            "print (true and false), (true or false), (true xor false)");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("false true true");
    }

    @Test
    void logicalOperatorsRejectNonBooleanOperands() {
        String program = "print (1 and true)";
        List<ErrorHandler.Error> runtimeErrors = runExpectRuntimeErrors(program);
        assertThat(runtimeErrors).singleElement()
            .satisfies(error -> assertThat(error.getMessage()).contains("Expected boolean value"));
    }

    @Test
    void implicitNumericConversionAllowsMixedMath() {
        String program = String.join("\n",
            "var total := 0",
            "total := total + 1.5",
            "print total");
        String output = runExpectSuccess(program);
        assertThat(output).isEqualTo("1.5");
    }

    private String runExpectSuccess(String source) {
        ExecutionResult result = execute(source);
        assertThat(result.finalErrors().size())
            .as(() -> "Runtime produced errors: " + formatErrors(result))
            .isEqualTo(result.errorsAfterOptimizer());
        return result.output();
    }

    private List<ErrorHandler.Error> runExpectRuntimeErrors(String source) {
        ExecutionResult result = execute(source);
        assertThat(result.finalErrors().size())
            .as(() -> "Expected runtime errors but optimizer produced fewer")
            .isGreaterThan(result.errorsAfterOptimizer());
        return result.finalErrors().subList(result.errorsAfterOptimizer(), result.finalErrors().size());
    }

    private ExecutionResult execute(String source) {
        ErrorHandler errorHandler = new ErrorHandler();
        ProgramNode ast = parse(source);

        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
        analyzer.analyze(ast);
        assertThat(errorHandler.hasErrors()).as("Semantic analysis produced errors").isFalse();
        int errorsAfterSemantics = errorHandler.getErrors().size();

        Optimizer optimizer = new Optimizer(errorHandler);
        ProgramNode optimized = optimizer.optimize(ast);
        int errorsAfterOptimizer = errorHandler.getErrors().size();
        assertThat(errorsAfterSemantics).as("Semantic error count mismatch").isZero();

        Interpreter interpreter = new Interpreter(errorHandler);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            interpreter.interpret(optimized);
        } finally {
            System.setOut(originalOut);
        }
        return new ExecutionResult(outputStream.toString().strip(), errorHandler.getErrors(), errorsAfterOptimizer);
    }

    private ProgramNode parse(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        return parser.parse();
    }

    private String formatErrors(ExecutionResult result) {
        List<ErrorHandler.Error> errors = result.finalErrors();
        if (errors.size() <= result.errorsAfterOptimizer()) {
            return "<none>";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = result.errorsAfterOptimizer(); i < errors.size(); i++) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(errors.get(i));
        }
        return builder.toString();
    }

    private record ExecutionResult(String output, List<ErrorHandler.Error> finalErrors, int errorsAfterOptimizer) { }
}
