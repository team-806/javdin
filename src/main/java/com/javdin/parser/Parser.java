package com.javdin.parser;

import com.javdin.lexer.Lexer;
import com.javdin.lexer.Token;
import com.javdin.lexer.TokenType;
import com.javdin.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the Javdin language.
 * Implements a recursive descent parser.
 * In the future, this will be replaced by CUP-generated parser.
 */
public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }
    
    /**
     * Parse the input and return the AST root node.
     */
    public ProgramNode parse() {
        List<StatementNode> statements = new ArrayList<>();
        
        while (currentToken.type() != TokenType.EOF) {
            if (currentToken.type() == TokenType.NEWLINE) {
                advance();
                continue;
            }
            
            if (currentToken.type() == TokenType.SEMICOLON) {
                advance(); // Skip semicolons
                continue;
            }
            
            StatementNode stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
                
                // Consume optional semicolon after statement
                if (currentToken.type() == TokenType.SEMICOLON) {
                    advance();
                }
            }
        }
        
        return new ProgramNode(statements, 1, 1);
    }
    
    private StatementNode parseStatement() {
        return switch (currentToken.type()) {
            case VAR -> parseDeclaration();
            case PRINT -> parsePrint();
            // Add more statement types here
            default -> parseExpressionStatement();
        };
    }
    
    private DeclarationNode parseDeclaration() {
        int line = currentToken.line();
        int column = currentToken.column();
        
        expect(TokenType.VAR);
        
        if (currentToken.type() != TokenType.IDENTIFIER) {
            throw new ParseException("Expected identifier after 'var'", currentToken.line(), currentToken.column());
        }
        
        String varName = currentToken.value();
        advance();
        
        ExpressionNode initialValue = null;
        if (currentToken.type() == TokenType.ASSIGN) {
            advance();
            initialValue = parseExpression();
        }
        
        return new DeclarationNode(varName, initialValue, line, column);
    }
    
    private PrintNode parsePrint() {
        int line = currentToken.line();
        int column = currentToken.column();
        
        expect(TokenType.PRINT);
        
        // Print statements can optionally have an expression
        ExpressionNode expression = null;
        if (currentToken.type() != TokenType.SEMICOLON && 
            currentToken.type() != TokenType.NEWLINE && 
            currentToken.type() != TokenType.EOF) {
            expression = parseExpression();
        }
        
        return new PrintNode(line, column, expression);
    }
    
    private ExpressionStatementNode parseExpressionStatement() {
        int line = currentToken.line();
        int column = currentToken.column();
        
        // Parse the expression
        ExpressionNode expression = parseExpression();
        
        return new ExpressionStatementNode(line, column, expression);
    }
    
    private ExpressionNode parseExpression() {
        return parseLiteral();
    }
    
    private LiteralNode parseLiteral() {
        Token token = currentToken;
        
        switch (token.type()) {
            case INTEGER -> {
                advance();
                return new LiteralNode(Integer.parseInt(token.value()), 
                                     LiteralNode.LiteralType.INTEGER, 
                                     token.line(), token.column());
            }
            case REAL -> {
                advance();
                return new LiteralNode(Double.parseDouble(token.value()), 
                                     LiteralNode.LiteralType.REAL, 
                                     token.line(), token.column());
            }
            case TRUE -> {
                advance();
                return new LiteralNode(true, LiteralNode.LiteralType.BOOLEAN, 
                                     token.line(), token.column());
            }
            case FALSE -> {
                advance();
                return new LiteralNode(false, LiteralNode.LiteralType.BOOLEAN, 
                                     token.line(), token.column());
            }
            case STRING -> {
                advance();
                return new LiteralNode(token.value(), LiteralNode.LiteralType.STRING, 
                                     token.line(), token.column());
            }
            default -> throw new ParseException("Expected literal", token.line(), token.column());
        }
    }
    
    private void expect(TokenType expected) {
        if (currentToken.type() != expected) {
            throw new ParseException("Expected " + expected + " but got " + currentToken.type(), 
                                   currentToken.line(), currentToken.column());
        }
        advance();
    }
    
    private void advance() {
        currentToken = lexer.nextToken();
    }
}
