package com.javdin.demo;

import com.javdin.lexer.Lexer;
import com.javdin.parser.Parser;
import com.javdin.semantics.SemanticAnalyzer;
import com.javdin.semantics.Optimizer;
import com.javdin.utils.ErrorHandler;
import com.javdin.ast.ProgramNode;
import com.javdin.visualization.AstXmlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Demo application to demonstrate semantic analysis and optimization.
 * 
 * Usage:
 *   mvn exec:java -Dexec.mainClass="com.javdin.demo.SemanticAnalysisDemo" \
 *                 -Dexec.args="path/to/file.d [--optimize]"
 * 
 * Examples:
 *   # Show semantic errors
 *   mvn exec:java -Dexec.mainClass="com.javdin.demo.SemanticAnalysisDemo" \
 *                 -Dexec.args="docs/demos/semantic-check-1-return-outside-function-error.d"
 *   
 *   # Show semantic analysis passing
 *   mvn exec:java -Dexec.mainClass="com.javdin.demo.SemanticAnalysisDemo" \
 *                 -Dexec.args="docs/demos/semantic-check-1-return-outside-function-fixed.d"
 *   
 *   # Show optimization with AST comparison
 *   mvn exec:java -Dexec.mainClass="com.javdin.demo.SemanticAnalysisDemo" \
 *                 -Dexec.args="docs/demos/optimization-1-constant-folding.d --optimize"
 */
public class SemanticAnalysisDemo {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }
        
        String filename = args[0];
        boolean showOptimization = args.length > 1 && args[1].equals("--optimize");
        
        try {
            System.out.println("===============================================================");
            System.out.println("  Javdin Semantic Analysis Demo");
            System.out.println("===============================================================");
            System.out.println();
            
            // Read source file
            String source = Files.readString(Paths.get(filename));
            System.out.println("Source File: " + filename);
            System.out.println("---------------------------------------------------------------");
            System.out.println(source);
            System.out.println("---------------------------------------------------------------");
            System.out.println();
            
            // Parse the source code
            System.out.println("Parsing...");
            Lexer lexer = new Lexer(source);
            Parser parser = new Parser(lexer);
            ProgramNode ast = parser.parse();
            
            if (ast == null) {
                System.err.println("ERROR: Parsing failed!");
                System.exit(1);
            }
            System.out.println("OK: Parsing successful");
            System.out.println();
            
            // Semantic Analysis
            System.out.println("Running Semantic Analysis...");
            System.out.println("---------------------------------------------------------------");
            ErrorHandler errorHandler = new ErrorHandler();
            SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
            analyzer.analyze(ast);
            
            if (errorHandler.hasErrors()) {
                System.out.println("ERROR: Semantic Errors Found:");
                System.out.println();
                for (ErrorHandler.Error error : errorHandler.getErrors()) {
                    System.out.println("  [!] Line " + error.getLine() + ", Column " + error.getColumn() + ":");
                    System.out.println("      " + error.getMessage());
                    System.out.println();
                }
                System.out.println("---------------------------------------------------------------");
                System.exit(1);
            } else {
                System.out.println("OK: No semantic errors detected");
                System.out.println("---------------------------------------------------------------");
                System.out.println();
            }
            
            // Optimization (if requested)
            if (showOptimization) {
                System.out.println("Running Optimizer...");
                System.out.println("---------------------------------------------------------------");
                
                // Generate XML for original AST
                AstXmlSerializer originalSerializer = new AstXmlSerializer();
                String originalXml = originalSerializer.serialize(ast);
                Files.writeString(Paths.get("ast-before-optimization.xml"), originalXml);
                System.out.println("Original AST saved to: ast-before-optimization.xml");
                
                // Apply optimizations
                ErrorHandler optimizerErrors = new ErrorHandler();
                Optimizer optimizer = new Optimizer(optimizerErrors);
                ProgramNode optimizedAst = optimizer.optimize(ast);
                
                // Generate XML for optimized AST
                AstXmlSerializer optimizedSerializer = new AstXmlSerializer();
                String optimizedXml = optimizedSerializer.serialize(optimizedAst);
                Files.writeString(Paths.get("ast-after-optimization.xml"), optimizedXml);
                System.out.println("Optimized AST saved to: ast-after-optimization.xml");
                System.out.println();
                
                // Show optimization warnings/info
                if (optimizerErrors.hasErrors()) {
                    System.out.println("Optimization Notes:");
                    System.out.println();
                    for (ErrorHandler.Error error : optimizerErrors.getErrors()) {
                        System.out.println("  [i] Line " + error.getLine() + ", Column " + error.getColumn() + ":");
                        System.out.println("      " + error.getMessage());
                        System.out.println();
                    }
                } else {
                    System.out.println("INFO: No optimizations applied");
                    System.out.println();
                }
                
                System.out.println("---------------------------------------------------------------");
                System.out.println();
                System.out.println("TIP: Compare the AST files to see optimizations:");
                System.out.println("   diff ast-before-optimization.xml ast-after-optimization.xml");
                System.out.println();
                System.out.println("   Or use xmldiff if installed:");
                System.out.println("   xmldiff ast-before-optimization.xml ast-after-optimization.xml");
            }
            
            System.out.println("===============================================================");
            System.out.println("OK: Analysis Complete!");
            System.out.println("===============================================================");
            
        } catch (IOException e) {
            System.err.println("ERROR: Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printUsage() {
        System.out.println("Usage: mvn exec:java -Dexec.mainClass=\"com.javdin.demo.SemanticAnalysisDemo\" \\");
        System.out.println("                     -Dexec.args=\"<file.d> [--optimize]\"");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --optimize    Show optimizations and generate before/after AST XML files");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # Check for semantic errors");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javdin.demo.SemanticAnalysisDemo\" \\");
        System.out.println("                -Dexec.args=\"docs/demos/semantic-check-1-return-outside-function-error.d\"");
        System.out.println();
        System.out.println("  # Show optimizations");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javdin.demo.SemanticAnalysisDemo\" \\");
        System.out.println("                -Dexec.args=\"docs/demos/optimization-1-constant-folding.d --optimize\"");
    }
}
