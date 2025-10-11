# Syntax Analyzer Implementation Plan for Javdin

## Overview
This document outlines a comprehensive plan to implement a CUP-based syntax analyzer (parser) for the Javdin language. The parser will replace the current placeholder recursive descent parser and generate a complete Abstract Syntax Tree (AST) that conforms to Project D specifications.

## Current State Analysis

### ✅ What We Have
- **Lexer**: Fully functional lexer with all token types defined
- **AST Nodes**: Complete set of AST node classes implementing the Visitor pattern
- **Basic Parser**: Placeholder recursive descent parser with minimal functionality
- **CUP Grammar Stub**: Initial `parser.cup` file with basic structure (only handles declarations and literals)
- **Test Infrastructure**: ParserTest with basic test cases
- **Token Types**: All tokens from Project D spec are defined in `TokenType.java`

### ❌ What's Missing
- **Complete CUP Grammar**: Full grammar covering all Project D constructs
- **Maven CUP Plugin Configuration**: Plugin is currently commented out in `pom.xml`
- **Parser Integration**: Integration between CUP-generated parser and existing code
- **Comprehensive Tests**: Parser tests for all language features
- **Error Recovery**: Robust error handling and recovery in the grammar
- **AST Construction**: Complete AST node creation in grammar actions

## Project D Language Features to Implement

Based on the spec, the parser must handle:

1. **Declarations**: `var` with optional initialization using `:=`
2. **Statements**:
   - Assignment: `Reference := Expression`
   - If statements: `if Expression then Body end` and `if Expression => Body`
   - While loop: `while Expression loop Body end`
   - For loops: `for [IDENT in] Expression [.. Expression] loop Body end`
   - Infinite loops: `loop Body end`
   - Exit: `exit`
   - Return: `return [Expression]`
   - Print: `print Expression {, Expression}`
3. **Expressions**:
   - Literals: integers, reals, strings, booleans, arrays, tuples, functions, `none`
   - Binary operators: `+`, `-`, `*`, `/`, `<`, `<=`, `>`, `>=`, `=`, `/=`, `and`, `or`, `xor`
   - Unary operators: `+`, `-`, `not`
   - Type checking: `Reference is TypeIndicator`
4. **References**:
   - Variables: `IDENT`
   - Array access: `Reference[Expression]`
   - Function calls: `Reference(Expression, ...)`
   - Tuple access: `Reference.IDENT` or `Reference.INTEGER`
5. **Composite Types**:
   - Arrays: `[Expression, ...]`
   - Tuples: `{[IDENT :=] Expression, ...}`
   - Functions: `func[(IDENT, ...)] (is Body end | => Expression)`

---

## MILESTONE 1: Environment Setup and CUP Integration
**Goal**: Configure the build system to generate parser from CUP grammar

### Task 1.1: Update Maven Configuration
**File**: `pom.xml`

#### Actions:
1. **Verify CUP Dependencies**: Ensure `java-cup-runtime` dependency is present and correct version
2. **Uncomment and Configure CUP Plugin**:
   ```xml
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
       <configuration>
           <cupDefinition>src/main/resources/parser.cup</cupDefinition>
           <outputDirectory>target/generated-sources/cup</outputDirectory>
           <className>CupParser</className>
           <symbolsName>Symbols</symbolsName>
           <packageName>com.javdin.parser.generated</packageName>
       </configuration>
   </plugin>
   ```
3. **Add build-helper-maven-plugin**: To add generated sources to the build path
   ```xml
   <plugin>
       <groupId>org.codehaus.mojo</groupId>
       <artifactId>build-helper-maven-plugin</artifactId>
       <version>3.4.0</version>
       <executions>
           <execution>
               <id>add-source</id>
               <phase>generate-sources</phase>
               <goals>
                   <goal>add-source</goal>
               </goals>
               <configuration>
                   <sources>
                       <source>target/generated-sources/cup</source>
                   </sources>
               </configuration>
           </execution>
       </executions>
   </plugin>
   ```

#### Testing:
- Run `mvn clean generate-sources` and verify CUP parser is generated
- Check `target/generated-sources/cup/com/javdin/parser/generated/` for generated files

### Task 1.2: Create Parser Wrapper
**File**: `src/main/java/com/javdin/parser/Parser.java`

#### Actions:
1. Refactor existing `Parser.java` to wrap the CUP-generated parser
2. Create adapter that:
   - Takes a `Lexer` instance
   - Creates a scanner adapter for CUP
   - Delegates parsing to CUP parser
   - Returns `ProgramNode`
