package com.javdin.parser;

import com.javdin.ast.*;
import com.javdin.lexer.Lexer;

public class LiteralTest {
    public static void main(String[] args) {
        String[] testCases = {
            "var x := none;",
            "var arr := [1, 2, 3];",
            "var emptyArr := [];",
            "var tup := {a := 1, b := 2};",
            "var mixedTup := {1, name := \"test\", 3};",
            "var emptyTup := {};",
            "var func1 := func(x, y) is print x; end;",
            "var func2 := func(x) => x;"
        };

        for (String testCase : testCases) {
            System.out.println("Testing: " + testCase);
            try {
                Lexer lexer = new Lexer(testCase);
                Parser parser = new Parser(lexer);
                ProgramNode program = parser.parse();
                System.out.println("[OK] Parsed successfully!");
                System.out.println("  Statements: " + program.getStatements().size());
            } catch (Exception e) {
                System.out.println("[FAIL] Error: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
        }
    }
}
