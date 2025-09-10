/* 
 * JFlex lexer specification for Javdin language
 * This will be used to generate the lexer
 */

package com.javdin.lexer;

import com.javdin.lexer.Token;
import com.javdin.lexer.TokenType;

%%

%class JFlexLexer
%public
%line
%column
%unicode
%type Token

%{
    private Token token(TokenType type) {
        return new Token(type, yytext(), yyline + 1, yycolumn + 1);
    }
    
    private Token token(TokenType type, String value) {
        return new Token(type, value, yyline + 1, yycolumn + 1);
    }
%}

/* Regular expressions */
DIGIT = [0-9]
LETTER = [a-zA-Z]
IDENTIFIER = {LETTER}({LETTER}|{DIGIT}|_)*
INTEGER = {DIGIT}+
REAL = {DIGIT}+\.{DIGIT}+
STRING = \"([^\\\"]|\\.)*\"
WHITESPACE = [ \t\r]+
NEWLINE = \n
COMMENT = "//"[^\n]*

%%

/* Keywords */
"var"       { return token(TokenType.VAR); }
"if"        { return token(TokenType.IF); }
"else"      { return token(TokenType.ELSE); }
"while"     { return token(TokenType.WHILE); }
"for"       { return token(TokenType.FOR); }
"function"  { return token(TokenType.FUNCTION); }
"return"    { return token(TokenType.RETURN); }
"print"     { return token(TokenType.PRINT); }
"input"     { return token(TokenType.INPUT); }
"true"      { return token(TokenType.TRUE); }
"false"     { return token(TokenType.FALSE); }
"lambda"    { return token(TokenType.LAMBDA); }
"break"     { return token(TokenType.BREAK); }
"continue"  { return token(TokenType.CONTINUE); }
"and"       { return token(TokenType.AND); }
"or"        { return token(TokenType.OR); }
"not"       { return token(TokenType.NOT); }

/* Operators */
"+"         { return token(TokenType.PLUS); }
"-"         { return token(TokenType.MINUS); }
"*"         { return token(TokenType.MULTIPLY); }
"/"         { return token(TokenType.DIVIDE); }
"%"         { return token(TokenType.MODULO); }
"="         { return token(TokenType.ASSIGN); }
"=="        { return token(TokenType.EQUAL); }
"!="        { return token(TokenType.NOT_EQUAL); }
"<"         { return token(TokenType.LESS_THAN); }
"<="        { return token(TokenType.LESS_EQUAL); }
">"         { return token(TokenType.GREATER_THAN); }
">="        { return token(TokenType.GREATER_EQUAL); }
"->"        { return token(TokenType.ARROW); }

/* Delimiters */
"("         { return token(TokenType.LEFT_PAREN); }
")"         { return token(TokenType.RIGHT_PAREN); }
"{"         { return token(TokenType.LEFT_BRACE); }
"}"         { return token(TokenType.RIGHT_BRACE); }
"["         { return token(TokenType.LEFT_BRACKET); }
"]"         { return token(TokenType.RIGHT_BRACKET); }
";"         { return token(TokenType.SEMICOLON); }
","         { return token(TokenType.COMMA); }
"."         { return token(TokenType.DOT); }
":"         { return token(TokenType.COLON); }

/* Literals */
{INTEGER}   { return token(TokenType.INTEGER, yytext()); }
{REAL}      { return token(TokenType.REAL, yytext()); }
{STRING}    { return token(TokenType.STRING, yytext().substring(1, yytext().length()-1)); }

/* Identifiers */
{IDENTIFIER} { return token(TokenType.IDENTIFIER, yytext()); }

/* Whitespace and comments */
{WHITESPACE} { /* ignore */ }
{COMMENT}    { /* ignore */ }
{NEWLINE}    { return token(TokenType.NEWLINE); }

/* End of file */
<<EOF>>     { return token(TokenType.EOF); }

/* Error fallback */
.           { return token(TokenType.UNKNOWN, yytext()); }
