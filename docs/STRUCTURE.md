# Javdin Project Structure

This document describes the structure and organization of the Javdin interpreter project.

## Project Overview

Javdin is a Java-based dynamic interpreter for an academic language similar to JavaScript. The project follows a traditional compiler pipeline:

1. **Lexical Analysis** - Converts source code into tokens
2. **Syntax Analysis** - Parses tokens into an Abstract Syntax Tree (AST)
3. **Semantic Analysis** - Performs symbol table construction and semantic checks
4. **Interpretation** - Executes the AST directly

## Directory Structure

```
javdin/
├── pom.xml                     # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com.javdin.lexer/       # Lexical analysis
│   │   │   │   ├── Lexer.java          # Main lexer implementation
│   │   │   │   ├── Token.java          # Token representation
│   │   │   │   ├── TokenType.java      # Token type enumeration
│   │   │   │   └── LexicalException.java
│   │   │   ├── com.javdin.parser/      # Syntax analysis
│   │   │   │   ├── Parser.java         # Recursive descent parser
│   │   │   │   └── ParseException.java
│   │   │   ├── com.javdin.ast/         # Abstract Syntax Tree
│   │   │   │   ├── AstNode.java        # Base AST interface
│   │   │   │   ├── AstVisitor.java     # Visitor pattern interface
│   │   │   │   ├── ProgramNode.java    # Root AST node
│   │   │   │   ├── StatementNode.java  # Base statement class
│   │   │   │   ├── ExpressionNode.java # Base expression class
│   │   │   │   ├── DeclarationNode.java
│   │   │   │   ├── LiteralNode.java
│   │   │   │   └── AstNodes.java       # Other AST node stubs
│   │   │   ├── com.javdin.semantics/   # Semantic analysis
│   │   │   │   ├── SemanticAnalyzer.java
│   │   │   │   ├── SymbolTable.java
│   │   │   │   └── Scope.java
│   │   │   ├── com.javdin.interpreter/ # Runtime execution
│   │   │   │   ├── Interpreter.java
│   │   │   │   ├── Value.java          # Runtime value wrapper
│   │   │   │   └── Environment.java    # Runtime environment
│   │   │   ├── com.javdin.utils/       # Utility classes
│   │   │   │   ├── ErrorHandler.java
│   │   │   │   └── IoUtils.java
│   │   │   └── com.javdin.main/        # Entry point
│   │   │       └── Main.java
│   │   └── resources/
│   │       ├── parser.cup              # CUP grammar file
│   │       └── lexer.flex              # JFlex lexer specification
│   └── test/
│       └── java/
│           ├── com.javdin.lexer/       # Lexer unit tests
│           ├── com.javdin.parser/      # Parser unit tests
│           └── com.javdin.integration/ # End-to-end tests
├── test-resources/                     # Sample programs
│   ├── simple.d                        # Basic test program
│   └── complex.d                       # Advanced test program
└── docs/                               # Documentation
    └── Project D.pdf                   # Project requirements
```

## Current Implementation Status

###  Completed

#### Lexical Analysis (100%)
- Maven project setup with all dependencies
- Full lexer implementation with support for:
  - All Keywords (var, if, else, while, for, loop, func, is, then, end, etc.)
  - All Operators (+, -, *, /, ==, !=, /=, <, >, <=, >=, and, or, xor, not, :=, etc.)
  - All Literals (integers, reals, booleans, strings, none)
  - Identifiers and comments
  - Separators (semicolons, newlines)
- 43 comprehensive lexer tests

#### Syntax Analysis (100%)
- **CUP Parser Generator Integration** 
  - Complete grammar specification in `parser.cup`
  - LR parser generation via Maven build process
  - LexerAdapter for token mapping
  
- **Full AST Node Hierarchy** 
  - All statement nodes (if, while, for, loop, return, print, declarations, assignments)
  - All expression nodes (binary ops, unary ops, literals, references)
  - All literal nodes (int, real, string, bool, none, arrays, tuples, functions)
  - Postfix operations (array access, tuple member access, function calls)
  
