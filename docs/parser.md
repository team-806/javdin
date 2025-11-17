# Javdin Parser Documentation

## Overview

The Javdin parser is a **syntax analyzer** built using the CUP (Constructor of Useful Parsers) parser generator. It takes a stream of tokens from the lexer and produces an Abstract Syntax Tree (AST) representing the syntactic structure of a Javdin program.

## Architecture

### Components

1. **CUP Parser Generator** (`parser.cup`)
   - Grammar specification in CUP syntax
   - Defines syntax rules and AST construction
   - Generates Java code for LR parser

2. **Parser Wrapper** (`Parser.java`)
   - Clean API for parsing Javdin code
   - Exception handling and error reporting
   - Bridges lexer and generated parser

3. **Lexer Adapter** (`LexerAdapter.java`)
   - Adapts Javdin lexer to CUP's Scanner interface
   - Maps token types to CUP symbols
   - Preserves source location information

4. **AST Nodes** (`com.javdin.ast` package)
   - Immutable data structures representing program elements
   - Visitor pattern support for traversal
   - Used by semantic analyzer and interpreter

### Data Flow

```
Source Code
    ↓
 Lexer (tokenization)
    ↓
 LexerAdapter (token mapping)
    ↓
 CupParser (LR parsing)
    ↓
 AST (Abstract Syntax Tree)
```

## Usage

### Basic Parsing

```java
// Create lexer from source code
String source = "var x := 42\nprint x";
Lexer lexer = new Lexer(source);

// Create parser
Parser parser = new Parser(lexer);

// Parse to AST
try {
    ProgramNode program = parser.parse();
    // Process the AST...
} catch (ParseException e) {
    System.err.println("Syntax error: " + e.getMessage());
}
```

### Error Handling

The parser throws `ParseException` for syntax errors:

```java
try {
    ProgramNode program = parser.parse();
} catch (ParseException e) {
    // Access error details
    String message = e.getMessage();      // Error description
    int line = e.getLine();               // Line number (if available)
    int column = e.getColumn();           // Column number (if available)
    Throwable cause = e.getCause();       // Original CUP exception
}
```

### AST Traversal

Use the visitor pattern to process the AST:

```java
ProgramNode program = parser.parse();

// Create a visitor
ASTVisitor<Void> printer = new ASTVisitor<>() {
    @Override
    public Void visit(DeclarationNode node) {
        System.out.println("Variable: " + node.getName());
        return null;
    }
    // Implement other visit methods...
};

// Traverse the AST
program.accept(printer);
```

## Grammar Overview

The Javdin grammar implements all features from the Project D specification.

### Expression Hierarchy

Expressions follow proper operator precedence (lowest to highest):

1. **Logical OR** (`or`)
2. **Logical XOR** (`xor`)
3. **Logical AND** (`and`)
4. **Comparisons** (`=`, `!=`, `/=`, `<`, `<=`, `>`, `>=`)
5. **Addition/Subtraction** (`+`, `-`)
6. **Multiplication/Division** (`*`, `/`)
7. **Unary** (`+`, `-`, `not`)
8. **Postfix** (array/tuple access, function calls, member access)
9. **Primary** (literals, identifiers, parentheses)

Example:
```d
var result := x + y * 2 > 10 and not flag
// Parsed as: ((x + (y * 2)) > 10) and (not flag)
```

### Statement Types

The parser recognizes all statement types:

- **Declarations**: `var x := 10, y := 20, z`
- **Assignments**: `x := 42`, `arr[i] := value`, `tuple.field := data`
- **If statements**: `if cond then ... end`, `if cond then ... else ... end`
- **Short if**: `if x > 0 => print x`
- **While loops**: `while cond loop ... end`
- **For loops**: `for i in range loop ... end`, `for i in 1..10 loop ... end`
- **Infinite loops**: `loop ... end`
- **Return statements**: `return`, `return value`
- **Print statements**: `print expr1, expr2, expr3`
- **Expression statements**: `functionCall(args)`

### Literals

All literal types are supported:

- **Integers**: `42`, `-10`, `0`
- **Reals**: `3.14`, `-0.5`, `2.0`
- **Strings**: `"hello"`, `"world"`
- **Booleans**: `true`, `false`
- **None**: `none`
- **Arrays**: `[1, 2, 3]`, `[]`, `[x, y, z]`
- **Tuples**: `{a := 1, b := 2}`, `{x, name := "test", 3}`, `{}`
- **Functions**: `func(x, y) is return x + y; end`, `func(x) => x * 2`

### Statement Separators

Statements can be separated by:
- **Semicolons** (`;`): `var x := 1; var y := 2`
- **Newlines** (`\n`): Multi-line programs
- **Mixed**: Both can be used interchangeably

Multiple consecutive separators are allowed:
```d
var x := 1;;
var y := 2


var z := 3
```

## Grammar Productions

### Core Productions

```cup
program ::= statement_list

statement_list ::= 
    separator_opt statement_list_core separator_opt

statement ::=
    declaration
    | assignment
    | if_statement
    | while_loop  
    | for_loop
    | return_statement
    | print_statement
    | expression

separator ::= SEMICOLON | NEWLINE
```

