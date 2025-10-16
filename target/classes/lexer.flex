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
"then"      { return token(TokenType.THEN); }
"else"      { return token(TokenType.ELSE); }
"end"       { return token(TokenType.END); }
"while"     { return token(TokenType.WHILE); }
"for"       { return token(TokenType.FOR); }
"in"        { return token(TokenType.IN); }
"loop"      { return token(TokenType.LOOP); }
"exit"      { return token(TokenType.EXIT); }
"function"  { return token(TokenType.FUNCTION); }
"func"      { return token(TokenType.FUNC); }
"return"    { return token(TokenType.RETURN); }
"print"     { return token(TokenType.PRINT); }
"input"     { return token(TokenType.INPUT); }
"true"      { return token(TokenType.TRUE); }
"false"     { return token(TokenType.FALSE); }
"lambda"    { return token(TokenType.LAMBDA); }
"break"     { return token(TokenType.BREAK); }
"continue"  { return token(TokenType.CONTINUE); }
"is"        { return token(TokenType.IS); }
"and"       { return token(TokenType.AND); }
"or"        { return token(TokenType.OR); }
"xor"       { return token(TokenType.XOR); }
"not"       { return token(TokenType.NOT); }

/* Type indicators (must come before keywords to avoid conflicts) */
"int"       { return token(TokenType.INT_TYPE); }
"real"      { return token(TokenType.REAL_TYPE); }
"bool"      { return token(TokenType.BOOL_TYPE); }
"string"    { return token(TokenType.STRING_TYPE); }
"none"      { return token(TokenType.NONE_TYPE); }

/* Operators */
"+="        { return token(TokenType.PLUS_ASSIGN); }
"-="        { return token(TokenType.MINUS_ASSIGN); }
"*="        { return token(TokenType.MULTIPLY_ASSIGN); }
"/="        { return token(TokenType.DIVIDE_ASSIGN); }
"%="        { return token(TokenType.MODULO_ASSIGN); }
"+"         { return token(TokenType.PLUS); }
"-"         { return token(TokenType.MINUS); }
"*"         { return token(TokenType.MULTIPLY); }
"/"         { return token(TokenType.DIVIDE); }
"%"         { return token(TokenType.MODULO); }
":="        { return token(TokenType.ASSIGN_OP); }
"=="        { return token(TokenType.EQUAL); }
"="         { return token(TokenType.EQUAL); }
"!="        { return token(TokenType.NOT_EQUAL); }
".."        { return token(TokenType.RANGE); }
"<"         { return token(TokenType.LESS_THAN); }
"<="        { return token(TokenType.LESS_EQUAL); }
">"         { return token(TokenType.GREATER_THAN); }
">="        { return token(TokenType.GREATER_EQUAL); }
"=>"        { return token(TokenType.SHORT_IF); }
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
