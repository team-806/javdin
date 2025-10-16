# Javdin Parser - Project Presentation

**Course**: [F25] Compiler Construction  
**Date**: October 16, 2025  
**Team**: 806

---

## Team Information

### Team 806
- **Timofey Ivlev**
- **George Selivanov**

### Project: Javdin

<img width="200" height="200" alt="Javdin Logo" src="../assets/javdin-logo-400-400.png" />

**Jav**a **d**ynamic **in**terpreter - A dynamic language interpreter with a Bison-based parser

---

## Technology Stack

### Core Technologies
- **Source Language**: Project D (academic dynamic language)
- **Implementation Language**: Java 17
- **Parser Development Tool**: **CUP (Construction of Useful Parsers)**
  - Bison-based parser generator for Java
  - Generates LR parsers from grammar specifications
  - Website: https://www2.cs.tum.edu/projects/cup/

### Build & Testing Tools
- **Build System**: Maven 3.6+
- **Testing Framework**: JUnit 5
- **Assertion Library**: AssertJ
- **Code Coverage**: JaCoCo
- **Lexer Generator**: JFlex (companion to CUP)

### Target Platform
- **JVM** (Java Virtual Machine)
- Runs on any platform with Java 17+

---

## Example Programs

### Example 1: Recursive Factorial

```d
// Factorial function with recursion
var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

var result := factorial(5)
print result
```

**Features demonstrated**:
- Function literals (long form: `func(...) is ... end`)
- Recursion
- Control flow (`if-then-else`)
- Return statements
- Binary operators (`<=`, `-`, `*`)
- Function calls

---

### Example 2: Array Processing

```d
// Array and loop operations
var numbers := [1, 2, 3, 4, 5]
var sum := 0
var i := 0

for i in numbers loop
    sum := sum + i
end

print "Sum:", sum

// Calculate average
var average := sum / 5
print "Average:", average
```

**Features demonstrated**:
- Array literals (`[1, 2, 3, 4, 5]`)
- Variable declarations with initialization
- For-in loops
- String literals
- Multiple print arguments
- Arithmetic operations

---

## Parser Output - AST Structure

### Example 1 AST (Factorial)

```
ProgramNode
├─ DeclarationNode
│  ├─ name: "factorial"
│  └─ initializer: FunctionLiteralNode
│     ├─ parameters: ["n"]
│     ├─ form: long (is...end)
│     └─ body:
│        └─ IfNode
│           ├─ condition: BinaryOpNode
│           │  ├─ operator: "<="
│           │  ├─ left: IdentifierNode("n")
│           │  └─ right: IntLiteralNode(1)
│           ├─ then-branch:
│           │  └─ ReturnNode
│           │     └─ value: IntLiteralNode(1)
│           └─ else-branch:
│              └─ ReturnNode
│                 └─ value: BinaryOpNode
│                    ├─ operator: "*"
│                    ├─ left: IdentifierNode("n")
│                    └─ right: FunctionCallNode
│                       ├─ function: IdentifierNode("factorial")
│                       └─ arguments:
│                          └─ BinaryOpNode
│                             ├─ operator: "-"
│                             ├─ left: IdentifierNode("n")
│                             └─ right: IntLiteralNode(1)
├─ DeclarationNode
│  ├─ name: "result"
│  └─ initializer: FunctionCallNode
│     ├─ function: IdentifierNode("factorial")
│     └─ arguments:
│        └─ IntLiteralNode(5)
└─ PrintNode
   └─ expressions:
      └─ IdentifierNode("result")
```

**AST Analysis**:
- **3 top-level statements**: 2 declarations + 1 print
- **Nested structures**: Function contains if-else, which contains return statements
- **Expression nesting**: `n * factorial(n - 1)` creates nested BinaryOpNode and FunctionCallNode
- **Recursive reference**: `factorial` calls itself (visible in AST)

---

### Example 2 AST (Array Processing)