3. Preserve existing public API so tests don't break

#### Code Structure:
```java
public class Parser {
    private final Lexer lexer;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    
    public ProgramNode parse() throws ParseException {
        try {
            CupParser cupParser = new CupParser(new LexerAdapter(lexer));
            return (ProgramNode) cupParser.parse().value;
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0, 0);
        }
    }
    
    // Inner class to adapt Lexer to CUP's scanner interface
    private static class LexerAdapter implements java_cup.runtime.Scanner {
        // Implementation
    }
}
```

### Task 1.3: Create Lexer-to-CUP Adapter
**File**: `src/main/java/com/javdin/parser/LexerAdapter.java`

#### Actions:
1. Implement `java_cup.runtime.Scanner` interface
2. Convert `Token` objects from our Lexer to CUP's `Symbol` objects
3. Map `TokenType` enum to CUP terminal symbols
4. Preserve line/column information for error reporting

**Completion Criteria**:
- ✅ Maven builds successfully with CUP plugin enabled
- ✅ Parser code is generated in `target/generated-sources/cup`
- ✅ `Parser` class wraps CUP parser and maintains existing API
- ✅ Existing basic tests still pass

---

## MILESTONE 2: Core Grammar - Literals and Simple Expressions
**Goal**: Implement grammar rules for all literal types and basic expressions

### Task 2.1: Define All Terminals in CUP
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Review `TokenType.java` and ensure all tokens are declared as terminals
2. Add terminals with proper types:
   ```cup
   // Terminals with values
   terminal String IDENTIFIER;
   terminal Integer INTEGER;
   terminal Double REAL;
   terminal String STRING;
   terminal Boolean BOOL;
   
   // Keywords (no value)
   terminal VAR, IF, THEN, ELSE, END, WHILE, FOR, IN, LOOP;
   terminal EXIT, FUNC, IS, RETURN, PRINT, TRUE, FALSE, NONE;
   
   // Operators
   terminal PLUS, MINUS, MULTIPLY, DIVIDE;
   terminal ASSIGN_OP;  // :=
   terminal EQUAL, NOT_EQUAL, NOT_EQUAL_ALT;  // =, !=, /=
   terminal LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL;
   terminal AND, OR, XOR, NOT;
   terminal RANGE;  // ..
   terminal SHORT_IF;  // =>
   
   // Delimiters
   terminal LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE;
   terminal LEFT_BRACKET, RIGHT_BRACKET;
   terminal SEMICOLON, COMMA, DOT, COLON;
   
   // Type indicators
   terminal INT_TYPE, REAL_TYPE, BOOL_TYPE, STRING_TYPE;
   terminal NONE_TYPE, ARRAY_TYPE, TUPLE_TYPE, FUNC_TYPE;
   
   // Special
   terminal NEWLINE;
   ```

### Task 2.2: Implement Literal Productions
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Expand `literal` production to handle all literal types:
   ```cup
   literal ::= 
       INTEGER:val
       {: RESULT = new LiteralNode(val, LiteralNode.LiteralType.INTEGER, 
                                    valleft, valright); :}
   | REAL:val
       {: RESULT = new LiteralNode(val, LiteralNode.LiteralType.REAL, 
                                    valleft, valright); :}
   | STRING:val
       {: RESULT = new LiteralNode(val, LiteralNode.LiteralType.STRING, 
                                    valleft, valright); :}
   | TRUE
       {: RESULT = new LiteralNode(true, LiteralNode.LiteralType.BOOLEAN, 
                                    TRUEleft, TRUEright); :}
   | FALSE
       {: RESULT = new LiteralNode(false, LiteralNode.LiteralType.BOOLEAN, 
                                    FALSEleft, FALSEright); :}
   | NONE
       {: RESULT = new LiteralNode(null, LiteralNode.LiteralType.NONE, 
                                    NONEleft, NONEright); :}
   | array_literal
   | tuple_literal
   | function_literal
   ;
   ```

