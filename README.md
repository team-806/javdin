<img width="200" height="200" alt="logo" src="assets/javdin-logo-400-400.png" />

---
# Overview
**Jav**a **d**ynamic **in**terpreter
with a Bison-based parser, for Dynamic academic language similar to JavaScript

This project is part of the [F25] Compiler Construction course at [Innopolis University](https://innopolis.university/)

**Javdin** processes source code written in [Project D](docs/Project%20D.pdf), a dynamically-typed academic language with JavaScript-like syntax, and executes it on the Java Virtual Machine.

Key characteristics:

- Implementation language: Java 17
- Target language: Project D (dynamic typing, 1-based arrays, tuples, closures)
- Parser generation: [CUP (Construction of Useful Parsers)](https://www2.cs.tum.edu/projects/cup/) for LALR(1) parsing
- Build system: Maven 3.6
- License: GNU GPL v3

---

# Architecture

Javdin contains these six main components:
1) A hand-written [lexer](src/main/java/com/javdin/lexer/Lexer.java). Takes source code in .d files and outputs stream of tokens with position information.
2) [LexerAdapter](src/main/java/com/javdin/parser/LexerAdapter.java). Bridge between lexer and CUP parser. Converts Token objects to CUP Symbol objects. Maps token types to CUP terminal symbols.
3) CUP-generated LR [parser](src/main/java/com/javdin/parser/Parser.java). We use CUP as mav
Takes token stream from LexerAdapter as an input. Uses parser.cup as a grammar rules file (417 lines). Returns the Abstract Syntax Tree (AST).
1) [Custom AST nodes](src/main/java/com/javdin/ast) (for creating custom ast tree xml visualization, see [AstXmlSerializer](src/main/java/com/javdin/visualization/AstXmlSerializer.java), can be used with [visualize-ast.sh](visualize-ast.sh)).
Node hierarchy: 23 specialized classes extending StatementNode or ExpressionNode
Each production rule creates specific AST node type. All nodes are immutable with final fields. Every node stores source line and column which allows position tracking.
1) Semantic Analysis:

    5.1) [SemanticAnalyzer](src/main/java/com/javdin/semantics/SemanticAnalyzer.java)
    - Performs non-modifying semantic validation
    - Detects 4 types of semantic errors:
        1. Return outside function check
        2. Break/Continue outside loop check
        3. Undeclared variable check
        4. Duplicate declaration check
    
    5.2) [Optimizer](src/main/java/com/javdin/semantics/Optimizer.java)
    - Performs AST-modifying optimizations
    - Uses symbol table for scope management
    - Implements 4 optimization techniques:
    - Pass 1: Collect used variables
    - Pass 2: Apply optimizations
        1. Constant folding
        2. Unused variable removal
        3. Dead branch elimination
        4. Unreachable code removal
   
2) [Interpreter](src/main/java/com/javdin/interpreter/Interpreter.java)  executes the optimized AST using the visitor pattern. It implements a tree-walking interpreter with dynamic typing, supporting eight value types: integer, real, boolean, string, array, tuple, function, and void. The interpreter uses a stack-based approach to handle lexical scoping in blocks and functions.

---
# Try Javdin on your machine!
```bash
git clone https://github.com/team-806/javdin.git # Clone project
mvn clean compile  # Compile project
mvn package       # Create JAR

# If you want run Tests
mvn test jacoco:report

# Run example programs...
java -jar target/javdin-1.0.0.jar ./test-resources/test-allfuncs.d
# or try writing something on your own...
```
You can see a lot of simple programs examples in test-resources. In case of any syntax related questions consult [Project D.pdf](docs/Project%20D.pdf).