```
ProgramNode
├─ DeclarationNode
│  ├─ name: "numbers"
│  └─ initializer: ArrayLiteralNode
│     └─ elements:
│        ├─ IntLiteralNode(1)
│        ├─ IntLiteralNode(2)
│        ├─ IntLiteralNode(3)
│        ├─ IntLiteralNode(4)
│        └─ IntLiteralNode(5)
├─ DeclarationNode
│  ├─ name: "sum"
│  └─ initializer: IntLiteralNode(0)
├─ DeclarationNode
│  ├─ name: "i"
│  └─ initializer: IntLiteralNode(0)
├─ ForNode
│  ├─ header:
│  │  ├─ variable: "i"
│  │  └─ iterable: IdentifierNode("numbers")
│  └─ body:
│     └─ AssignmentNode
│        ├─ target: IdentifierNode("sum")
│        └─ value: BinaryOpNode
│           ├─ operator: "+"
│           ├─ left: IdentifierNode("sum")
│           └─ right: IdentifierNode("i")
├─ PrintNode
│  └─ expressions:
│     ├─ StringLiteralNode("Sum:")
│     └─ IdentifierNode("sum")
├─ DeclarationNode
│  ├─ name: "average"
│  └─ initializer: BinaryOpNode
│     ├─ operator: "/"
│     ├─ left: IdentifierNode("sum")
│     └─ right: IntLiteralNode(5)
└─ PrintNode
   └─ expressions:
      ├─ StringLiteralNode("Average:")
      └─ IdentifierNode("average")
```

**AST Analysis**:
- **7 top-level statements**: 4 declarations + 1 for-loop + 2 prints
- **Array literal**: Contains 5 integer elements
- **For-loop structure**: Header specifies iteration, body contains assignment
- **Multiple print arguments**: Each print has 2 expressions (label + value)

---

## Parser Implementation Details

### Architecture Overview

```
┌─────────────────────────────────────────────────┐
│              Parser Architecture                │
├─────────────────────────────────────────────────┤
│                                                 │
│  Source Code (*.d files)                        │
│       ↓                                         │
│  Lexer (tokenization)                           │
│       ↓                                         │
│  LexerAdapter (token → CUP symbol mapping)      │
│       ↓                                         │
│  CupParser (LR parser, grammar rules)           │
│       ↓                                         │
│  AST (Abstract Syntax Tree)                     │
│                                                 │
└─────────────────────────────────────────────────┘
```

### CUP Parser Generator

**What is CUP?**
- **C**onstruction of **U**seful **P**arsers
- Java-based parser generator (similar to Bison/Yacc for C)
- Generates **LR parsers** from grammar specifications
- Developed at TU Munich

**How it works**:
1. Write grammar in `.cup` file
2. CUP generates Java parser code
3. Generated parser uses LR parsing algorithm
4. Parser creates AST nodes during parsing

**Our Grammar File**: `src/main/resources/parser.cup` (~800 lines)

---

### AST Node Hierarchy

```
AstNode (interface)
│
├─── StatementNode (abstract)
│    │
│    ├─── ProgramNode (root)
│    ├─── DeclarationNode
│    ├─── AssignmentNode
│    ├─── IfNode
│    ├─── WhileNode
│    ├─── ForNode
│    ├─── LoopNode (infinite loop)
│    ├─── ReturnNode
│    ├─── PrintNode
│    └─── ExpressionStatementNode
│
└─── ExpressionNode (abstract)
     │
     ├─── LiteralNode (abstract)
     │    ├─── IntLiteralNode
     │    ├─── RealLiteralNode
     │    ├─── StringLiteralNode
     │    ├─── BoolLiteralNode
     │    ├─── NoneLiteralNode
     │    ├─── ArrayLiteralNode
     │    ├─── TupleLiteralNode
     │    └─── FunctionLiteralNode
     │
     ├─── BinaryOpNode
     ├─── UnaryOpNode
     ├─── IdentifierNode
     ├─── ArrayAccessNode
     ├─── TupleMemberAccessNode
     ├─── FunctionCallNode
     └─── TypeCheckNode (for 'is' operator)
```

**Total**: 28 different AST node types

---

### AST Node Representation

#### Node Structure

All AST nodes share common characteristics:

```java
public abstract class StatementNode implements AstNode {
    private final int line;      // Source location
    private final int column;    // Source location
    
    protected StatementNode(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    // Visitor pattern support
    public abstract <T> T accept(AstVisitor<T> visitor);
}
```

#### Example: BinaryOpNode

```java
public class BinaryOpNode extends ExpressionNode {
    private final ExpressionNode left;     // Left operand
    private final String operator;         // Operator symbol
    private final ExpressionNode right;    // Right operand
    
    public BinaryOpNode(int line, int column, 
                        ExpressionNode left, 
                        String operator, 
                        ExpressionNode right) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    // Getters
    public ExpressionNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExpressionNode getRight() { return right; }
    
    // Visitor pattern
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBinaryOp(this);
    }
}
```

**Key Features**:
- **Immutable**: All fields are `final`
- **Source location**: Every node knows its position
- **Type-safe**: Strong typing for operands
- **Visitor support**: For AST traversal

---

### Core Parsing Logic

#### 1. Grammar Specification