- **Complete Grammar Support** 
  - Expressions with proper operator precedence (9 levels)
  - All control flow statements (if/then/else, while, for, loop)
  - Variable declarations and assignments
  - Return and print statements
  - Function literals (both `is...end` and `=>` forms)
  - Arrays and tuples with named/unnamed elements
  - Statement separators (semicolons and newlines)
  - Type checking with `is` operator
  
- **Error Handling** 
  - Syntax error detection via CUP
  - ParseException wrapper with error details
  - 26 comprehensive error handling tests
  
- **Comprehensive Testing** 
  - 193 total tests across all components
  - 78% overall code coverage
  - 75% parser package coverage
  - 136 parser-specific tests organized by feature
  
- **Documentation** 
  - Complete parser documentation in `docs/parser.md`
  - Grammar specification documented
  - Usage examples and integration guide
  - Milestone completion reports (M1-M10)

#### Semantic Analysis (Partial)
- Basic semantic analyzer with symbol table support
- Scope management
- Type system foundation

#### Interpreter (Partial)
- Basic interpreter with value system and environment
- Some statement execution support
- Runtime value handling

###  In Progress / TODO

#### Semantic Analysis (Remaining)
- Full type checking implementation
- Function scope and closure support
- Advanced semantic validations
- Constant folding optimizations

#### Interpreter (Remaining)
- Complete interpreter functionality for all AST nodes
- Full support for:
  - All control flow statements
  - Function calls and closures
  - Array and tuple operations
  - Type conversions
  - Built-in functions
- Runtime error handling

#### Future Enhancements
- Optimization passes
- Debugger support
- Better error messages with suggestions
- Performance profiling and optimization

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Generate test coverage report
mvn jacoco:report

# Create executable JAR
mvn package

# Run the interpreter
java -jar target/javdin-1.0.0.jar test-resources/simple.d
```

### Development Workflow

1. **Lexer Development**: Start with lexer tests, implement token recognition
2. **Parser Development**: Define CUP grammar, implement AST construction
3. **Semantic Analysis**: Add symbol table management and semantic checks
4. **Interpreter**: Implement AST execution and runtime behavior
5. **Testing**: Write comprehensive test cases for each component

## Code Organization Principles

- **Separation of Concerns**: Each package handles a specific phase of compilation
- **Visitor Pattern**: Used for AST traversal in semantic analysis and interpretation
- **Immutable AST**: AST nodes are immutable for thread safety and debugging
- **Error Handling**: Centralized error collection and reporting
- **Testing**: Comprehensive unit and integration tests with high coverage

## Contributing

When adding new features:
1. Write tests first (TDD approach)
2. Implement the feature
3. Ensure all tests pass
4. Update documentation as needed
5. Follow the existing code style and patterns

## Future Enhancements

- Optimize interpreter performance
- Add debugging support
- Implement code generation for bytecode
- Add IDE integration features
- Performance profiling and optimization


## Requirement coverage 17.11.2025
Project D requirement	Coverage
§2 Loop/Exit (“exit is the only way out of loop … end”)	InterpreterSemanticsTest.loopExitTerminatesInfiniteLoop
§2 Range loops support arbitrary bounds	rangeLoopSupportsDescendingOrder
Arrays are sparse, 1-based; undefined cells are none	arraysAreSparseAndOneBased, arrayIndexZeroProducesRuntimeError
§Types Tuples: concatenation and access by name/index	tupleConcatenationAndIndexAccessWorkTogether
Operator table: + only defined for numeric pairs, string+string, tuple+tuple, array+array	invalidAdditionBetweenMismatchedTypesFails
“Implicit type conversions are supported” (ints mixing with reals)	implicitNumericConversionAllowsMixedMath
Exit outside loops already enforced	Existing SemanticAnalyzerTest (exit-at-global) continues to cover §2 constraint