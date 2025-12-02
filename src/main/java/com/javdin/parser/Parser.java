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
        // Create an adapter to bridge our Lexer to CUP's Scanner interface
        LexerAdapter scanner = new LexerAdapter(lexer);
        java.io.PrintStream oldErr = System.err;
        java.io.ByteArrayOutputStream errBuf = new java.io.ByteArrayOutputStream();
        try {
            // Create the CUP parser with the adapted lexer
            CupParser cupParser = new CupParser(scanner);

            // CUP sometimes prints diagnostics directly to System.err when it
            // fails to recover. Capture that output temporarily so we can
            // present a concise, controlled error message instead of raw parser
            // dumps.
            System.setErr(new java.io.PrintStream(errBuf));
            java_cup.runtime.Symbol result;
            try {
                // Parse and extract the result
                result = cupParser.parse();
            } finally {
                // Always restore System.err
                System.setErr(oldErr);
            }

            // The semantic value of the parse result is the ProgramNode
            return (ProgramNode) result.value;

        } catch (Exception e) {
            // If the root cause is a lexical error, rethrow it so the
            // main entrypoint can present the lexical message directly.
            if (e instanceof com.javdin.lexer.LexicalException) {
                throw (com.javdin.lexer.LexicalException) e;
            }
            if (e.getCause() instanceof com.javdin.lexer.LexicalException) {
                throw (com.javdin.lexer.LexicalException) e.getCause();
            }
            // If the last token was a type-indicator keyword used where an
            // identifier was expected, produce a clearer ParseException.
            try {
                com.javdin.lexer.Token tok = scanner.getCurrentToken();
                if (tok != null) {
                    switch (tok.type()) {
                        case INT_TYPE, REAL_TYPE, BOOL_TYPE, STRING_TYPE,
                             ARRAY_TYPE, TUPLE_TYPE, FUNC_TYPE, NONE_TYPE -> {
                            String message = String.format(
                                "Reserved word '%s' cannot be used as an identifier",
                                tok.value()
                            );
                            throw new ParseException(message, tok.line(), tok.column(), e);
                        }
                        default -> {
                            // Fall through to generic message below
                        }
                    }
                }
            } catch (Exception ignore) {
                // If any inspection fails, ignore and continue to generic message
            }

            // If CUP printed a diagnostic, use its first non-empty line as
            // the parse message (keeps the output concise but informative).
            try {
                String printed = errBuf.toString();
                if (!printed.isBlank()) {
                    String[] lines = printed.split("\r?\n");
                    for (String l : lines) {
                        if (!l.isBlank()) {
                            throw new ParseException(l.trim(), 0, 0, e);
                        }
                    }
                }
            } catch (Exception ignore) {
                // ignore and fall through to generic message
            }

            String message = e.getMessage() != null ? e.getMessage() : "Syntax error";
            throw new ParseException(message, 0, 0, e);
        }
    }
}