**Expression precedence** (lowest to highest):
```cup
precedence left OR;                // or
precedence left XOR;               // xor
precedence left AND;               // and
precedence left EQUAL, NOT_EQUAL;  // =, !=, /=
precedence left LESS_THAN, ...;    // <, <=, >, >=
precedence left PLUS, MINUS;       // +, -
precedence left MULTIPLY, DIVIDE;  // *, /
precedence right NOT;              // not
precedence left DOT, LEFT_BRACKET; // ., [, (
```

#### 2. Production Rules

**Example: If statement**
```cup
if_statement ::=
    IF expression:cond THEN statement_list:thenBody END
    {: RESULT = new IfNode(IFleft, IFright, cond, 
                           new BlockNode(thenBody), null); :}
  | IF expression:cond THEN statement_list:thenBody 
       ELSE statement_list:elseBody END
    {: RESULT = new IfNode(IFleft, IFright, cond,
                           new BlockNode(thenBody), 
                           new BlockNode(elseBody)); :}
  | IF expression:cond SHORT_IF statement_list:body
    {: RESULT = new IfNode(IFleft, IFright, cond,
                           new BlockNode(body), null); :}
  ;
```

**What happens**:
1. CUP matches tokens to grammar rule
2. Executes semantic action in `{: ... :}`
3. Creates AST node (IfNode)
4. Passes source position (`IFleft`, `IFright`)
5. Returns node as `RESULT`

#### 3. Token Mapping

**LexerAdapter** bridges our lexer to CUP:

```java
public class LexerAdapter implements Scanner {
    private final Lexer lexer;
    
    @Override
    public Symbol next_token() throws Exception {
        Token token = lexer.nextToken();
        int symbolId = mapTokenTypeToSymbol(token.type());
        Object value = extractTokenValue(token);
        return new Symbol(symbolId, 
                         token.line(), 
                         token.column(), 
                         value);
    }
    
    private int mapTokenTypeToSymbol(TokenType type) {
        return switch (type) {
            case IF -> Symbols.IF;
            case THEN -> Symbols.THEN;
            case INTEGER -> Symbols.INTEGER;
            // ... 50+ token types mapped
        };
    }
}
```

---

### Parsing Algorithm

**CUP uses LR(1) parsing**:

1. **Shift-Reduce Algorithm**:
   - **Shift**: Push token onto stack
   - **Reduce**: Apply grammar rule, create AST node
   - **Accept**: Complete parse, return AST

2. **Example: Parsing `1 + 2 * 3`**

   ```
   Step 1: Shift 1          Stack: [1]
   Step 2: Reduce (literal) Stack: [IntLiteralNode(1)]
   Step 3: Shift +          Stack: [IntLiteralNode(1), +]
   Step 4: Shift 2          Stack: [IntLiteralNode(1), +, 2]
   Step 5: Reduce (literal) Stack: [IntLiteralNode(1), +, IntLiteralNode(2)]
   Step 6: Shift *          Stack: [..., +, IntLiteralNode(2), *]
   Step 7: Shift 3          Stack: [..., +, IntLiteralNode(2), *, 3]
   Step 8: Reduce (literal) Stack: [..., +, IntLiteralNode(2), *, IntLiteralNode(3)]
   Step 9: Reduce (* rule)  Stack: [IntLiteralNode(1), +, BinaryOpNode(2, *, 3)]
   Step 10: Reduce (+ rule) Stack: [BinaryOpNode(1, +, BinaryOpNode(2, *, 3))]
   ```

   **Result**: Respects precedence! `*` applied before `+`

3. **Time Complexity**: O(n) where n = number of tokens

---

### Statement Separators

**Project D allows flexible separation**:

```cup
separator ::=
    SEMICOLON      // ;
  | NEWLINE        // \n
  ;

separator_list ::=
    separator_list separator
  | separator
  ;

statement_list ::=
    separator_opt statement_list_core separator_opt
  ;
```

**Examples**:
```d
var x := 1; var y := 2    // Semicolons
var x := 1
var y := 2                // Newlines
var x := 1;
var y := 2                // Mixed
```

All three are equivalent!

---

### Error Handling

**How errors are detected**:

