package com.javdin.main;

import com.javdin.lexer.Lexer;
import com.javdin.parser.Parser;
import com.javdin.semantics.SemanticAnalyzer;
import com.javdin.interpreter.Interpreter;
import com.javdin.utils.ErrorHandler;
import com.javdin.utils.IoUtils;
import com.javdin.ast.ProgramNode;

import java.io.IOException;

/**
 * Main entry point for the Javdin interpreter.
 * 
 * Usage: java -jar javdin.jar input.d
 */
public class Main {
    
    public static void main(String[] args) {
        int exitCode = runInterpreter(args);
        System.exit(exitCode);
    }
    
    /**
     * Run the interpreter and return an exit code.
     * This method is testable as it doesn't call System.exit().
     * 
     * @param args Command line arguments
     * @return Exit code (0 for success, 1 for error)
     */
    public static int runInterpreter(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar javdin.jar <source-file>");
            return 1;
        }
        
        String sourceFile = args[0];
        ErrorHandler errorHandler = new ErrorHandler();
        
        try {
            // Read source code
            String sourceCode = IoUtils.readFile(sourceFile);
            
            // Lexical analysis
            Lexer lexer = new Lexer(sourceCode);
            
            // Syntax analysis
            Parser parser = new Parser(lexer);
            ProgramNode ast = parser.parse();
            
            if (errorHandler.hasErrors()) {
                errorHandler.printErrors();
                return 1;
            }
            
            // Semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(errorHandler);
            semanticAnalyzer.analyze(ast);
            
            if (errorHandler.hasErrors()) {
                errorHandler.printErrors();
                return 1;
            }
            
            // Interpretation
            Interpreter interpreter = new Interpreter(errorHandler);
            interpreter.interpret(ast);
            
            return 0;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}
