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

### ✅ Completed
- Maven project setup with all dependencies
- Basic package structure
- Lexer implementation with support for:
  - Keywords (var, if, else, while, etc.)
  - Operators (+, -, *, /, ==, !=, etc.)
  - Literals (integers, reals, booleans, strings)
  - Identifiers and comments
- Basic AST node hierarchy
- Simple recursive descent parser (placeholder for CUP)
- Semantic analyzer with symbol table support
- Interpreter with value system and environment
- Utility classes for error handling and I/O
- Unit tests for lexer and parser
- Integration tests
- Sample test programs

### 🚧 In Progress / TODO
- Complete CUP grammar implementation
- Full AST node implementations
- Complete parser integration with CUP
- Advanced semantic analysis features
- Full interpreter functionality
- Support for:
  - Control flow (if, while, for)
  - Functions and lambdas
  - Arrays and tuples
  - Type conversions
  - Built-in functions

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