### Task 2.3: Add Array Literal Production
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Define `array_literal` production:
   ```cup
   non terminal ArrayLiteralNode array_literal;
   non terminal List<ExpressionNode> expression_list;
   non terminal List<ExpressionNode> expression_list_opt;
   
   array_literal ::=
       LEFT_BRACKET expression_list_opt:exprs RIGHT_BRACKET
       {: RESULT = new ArrayLiteralNode(exprs, LEFT_BRACKETleft, LEFT_BRACKETright); :}
   ;
   
   expression_list_opt ::=
       expression_list:list
       {: RESULT = list; :}
   | /* empty */
       {: RESULT = new ArrayList<>(); :}
   ;
   
   expression_list ::=
       expression_list:list COMMA expression:expr
       {: list.add(expr); RESULT = list; :}
   | expression:expr
       {: List<ExpressionNode> list = new ArrayList<>();
          list.add(expr);
          RESULT = list; :}
   ;
   ```

2. Create `ArrayLiteralNode` class if it doesn't exist

### Task 2.4: Add Tuple Literal Production
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Define `tuple_literal` production:
   ```cup
   non terminal TupleLiteralNode tuple_literal;
   non terminal List<TupleElement> tuple_element_list;
   non terminal TupleElement tuple_element;
   
   tuple_literal ::=
       LEFT_BRACE tuple_element_list:elements RIGHT_BRACE
       {: RESULT = new TupleLiteralNode(elements, LEFT_BRACEleft, LEFT_BRACEright); :}
   | LEFT_BRACE RIGHT_BRACE
       {: RESULT = new TupleLiteralNode(new ArrayList<>(), LEFT_BRACEleft, LEFT_BRACEright); :}
   ;
   
   tuple_element_list ::=
       tuple_element_list:list COMMA tuple_element:elem
       {: list.add(elem); RESULT = list; :}
   | tuple_element:elem
       {: List<TupleElement> list = new ArrayList<>();
          list.add(elem);
          RESULT = list; :}
   ;
   
   tuple_element ::=
       IDENTIFIER:name ASSIGN_OP expression:expr
       {: RESULT = new TupleElement(name, expr); :}
   | expression:expr
       {: RESULT = new TupleElement(null, expr); :}
   ;
   ```

2. Create `TupleLiteralNode` and `TupleElement` helper class

**Completion Criteria**:
- ✅ All literal types can be parsed
- ✅ Arrays with 0 or more elements work
- ✅ Tuples with named and unnamed elements work
- ✅ Tests pass for parsing various literals

---

## MILESTONE 3: Expression Grammar with Proper Precedence
**Goal**: Implement complete expression grammar with correct operator precedence

