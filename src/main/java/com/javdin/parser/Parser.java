package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.ast.ProgramNode;
import com.javdin.parser.generated.CupParser;

/**
 * Parser for the Javdin language.
 * Wraps the CUP-generated parser to provide a clean API.
 */
public class Parser {
    private final Lexer lexer;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    
    /**
     * Parse the input and return the AST root node.
     * @return The root ProgramNode of the parsed AST
     * @throws ParseException if there is a syntax error
     */
    public ProgramNode parse() throws ParseException {
        try {
            // Create an adapter to bridge our Lexer to CUP's Scanner interface
            LexerAdapter scanner = new LexerAdapter(lexer);
            
            // Create the CUP parser with the adapted lexer
            CupParser cupParser = new CupParser(scanner);
            
            // Parse and extract the result
            java_cup.runtime.Symbol result = cupParser.parse();
            
            // The semantic value of the parse result is the ProgramNode
            return (ProgramNode) result.value;
            
        } catch (Exception e) {
            // Wrap any parsing exceptions in our ParseException
            String message = e.getMessage() != null ? e.getMessage() : "Syntax error";
            throw new ParseException(message, 0, 0, e);
        }
    }
}
