package com.javdin.debug;

import com.javdin.lexer.Lexer;
import com.javdin.parser.Parser;
import com.javdin.ast.ProgramNode;

public class TestParse {
    public static void main(String[] args) {
        String code = "while true loop print 1; print 2 end";
        
        System.out.println("CODE: " + code);
        
        try {
            Lexer lexer = new Lexer(code);
            Parser parser = new Parser(lexer);
            ProgramNode program = parser.parse();
            System.out.println("Parse SUCCESS!");
            System.out.println("Statements: " + program.getStatements().size());
        } catch (Exception e) {
            System.out.println("Parse FAILED!");
            e.printStackTrace();
        }
    }
}