1. **Lexer errors**: Invalid characters, malformed literals
2. **Parser errors**: Syntax errors (CUP's built-in detection)
3. **Semantic errors**: Type mismatches (future phase)

**Example error message**:
```
Parse error at line 3, column 15: Syntax error
Expected one of: SEMICOLON, NEWLINE, END
Found: IDENTIFIER
```

**Current strategy**: Fail-fast
- Stop at first error
- Clear error message
- Provides error location

**Future enhancement**: Error recovery
- Continue parsing after errors
- Report multiple errors
- Suggest fixes

---

## Implementation Statistics

### Code Metrics

| Metric | Value |
|--------|-------|
| **Total Tests** | 193 tests |
| **Test Coverage** | 78% overall, 81.5% parser |
| **Grammar Lines** | ~800 lines (parser.cup) |
| **AST Node Types** | 28 types |
| **Supported Operators** | 20+ operators |
| **Keywords** | 15 keywords |
| **Development Time** | ~60 hours |

### Test Organization

```
Parser Tests (136 tests):
├── ControlFlowTest.java        (23 tests) - if/while/for
├── ReturnPrintTest.java        (31 tests) - return/print
├── SeparatorTest.java          (22 tests) - semicolons/newlines
├── ErrorHandlingTest.java      (26 tests) - error detection
├── FunctionLiteralTest.java    (10 tests) - functions
├── OperatorPrecedenceTest.java (10 tests) - precedence
├── AssignmentTest.java         (13 tests) - declarations
└── ParserTest.java             (11 tests) - basic features

Integration Tests (4 tests):
└── EndToEndTest.java           (4 tests)  - full pipeline

Lexer Tests (43 tests):
├── LexerTest.java              (22 tests)
└── LexerEnhancedTest.java      (21 tests)
```

**All 193 tests passing** ✅

---

## Key Features Implemented

### ✅ Expressions
- Binary operators: `+`, `-`, `*`, `/`, `=`, `!=`, `<`, `>`, `<=`, `>=`, `and`, `or`, `xor`
- Unary operators: `+`, `-`, `not`
- 9 levels of operator precedence
- Type checking: `expr is type`

### ✅ Statements
- Variable declarations: `var x := 10, y := 20`
- Assignments: `x := value`
- If statements: `if...then...end`, `if...then...else...end`
- Short if: `if condition => action`
- While loops: `while...loop...end`
- For loops: `for x in iterable loop...end`
- Return and print statements

### ✅ Literals
- All basic types: int, real, string, bool, none
- Collections: arrays `[...]`, tuples `{...}`
- Functions: `func(x) is...end`, `func(x) => expr`

### ✅ References
- Variables, array access, tuple members, function calls
- Chaining: `obj.field[5].method()`

---

## Milestones Completed

| # | Milestone | Status |
|---|-----------|--------|
| M1 | Environment Setup and CUP Integration | ✅ Complete |
| M2 | Core Grammar - Literals | ✅ Complete |
| M3 | Expression Grammar with Precedence | ✅ Complete |
| M4 | References and Postfix Operations | ✅ Complete |
| M5 | Function Literals | ✅ Complete |
| M6 | Declarations and Assignments | ✅ Complete |
| M7 | Control Flow Statements | ✅ Complete |
| M8 | Remaining Statements | ✅ Complete |
| M9 | Statement Organization | ✅ Complete |
| M10 | Error Handling | ✅ Complete |
| M11 | Comprehensive Testing | ✅ Complete |
| M12 | Documentation | ✅ Complete |
| M13 | Integration | ✅ Complete |

**Parser: 100% Complete** 🎉

---

## Next Steps

### Immediate
1. ✅ **Parser** - Complete (this presentation)
2. 🚧 **Semantic Analysis** - In progress
   - Type checking
   - Symbol table management
   - Scope analysis

### Future
3. **Interpreter** - Planned
   - AST execution
   - Runtime environment
   - Built-in functions

4. **Optimizations** - Future
   - Constant folding
   - Dead code elimination
   - Performance tuning

---

## Conclusion

### What We Built
- **Complete parser** for Project D language
- **193 comprehensive tests** with 78% coverage
- **28 AST node types** representing all language features
- **CUP-based LR parser** with O(n) performance
- **Full documentation** (1,500+ lines)

### Technical Highlights
- ✅ Proper operator precedence (9 levels)
- ✅ Flexible syntax (semicolons or newlines)
- ✅ Comprehensive error detection
- ✅ Clean AST structure (immutable, type-safe)
- ✅ Visitor pattern for AST traversal
- ✅ Source location tracking

### Success Metrics
- ✅ All Project D features supported
- ✅ All test programs parse correctly
- ✅ Clean, maintainable code
- ✅ Fully documented
- ✅ Production-ready

---

## Thank You!

**Questions?**

---

### References

- **CUP Manual**: http://www2.cs.tum.edu/projects/cup/
- **Project Repository**: https://github.com/team-806/javdin
- **Documentation**: `docs/parser.md`, `docs/parser-plan.md`
- **Test Suite**: `src/test/java/com/javdin/parser/`

---

**Team 806** - Javdin Parser  
October 16, 2025
