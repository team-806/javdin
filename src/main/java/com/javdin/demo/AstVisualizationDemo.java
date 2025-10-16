package com.javdin.demo;

import com.javdin.parser.Parser;
import com.javdin.lexer.Lexer;
import com.javdin.ast.ProgramNode;
import com.javdin.visualization.AstXmlSerializer;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.nio.file.*;

/**
 * AST Visualization Demo
 * 
 * Generates XML AST and HTML visualization from Javdin source code.
 * Uses custom AST serialization and XSLT transformations.
 * 
 * Usage:
 *   mvn exec:java -Dexec.mainClass="com.javdin.demo.AstVisualizationDemo" \
 *                 -Dexec.args="presentation-example-1.d"
 * 
 * Or after packaging:
 *   java -cp target/javdin-1.0.0.jar com.javdin.demo.AstVisualizationDemo program.d
 */
public class AstVisualizationDemo {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: AstVisualizationDemo <source-file>");
            System.err.println();
            System.err.println("Example:");
            System.err.println("  mvn exec:java -Dexec.mainClass=\"com.javdin.demo.AstVisualizationDemo\" \\");
            System.err.println("                -Dexec.args=\"presentation-example-1.d\"");
            System.exit(1);
        }
        
        String sourceFile = args[0];
        
        try {
            System.out.println("==============================================================");
            System.out.println("         Javdin AST Visualization Generator                  ");
            System.out.println("==============================================================");
            System.out.println();
            
            // Read source file
            String sourceCode = Files.readString(Paths.get(sourceFile));
            System.out.println("Source file: " + sourceFile);
            System.out.println("Size: " + sourceCode.length() + " bytes");
            System.out.println();
            
            // Parse source code
            System.out.println("Parsing source code...");
            Lexer lexer = new Lexer(sourceCode);
            Parser parser = new Parser(lexer);
            ProgramNode ast = parser.parse();
            System.out.println("  [OK] Parsing complete");
            System.out.println();
            
            // Serialize AST to XML
            System.out.println("Serializing AST to XML...");
            AstXmlSerializer serializer = new AstXmlSerializer();
            String astXml = serializer.serialize(ast);
            
            String astFile = "ast.xml";
            Files.writeString(Paths.get(astFile), astXml);
            System.out.println("  [OK] AST XML saved to: " + astFile);
            System.out.println();
            
            // Transform to HTML visualization
            System.out.println("Generating HTML visualization...");
            String htmlFile = "ast-visualization.html";
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            
            // Load XSLT from resources
            InputStream xsltStream = AstVisualizationDemo.class
                .getResourceAsStream("/xslt/ast-to-html.xsl");
            
            if (xsltStream == null) {
                System.err.println("ERROR: Could not find ast-to-html.xsl in resources");
                System.err.println("Make sure to run: mvn compile");
                System.exit(1);
            }
            
            Transformer htmlTransformer = transformerFactory
                .newTransformer(new StreamSource(xsltStream));
            
            htmlTransformer.transform(
                new StreamSource(new File(astFile)),
                new StreamResult(new File(htmlFile))
            );
            
            System.out.println("  [OK] HTML visualization saved to: " + htmlFile);
            System.out.println();
            
            // Print summary
            System.out.println("==============================================================");
            System.out.println("                      SUCCESS                                 ");
            System.out.println("==============================================================");
            System.out.println();
            System.out.println("Generated files:");
            System.out.println("  1. " + astFile + " - AST in XML format");
            System.out.println("  2. " + htmlFile + " - Visual HTML tree");
            System.out.println();
            System.out.println("To view the visualization:");
            System.out.println("  Open " + htmlFile + " in your web browser");
            System.out.println();
            
            // Try to open in default browser
            File htmlFileObj = new File(htmlFile);
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    java.awt.Desktop.getDesktop().browse(htmlFileObj.toURI());
                    System.out.println("Opening visualization in browser...");
                } catch (Exception e) {
                    // Silently fail if can't open browser
                }
            }
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("==============================================================");
            System.err.println("                       ERROR                                  ");
            System.err.println("==============================================================");
            System.err.println();
            System.err.println("Error type: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            System.err.println();
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