### Expression Grammar

```cup
expression ::=
    expression OR relation
    | expression AND relation
    | expression XOR relation
    | relation

relation ::=
    factor (EQUAL | NOT_EQUAL | LESS_THAN | ...) factor
    | factor

factor ::=
    factor (PLUS | MINUS) term
    | term

term ::=
    term (MULTIPLY | DIVIDE) unary
    | unary

unary ::=
    (PLUS | MINUS | NOT) primary
    | reference IS type_indicator
    | primary

primary ::=
    literal
    | reference
    | LEFT_PAREN expression RIGHT_PAREN
```

### References (Postfix Operations)

```cup
reference ::=
    simple_reference
    | reference LEFT_BRACKET expression RIGHT_BRACKET  // Array access
    | reference DOT IDENTIFIER                          // Tuple member
    | reference LEFT_PAREN argument_list RIGHT_PAREN   // Function call
```

## Operator Precedence

Precedence declarations in CUP (lowest to highest):

```cup
precedence left OR;
precedence left XOR;
precedence left AND;
precedence left EQUAL, NOT_EQUAL, NOT_EQUAL_ALT;
precedence left LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL;
precedence left PLUS, MINUS;
precedence left MULTIPLY, DIVIDE;
precedence right NOT, UNARY_MINUS, UNARY_PLUS;
precedence left DOT, LEFT_BRACKET, LEFT_PAREN;
```

### Associativity

- Most binary operators are **left-associative**:
  - `a + b + c` → `(a + b) + c`
  - `a and b and c` → `(a and b) and c`

- Unary operators are **right-associative**:
  - `not not x` → `not (not x)`
  - `- - x` → `- (- x)`

- Postfix operators (array access, function calls) are **left-associative**:
  - `arr[i][j]` → `(arr[i])[j]`
  - `f(x)(y)` → `(f(x))(y)`

## Error Handling

### Error Detection

The parser detects all major syntax errors:

- Missing tokens (keywords, operators, delimiters)
- Unmatched delimiters (parentheses, brackets, braces)
- Invalid token sequences
- Malformed expressions
- Incomplete statements

### Error Messages

Error messages include:
- Description of the error
- Expected tokens (when applicable)
- Source position (character index)

Example:
```
Parse error at line 0, column 0: Syntax error at character 15 of input
instead expected token classes are [SEMICOLON, NEWLINE, END]
```

### Error Recovery

Currently, the parser uses CUP's default error recovery:
- Stops parsing at first error (fail-fast)
- Provides basic error messages
- No automatic error correction

**Future Enhancement**: Custom error productions could be added for better recovery and more specific error messages.

## AST Structure

### Node Hierarchy

```
Node (abstract base)
├── ProgramNode
├── StatementNode (abstract)
│   ├── DeclarationNode
│   ├── DeclarationListNode
│   ├── AssignmentNode
│   ├── IfNode
│   ├── WhileNode
│   ├── ForNode
│   ├── LoopNode
│   ├── ReturnNode
│   ├── PrintNode
│   └── ExpressionStatementNode
├── ExpressionNode (abstract)
│   ├── LiteralNode (abstract)
│   │   ├── IntLiteralNode
│   │   ├── RealLiteralNode
│   │   ├── StringLiteralNode
│   │   ├── BoolLiteralNode
│   │   ├── NoneLiteralNode
│   │   ├── ArrayLiteralNode
│   │   ├── TupleLiteralNode
│   │   └── FunctionLiteralNode
│   ├── BinaryOpNode
│   ├── UnaryOpNode
│   ├── IdentifierNode
│   ├── ArrayAccessNode
│   ├── TupleMemberAccessNode
│   ├── FunctionCallNode
│   └── TypeCheckNode
└── (Helper classes)
    ├── TupleElement
    ├── ForHeader
    └── BlockNode
```

### Node Properties

All AST nodes:
- Are **immutable** (all fields final)
- Include **source location** (line, column)
- Support the **visitor pattern**
- Override `equals()` and `hashCode()` for testing

Example node structure:
```java
public class BinaryOpNode extends ExpressionNode {
    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;
    
    // Constructor, getters, visitor support...
}
```

## Testing

### Test Coverage

The parser has comprehensive test coverage:

- **193 total tests** (as of October 15, 2025)
- **78% code coverage** overall
- **75% parser package coverage**
- **88% generated parser coverage**

### Test Organization

Tests are organized by feature:

- `ParserTest.java` - Basic parsing functionality
- `ControlFlowTest.java` - If/while/for statements (23 tests)
- `FunctionLiteralTest.java` - Function literals (10 tests)
- `OperatorPrecedenceTest.java` - Expression precedence (10 tests)
- `AssignmentTest.java` - Declarations and assignments (13 tests)
- `ReturnPrintTest.java` - Return/print statements (31 tests)
- `SeparatorTest.java` - Statement separators (22 tests)
- `ErrorHandlingTest.java` - Error detection (26 tests)
- `EndToEndTest.java` - Integration tests (4 tests)

### Running Tests