### Task 3.1: Define Operator Precedence
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add precedence declarations (from lowest to highest):
   ```cup
   // Precedence (lowest to highest)
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

### Task 3.2: Implement Expression Productions
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Create expression hierarchy following Project D spec:
   ```cup
   expression ::=
       expression:left OR relation:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "or", right); :}
   | expression:left AND relation:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "and", right); :}
   | expression:left XOR relation:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "xor", right); :}
   | relation:r
       {: RESULT = r; :}
   ;
   
   relation ::=
       factor:left LESS_THAN factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "<", right); :}
   | factor:left LESS_EQUAL factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "<=", right); :}
   | factor:left GREATER_THAN factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, ">", right); :}
   | factor:left GREATER_EQUAL factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, ">=", right); :}
   | factor:left EQUAL factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "=", right); :}
   | factor:left NOT_EQUAL factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "!=", right); :}
   | factor:left NOT_EQUAL_ALT factor:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "/=", right); :}
   | factor:f
       {: RESULT = f; :}
   ;
   
   factor ::=
       factor:left PLUS term:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "+", right); :}
   | factor:left MINUS term:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "-", right); :}
   | term:t
       {: RESULT = t; :}
   ;
   
   term ::=
       term:left MULTIPLY unary:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "*", right); :}
   | term:left DIVIDE unary:right
       {: RESULT = new BinaryOpNode(leftleft, leftright, left, "/", right); :}
   | unary:u
       {: RESULT = u; :}
   ;
   ```

### Task 3.3: Implement Unary Operations
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add unary productions including `is` type checking:
   ```cup
   unary ::=
       PLUS:op primary:expr
       {: RESULT = new UnaryOpNode(opleft, opright, "+", expr); :}
   | MINUS:op primary:expr
       {: RESULT = new UnaryOpNode(opleft, opright, "-", expr); :}  %prec UNARY_MINUS
   | NOT:op primary:expr
       {: RESULT = new UnaryOpNode(opleft, opright, "not", expr); :}
   | reference:ref IS type_indicator:type
       {: RESULT = new TypeCheckNode(refleft, refright, ref, type); :}
   | primary:p
       {: RESULT = p; :}
   ;
   
   type_indicator ::=
       INT_TYPE {: RESULT = "int"; :}
   | REAL_TYPE {: RESULT = "real"; :}
   | BOOL_TYPE {: RESULT = "bool"; :}
   | STRING_TYPE {: RESULT = "string"; :}
   | NONE_TYPE {: RESULT = "none"; :}
   | LEFT_BRACKET RIGHT_BRACKET {: RESULT = "array"; :}
   | LEFT_BRACE RIGHT_BRACE {: RESULT = "tuple"; :}
   | FUNC_TYPE {: RESULT = "func"; :}
   ;
   ```

2. Create `TypeCheckNode` AST class

### Task 3.4: Implement Primary Expressions
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Define primary (highest precedence) expressions:
   ```cup
   primary ::=
       literal:lit
       {: RESULT = lit; :}
   | reference:ref
       {: RESULT = ref; :}
   | LEFT_PAREN expression:expr RIGHT_PAREN
       {: RESULT = expr; :}
   ;
   ```

**Completion Criteria**:
- ✅ All binary operators work with correct precedence
- ✅ Unary operators work correctly
- ✅ Type checking with `is` works
- ✅ Parenthesized expressions override precedence
- ✅ Tests for complex expressions pass

---

## MILESTONE 4: References and Postfix Operations
**Goal**: Implement array access, function calls, and tuple member access

### Task 4.1: Implement Reference Production
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Define reference as a base identifier with optional postfix operations:
   ```cup
   non terminal ExpressionNode reference;
   non terminal ExpressionNode reference_base;
   
   reference ::=
       reference_base:base
       {: RESULT = base; :}
   | reference:ref LEFT_BRACKET expression:index RIGHT_BRACKET
       {: RESULT = new ArrayAccessNode(refleft, refright, ref, index); :}
   | reference:ref LEFT_PAREN argument_list_opt:args RIGHT_PAREN
       {: RESULT = new FunctionCallNode(refleft, refright, ref, args); :}
   | reference:ref DOT IDENTIFIER:member
       {: RESULT = new TupleMemberAccessNode(refleft, refright, ref, member); :}
   | reference:ref DOT INTEGER:index
       {: RESULT = new TupleMemberAccessNode(refleft, refright, ref, index); :}
   ;
   
   reference_base ::=
       IDENTIFIER:name
       {: RESULT = new ReferenceNode(nameleft, nameright, name); :}
   ;
   ```

### Task 4.2: Add Function Call Arguments
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Define argument list productions:
   ```cup
   non terminal List<ExpressionNode> argument_list_opt;
   non terminal List<ExpressionNode> argument_list;
   
   argument_list_opt ::=
       argument_list:list
       {: RESULT = list; :}
   | /* empty */
       {: RESULT = new ArrayList<>(); :}
   ;
   
   argument_list ::=
       argument_list:list COMMA expression:expr
       {: list.add(expr); RESULT = list; :}
   | expression:expr
       {: List<ExpressionNode> list = new ArrayList<>();
          list.add(expr);
          RESULT = list; :}
   ;
   ```

### Task 4.3: Create Missing AST Nodes
**Files**: Create new AST node classes as needed

#### Actions:
1. Create `TupleMemberAccessNode` extending `ExpressionNode`
2. Verify `ArrayAccessNode` and `FunctionCallNode` exist and are correct
3. Ensure all nodes implement the visitor pattern

**Completion Criteria**:
- ✅ Variable references work
- ✅ Array indexing works: `arr[5]`
- ✅ Function calls work: `func(a, b, c)`
- ✅ Tuple member access works: `tuple.name` and `tuple.1`
- ✅ Chained operations work: `obj.method()[0].field`
- ✅ Tests for all reference types pass

---

## MILESTONE 5: Function Literals
**Goal**: Implement function literal syntax with both statement and expression bodies

### Task 5.1: Implement Function Literal Production
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add function literal grammar:
   ```cup
   non terminal FunctionLiteralNode function_literal;
   non terminal List<String> parameter_list_opt;
   non terminal List<String> parameter_list;
   
   function_literal ::=
       FUNC parameter_list_opt:params IS statement_list:body END
       {: RESULT = new FunctionLiteralNode(FUNCleft, FUNCright, params, body, false); :}
   | FUNC parameter_list_opt:params SHORT_IF expression:expr
       {: RESULT = new FunctionLiteralNode(FUNCleft, FUNCright, params, expr, true); :}
   ;
   
   parameter_list_opt ::=
       LEFT_PAREN parameter_list:params RIGHT_PAREN
       {: RESULT = params; :}
   | LEFT_PAREN RIGHT_PAREN
       {: RESULT = new ArrayList<>(); :}
   | /* empty - no parentheses */
       {: RESULT = new ArrayList<>(); :}
   ;
   
   parameter_list ::=
       parameter_list:list COMMA IDENTIFIER:param
       {: list.add(param); RESULT = list; :}
   | IDENTIFIER:param
       {: List<String> list = new ArrayList<>();
          list.add(param);
          RESULT = list; :}
   ;
   ```

### Task 5.2: Update FunctionLiteralNode
**File**: `src/main/java/com/javdin/ast/FunctionLiteralNode.java`

#### Actions:
1. Ensure `FunctionLiteralNode` can handle both:
   - Statement list body: `func(x) is ... end`
   - Expression body: `func(x) => expr`
2. Add appropriate constructors and fields

**Completion Criteria**:
- ✅ Functions with statement bodies work: `func(a, b) is return a + b end`
- ✅ Functions with expression bodies work: `func(x) => x * x`
- ✅ Functions with no parameters work: `func => 42`
- ✅ Functions can be assigned to variables
- ✅ Tests for function literals pass

---

## MILESTONE 6: Declarations and Assignments
**Goal**: Implement variable declarations with Project D syntax and assignment statements

### Task 6.1: Update Declaration Production
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Update to use `:=` operator as per Project D spec:
   ```cup
   declaration ::=
       VAR variable_definition_list:defs
       {: RESULT = new DeclarationListNode(VARleft, VARright, defs); :}
   ;
   
   non terminal List<DeclarationNode> variable_definition_list;
   non terminal DeclarationNode variable_definition;
   
   variable_definition_list ::=
       variable_definition_list:list COMMA variable_definition:def
       {: list.add(def); RESULT = list; :}
   | variable_definition:def
       {: List<DeclarationNode> list = new ArrayList<>();
          list.add(def);
          RESULT = list; :}
   ;
   
   variable_definition ::=
       IDENTIFIER:name ASSIGN_OP expression:expr
       {: RESULT = new DeclarationNode(name, expr, nameleft, nameright); :}
   | IDENTIFIER:name
       {: RESULT = new DeclarationNode(name, null, nameleft, nameright); :}
   ;
   ```

### Task 6.2: Implement Assignment Statement
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add assignment production:
   ```cup
   assignment ::=
       reference:target ASSIGN_OP expression:value
       {: RESULT = new AssignmentNode(targetleft, targetright, target, value); :}
   ;
   ```

2. Update statement production to include assignment

**Completion Criteria**:
- ✅ Multiple variables can be declared: `var x := 1, y := 2, z`
- ✅ Assignments work: `x := 42`
- ✅ Complex left-hand sides work: `arr[i] := value`, `tuple.field := value`
- ✅ Tests for declarations and assignments pass

---

## MILESTONE 7: Control Flow Statements
**Goal**: Implement if, while, for, and loop statements

### Task 7.1: Implement If Statements
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add both if statement forms:
   ```cup
   if_statement ::=
       IF expression:cond THEN statement_list:thenBody END
       {: RESULT = new IfNode(IFleft, IFright, cond, thenBody, null); :}
   | IF expression:cond THEN statement_list:thenBody ELSE statement_list:elseBody END
       {: RESULT = new IfNode(IFleft, IFright, cond, thenBody, elseBody); :}
   | IF expression:cond SHORT_IF statement_list:body
       {: RESULT = new IfNode(IFleft, IFright, cond, body, null); :}
   ;
   ```

### Task 7.2: Implement While Loop
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add while loop production:
   ```cup
   while_loop ::=
       WHILE expression:cond LOOP statement_list:body END
       {: RESULT = new WhileNode(WHILEleft, WHILEright, cond, body); :}
   ;
   ```

### Task 7.3: Implement For Loops
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add for loop with all variants:
   ```cup
   for_loop ::=
       for_header_opt:header LOOP statement_list:body END
       {: RESULT = new ForNode(LOOPleft, LOOPright, header, body); :}
   ;
   
   non terminal ForHeader for_header_opt;
   non terminal ForHeader for_header;
   
   for_header_opt ::=
       for_header:h
       {: RESULT = h; :}
   | /* empty - infinite loop */
       {: RESULT = null; :}
   ;
   
   for_header ::=
       FOR IDENTIFIER:var IN expression:iterable
       {: RESULT = new ForHeader(var, iterable, null); :}
   | FOR IDENTIFIER:var IN expression:start RANGE expression:end
       {: RESULT = new ForHeader(var, start, end); :}
   | FOR expression:start RANGE expression:end
       {: RESULT = new ForHeader(null, start, end); :}
   | FOR expression:iterable
       {: RESULT = new ForHeader(null, iterable, null); :}
   ;
   ```

2. Create `ForHeader` helper class to encapsulate for loop variants

### Task 7.4: Implement Exit Statement
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add exit production:
   ```cup
   exit_statement ::=
       EXIT
       {: RESULT = new BreakNode(EXITleft, EXITright); :}
   ;
   ```

**Completion Criteria**:
- ✅ If-then-end works
- ✅ If-then-else-end works
- ✅ Short if (=>) works
- ✅ While loops work
- ✅ For-in loops work: `for i in array`
- ✅ Range loops work: `for i in 1..10`
- ✅ Infinite loops work: `loop ... end`
- ✅ Exit statement works
- ✅ Tests for all control flow pass

---

## MILESTONE 8: Remaining Statements
**Goal**: Implement return and print statements

### Task 8.1: Implement Return Statement
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add return production:
   ```cup
   return_statement ::=
       RETURN expression:expr
       {: RESULT = new ReturnNode(RETURNleft, RETURNright, expr); :}
   | RETURN
       {: RESULT = new ReturnNode(RETURNleft, RETURNright, null); :}
   ;
   ```

### Task 8.2: Implement Print Statement
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Update print to handle multiple expressions:
   ```cup
   print_statement ::=
       PRINT expression_list:exprs
       {: RESULT = new PrintNode(PRINTleft, PRINTright, exprs); :}
   ;
   ```

2. Update `PrintNode` to handle list of expressions

**Completion Criteria**:
- ✅ Return with value works
- ✅ Return without value works
- ✅ Print with multiple comma-separated expressions works
- ✅ Tests pass

---

## MILESTONE 9: Statement Organization and Separators
**Goal**: Handle semicolons, newlines, and statement lists correctly

### Task 9.1: Implement Statement Separators
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Update statement list to handle optional separators:
   ```cup
   non terminal List<StatementNode> statement_list;
   non terminal StatementNode statement;
   non terminal void separator_opt;
   non terminal void separator;
   
   statement_list ::=
       statement_list:list separator statement:stmt
       {: list.add(stmt); RESULT = list; :}
   | statement_list:list separator
       {: RESULT = list; :}  // trailing separator
   | statement:stmt
       {: List<StatementNode> list = new ArrayList<>();
          list.add(stmt);
          RESULT = list; :}
   ;
   
   separator ::=
       SEMICOLON
   | NEWLINE
   | SEMICOLON separator  // multiple separators
   | NEWLINE separator
   ;
   
   separator_opt ::=
       separator
   | /* empty */
   ;
   ```

### Task 9.2: Update All Statement Productions
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Consolidate all statement types:
   ```cup
   statement ::=
       declaration:d
       {: RESULT = d; :}
   | assignment:a
       {: RESULT = a; :}
   | if_statement:i
       {: RESULT = i; :}
   | while_loop:w
       {: RESULT = w; :}
   | for_loop:f
       {: RESULT = f; :}
   | return_statement:r
       {: RESULT = r; :}
   | exit_statement:e
       {: RESULT = e; :}
   | print_statement:p
       {: RESULT = p; :}
   | expression:e
       {: RESULT = new ExpressionStatementNode(eleft, eright, e); :}
   ;
   ```

**Completion Criteria**:
- ✅ Statements can be separated by semicolons
- ✅ Statements can be separated by newlines
- ✅ Both separators can be used interchangeably
- ✅ Multiple consecutive separators are handled
- ✅ Tests with various separator combinations pass

---

## MILESTONE 10: Error Handling and Recovery
**Goal**: Add robust error handling and meaningful error messages

### Task 10.1: Add Error Productions
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add error recovery rules at strategic points:
   ```cup
   statement ::=
       // ... existing rules ...
   | error separator
       {: parser.report_error("Invalid statement", null);
          RESULT = new ErrorNode(0, 0); :}
   ;
   
   expression ::=
       // ... existing rules ...
   | error
       {: parser.report_error("Invalid expression", null);
          RESULT = new LiteralNode(null, LiteralNode.LiteralType.NONE, 0, 0); :}
   ;
   ```

### Task 10.2: Enhance Error Reporting
**File**: `src/main/java/com/javdin/parser/Parser.java`

#### Actions:
1. Catch and wrap CUP exceptions with better messages
2. Include line and column information
3. Provide helpful suggestions for common errors

### Task 10.3: Add Custom Error Handler
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Override CUP error reporting methods:
   ```cup
   parser code {:
       public void report_error(String message, Object info) {
           // Custom error reporting
       }
       
       public void syntax_error(Symbol cur_token) {
           // Enhanced syntax error messages
       }
   :};
   ```

**Completion Criteria**:
- ✅ Syntax errors produce helpful messages
- ✅ Line and column numbers are reported
- ✅ Parser recovers from errors when possible
- ✅ Tests for error cases pass

---

## MILESTONE 11: Comprehensive Testing
**Goal**: Create extensive test suite covering all language features

### Task 11.1: Create Expression Tests
**File**: `src/test/java/com/javdin/parser/ParserExpressionTest.java`

#### Actions:
1. Test all binary operators
2. Test all unary operators
3. Test operator precedence
4. Test associativity
5. Test complex nested expressions

### Task 11.2: Create Statement Tests
**File**: `src/test/java/com/javdin/parser/ParserStatementTest.java`

#### Actions:
1. Test all control flow statements
2. Test declarations and assignments
3. Test function definitions
4. Test nested statements

### Task 11.3: Create Literal Tests
**File**: `src/test/java/com/javdin/parser/ParserLiteralTest.java`

#### Actions:
1. Test all literal types
2. Test arrays with various content
3. Test tuples with named/unnamed elements
4. Test function literals

### Task 11.4: Create Integration Tests
**File**: `src/test/java/com/javdin/parser/ParserIntegrationTest.java`

#### Actions:
1. Parse all files in `test-resources/`
2. Verify AST structure for each
3. Test programs from Project D spec

### Task 11.5: Test Error Cases
**File**: `src/test/java/com/javdin/parser/ParserErrorTest.java`

#### Actions:
1. Test invalid syntax
2. Test missing tokens
3. Test unexpected tokens
4. Verify error messages are helpful

**Completion Criteria**:
- ✅ Code coverage > 80% for parser
- ✅ All test programs parse successfully
- ✅ All error cases handled properly
- ✅ Edge cases tested

---

## MILESTONE 12: Documentation and Refinement
**Goal**: Document the parser and prepare for integration

### Task 12.1: Document Grammar
**File**: `src/main/resources/parser.cup`

#### Actions:
1. Add comprehensive comments explaining each production
2. Document operator precedence decisions
3. Add examples for complex rules

### Task 12.2: Create Parser Documentation
**File**: `docs/parser.md`

#### Actions:
1. Explain CUP integration
2. Document AST structure
3. Provide usage examples
4. Explain error handling approach

### Task 12.3: Update Project Documentation
**File**: `docs/STRUCTURE.md`

#### Actions:
1. Mark parser as completed
2. Update implementation status
3. Document next steps (semantic analysis)

### Task 12.4: Performance Testing
**File**: `src/test/java/com/javdin/parser/ParserPerformanceTest.java`

#### Actions:
1. Test parsing large files
2. Test deeply nested structures
3. Identify and fix performance bottlenecks

**Completion Criteria**:
- ✅ Grammar is well-documented
- ✅ Parser API is documented
- ✅ Usage examples provided
- ✅ Performance is acceptable

---

## MILESTONE 13: Integration with Existing Components
**Goal**: Ensure parser works seamlessly with lexer, AST, and interpreter

### Task 13.1: Verify Lexer Integration
**Actions**:
1. Test all token types from lexer
2. Verify line/column information preservation
3. Test with all sample programs

### Task 13.2: Verify AST Integration
**Actions**:
1. Ensure all AST nodes are created correctly
2. Test visitor pattern with parser output
3. Verify semantic analyzer can process parsed AST

### Task 13.3: End-to-End Testing
**File**: `src/test/java/com/javdin/integration/EndToEndTest.java`

#### Actions:
1. Update tests to use CUP parser
2. Test full pipeline: source → tokens → AST → interpretation
3. Verify output correctness

**Completion Criteria**:
- ✅ All components work together
- ✅ End-to-end tests pass
- ✅ No regressions in existing functionality

---

## Progress Tracking

### Milestone Checklist
- [x] M1: Environment Setup and CUP Integration ✅ **COMPLETED** (Oct 11, 2025)
- [ ] M2: Core Grammar - Literals and Simple Expressions
- [ ] M3: Expression Grammar with Proper Precedence
- [ ] M4: References and Postfix Operations
- [ ] M5: Function Literals
- [ ] M6: Declarations and Assignments
- [ ] M7: Control Flow Statements
- [ ] M8: Remaining Statements
- [ ] M9: Statement Organization and Separators
- [ ] M10: Error Handling and Recovery
- [ ] M11: Comprehensive Testing
- [ ] M12: Documentation and Refinement
- [ ] M13: Integration with Existing Components

### Key Files to Create/Modify

#### To Modify:
- `pom.xml` - Enable CUP plugin
- `src/main/resources/parser.cup` - Complete grammar
- `src/main/java/com/javdin/parser/Parser.java` - Wrapper for CUP parser
- Test files - Update and expand tests

#### To Create:
- `src/main/java/com/javdin/parser/LexerAdapter.java` - Lexer-to-CUP adapter
- `src/main/java/com/javdin/ast/ArrayLiteralNode.java` - If missing
- `src/main/java/com/javdin/ast/TupleLiteralNode.java` - If missing
- `src/main/java/com/javdin/ast/TupleElement.java` - Helper class
- `src/main/java/com/javdin/ast/TypeCheckNode.java` - For `is` operator
- `src/main/java/com/javdin/ast/TupleMemberAccessNode.java` - For tuple access
- `src/main/java/com/javdin/ast/ForHeader.java` - Helper for for loops
- `src/test/java/com/javdin/parser/Parser*Test.java` - Comprehensive tests
- `docs/parser.md` - Parser documentation

---

## Notes and Considerations

### Important Design Decisions

1. **CUP vs Hand-Written Parser**: Using CUP for declarative grammar definition and automatic parser generation. More maintainable than hand-written recursive descent.

2. **Token Mapping**: Need to carefully map between our `TokenType` enum and CUP's symbol constants. The `LexerAdapter` is crucial.

3. **AST Node Creation**: All node creation happens in grammar actions using line/column from token positions.

4. **Error Recovery**: Use CUP's error recovery with strategic error productions to continue parsing after errors.

5. **Precedence**: Define operator precedence in CUP rather than in grammar structure where possible.

### Potential Challenges

1. **Lexer-CUP Integration**: The adapter between our lexer and CUP's scanner interface needs careful testing.

2. **Line/Column Tracking**: Must preserve source location throughout for good error messages.

3. **Ambiguity Resolution**: Some Project D constructs may create grammar ambiguities that need resolution.

4. **Separator Handling**: Newlines vs semicolons as statement separators needs careful grammar design.

5. **Function Syntax**: The two function body styles (`is...end` vs `=>`) need clear disambiguation.

### Testing Strategy

1. **Unit Tests**: Test individual grammar rules in isolation
2. **Integration Tests**: Test combinations of features
3. **Regression Tests**: Use existing test files to prevent regressions
4. **Error Tests**: Verify error handling and messages
5. **Performance Tests**: Ensure parser scales to large inputs

### Success Criteria

The parser implementation is complete when:
- ✅ All Project D language features can be parsed
- ✅ All test programs in `test-resources/` parse correctly
- ✅ AST structure matches language semantics
- ✅ Error messages are clear and helpful
- ✅ Test coverage > 80%
- ✅ No regressions in existing functionality
- ✅ Documentation is complete
- ✅ Integration with other components works

---

## Timeline Estimate

- **M1**: 2-3 hours (setup and build integration)
- **M2**: 3-4 hours (basic grammar and literals)
- **M3**: 4-5 hours (expression precedence)
- **M4**: 2-3 hours (references and postfix)
- **M5**: 2-3 hours (function literals)
- **M6**: 2-3 hours (declarations and assignments)
- **M7**: 4-5 hours (control flow)
- **M8**: 1-2 hours (remaining statements)
- **M9**: 2-3 hours (separators and organization)
- **M10**: 3-4 hours (error handling)
- **M11**: 5-6 hours (comprehensive testing)
- **M12**: 2-3 hours (documentation)
- **M13**: 2-3 hours (integration verification)

**Total Estimated Time**: 35-47 hours

---

## Next Steps After Completion

Once the parser is complete:
1. Complete semantic analysis implementation
2. Enhance interpreter to handle all AST nodes
3. Add runtime type checking
4. Implement all built-in functions
5. Add debugging support
6. Create user documentation

---

*This plan should be treated as a living document and updated as implementation progresses.*
