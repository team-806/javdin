- поддержка tuple и других сложных структур. Чел придирался 3 раза к tuple. Не зря наверное
- аст дерево непонятное, надо как то систематизировать
(Возможно все же придется переделывать под нужды cup - не понятно получиться ли делать запускаемый код из этого???)
- и затрагивая оба пункта, надо четка иметь представление о if else, и в целом синтаксисе. Сделать шпору


Context:
I am working on the Javdin project which is Java-based dynamic interpreter for an academic language similar to JavaScript, language specification can be found in docs/Project D.md. This document is a single source of truth for this project. In case of any confusion points refer to this document first.
On the current stage of the project we have:
1) A hand-written lexer, which takes source code in .d files and outputs stream of tokens with position information.
2) LexerAdapter -
Purpose: Bridge between lexer and CUP parser
Converts Token objects to CUP Symbol objects
Maps token types to CUP terminal symbols
3) CUP-generated LR parser
Input: Token stream from LexerAdapter
Grammar: 417 lines in parser.cup
Output: Abstract Syntax Tree (AST)
4) Custom AST nodes (for creating custom ast tree xml visualization, see src/main/java/com/javdin/visualization/AstXmlSerializer.java)
Node hierarchy: 23 specialized classes extending StatementNode or ExpressionNode
Each production rule creates specific AST node type
All nodes are immutable with final fields
Position tracking: Every node stores source line and column
5) Semantic Analysis:
5.1) SemanticAnalyzer
- Performs non-modifying semantic validation
   - Detects 4 types of semantic errors:
     1. Return outside function check
     2. Break/Continue outside loop check
     3. Undeclared variable check
     4. Duplicate declaration check
5.2) Optimizer
   - Performs AST-modifying optimizations
   - Uses symbol table for scope management
   - Implements 4 optimization techniques:
   - Pass 1: Collect used variables
   - Pass 2: Apply optimizations
     1. Constant folding
     2. Unused variable removal
     3. Dead branch elimination
     4. Unreachable code removal
6) Interpreter: Executes the optimized AST and produces runtime behavior.

The problems:
1) Current parser implementation rejects empty array/tuple literals despite the spec. You can try running these tests to see the issue:
java -jar target/javdin-1.0.0.jar test-resources/test-empty-array.d 
java -jar target/javdin-1.0.0.jar test-resources/test-empty-tuple.d 
2) There is a confusion with array/array type indicators, for example in the current program logic, to check if one type is instance of another type you should write: "{1,2,3} is {}" meaning "is {1,2,3} is of tuple type?" or "[1,2,3] is []" meaning "is [1,2,3] is of array type?". This syntax is not obvious and should be either:
2.1) replaced by this "{1,2,3} is tuple" meaning "is {1,2,3} is of tuple type?" or "[1,2,3] is array" meaning "is [1,2,3] is of array type?"
2.2) or complemented by 2.1) - so both notations should work. But this option may be very non trivial to implement, if that is the case stay with 2.1)

Your goal:
Analyze the existing components, and solve the problems 

Output format notes:
Stay formal and try to be critical thinking. Remember that existing code might have errors. Do not use emoji or Unicode symbols use only Ascii. Do not create excessive documentation, try to be concise with your reports.