```bash
# Run all tests
mvn test

# Run parser tests only
mvn test -Dtest=*Parser*

# Run with coverage report
mvn test jacoco:report
# View: target/site/jacoco/index.html
```

## Performance Characteristics

### Time Complexity

- **Parsing**: O(n) where n = number of tokens
  - CUP generates an LR parser with linear time complexity
  - Each token is processed exactly once

- **AST Construction**: O(n)
  - Node creation happens during parsing
  - No post-processing required

### Space Complexity

- **Parser State**: O(1) for LR parser stack
- **AST**: O(n) nodes for n tokens
- **Overall**: O(n) memory usage

### Scalability

The parser handles:
-  Programs with 1000+ lines
-  Deeply nested structures (limited by JVM stack)
-  Large array/tuple literals
-  Complex expressions with many operators

## Integration

### With Lexer

The lexer provides tokens via the `LexerAdapter`:

```java
public class LexerAdapter implements java_cup.runtime.Scanner {
    private final Lexer lexer;
    
    @Override
    public Symbol next_token() throws Exception {
        Token token = lexer.nextToken();
        int symbolId = mapTokenTypeToSymbol(token.type());
        Object value = extractTokenValue(token);
        return new Symbol(symbolId, token.line(), token.column(), value);
    }
}
```

Key integration points:
- Token type mapping (TokenType → CUP Symbol)
- Value extraction (literals, identifiers)
- Source location preservation

### With Semantic Analyzer

The semantic analyzer processes the parsed AST:

```java
ProgramNode program = parser.parse();
SemanticAnalyzer analyzer = new SemanticAnalyzer();
analyzer.analyze(program);  // Type checking, scope analysis, etc.
```

### With Interpreter

The interpreter executes the analyzed AST:

```java
ProgramNode program = parser.parse();
// Optionally: semantic analysis
Interpreter interpreter = new Interpreter();
interpreter.execute(program);
```

## Limitations and Future Work

### Current Limitations

1. **Error Recovery**: Stops at first error
   - Future: Add error productions for better recovery
   - Continue parsing after errors when possible

2. **Error Messages**: Basic CUP-generated messages
   - Future: Custom messages for common mistakes
   - "Did you mean...?" suggestions

3. **Source Locations**: Character-based positions
   - Future: Better line/column tracking
   - Multi-line token handling

4. **Performance**: No optimization for very large files
   - Future: Streaming parser for huge files
   - Incremental parsing support

### Potential Enhancements

1. **Better Error Messages**:
   ```java
   // Current: "Syntax error at character 15"
   // Future: "Expected 'end' to close 'if' statement at line 5"
   ```

2. **Error Recovery**:
   ```cup
   statement ::= 
       declaration
       | assignment
       | error SEMICOLON  // Skip to next statement
   ```

3. **IDE Integration**:
   - Syntax highlighting support
   - Auto-completion based on parser state
   - Real-time error checking

4. **Performance Profiling**:
   - Identify slow grammar rules
   - Optimize common code patterns
   - Reduce AST node allocations

## Build Integration

### Maven Configuration

The parser is built using Maven with the CUP plugin:

```xml
<plugin>
    <groupId>de.jflex</groupId>
    <artifactId>jflex-maven-plugin</artifactId>
    <version>1.9.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<plugin>
    <groupId>com.github.vbmacher</groupId>
    <artifactId>cup-maven-plugin</artifactId>
    <version>11b-20160615</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Build Process

1. **JFlex**: Generates lexer from `lexer.flex`
2. **CUP**: Generates parser from `parser.cup`
3. **Javac**: Compiles generated + hand-written code
4. **Tests**: Run all unit and integration tests
5. **JaCoCo**: Generate coverage report

```bash
mvn clean compile  # Generate and compile
mvn test          # Run tests
mvn package       # Create JAR
```

## Troubleshooting

### Common Issues

**Q: "Unmapped token type" error**  
A: The `LexerAdapter` doesn't recognize a token. Add mapping in `mapTokenTypeToSymbol()`.

**Q: "Syntax error" with no details**  
A: CUP's default error messages are limited. Check the grammar for ambiguities or add custom error productions.

**Q: Parser seems slow**  
A: Large grammars can slow down parser generation. Profile with `-X` Maven flag to identify bottlenecks.

**Q: AST structure doesn't match expected**  
A: Check grammar actions in `parser.cup`. Ensure correct node types are created.

**Q: Tests fail after grammar changes**  
A: Recompile with `mvn clean compile` to regenerate parser code.

## References

- **CUP Manual**: http://www2.cs.tum.edu/projects/cup/manual.html
- **Project D Specification**: `docs/Project D.md`
- **Parser Plan**: `docs/parser-plan.md`
- **Milestone Reports**: `docs/milestone-*.md`

## Version History

- **Oct 11, 2025**: Initial CUP integration (M1)
- **Oct 11, 2025**: Control flow statements (M7)
- **Oct 15, 2025**: Return/print statements (M8)
- **Oct 15, 2025**: Separators and error handling (M9, M10)
- **Oct 15, 2025**: Documentation completed (M12)

---

*For implementation details, see the source code in `src/main/java/com/javdin/parser/` and grammar specification in `src/main/resources/parser.cup`.*
