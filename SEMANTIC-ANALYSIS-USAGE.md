# Semantic Analysis Quick Reference

## Usage Examples

### Basic Usage

```java
import com.javdin.semantics.*;
import com.javdin.utils.ErrorHandler;
import com.javdin.ast.ProgramNode;

// After parsing your source code to get an AST
ProgramNode ast = parser.parse(sourceCode);

// Create error handler
ErrorHandler errorHandler = new ErrorHandler();

// Step 1: Semantic Analysis
SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
analyzer.analyze(ast);

if (errorHandler.hasErrors()) {
    errorHandler.printErrors();
    // Handle errors appropriately
    return;
}

// Step 2: Optimization (optional but recommended)
Optimizer optimizer = new Optimizer(errorHandler);
ProgramNode optimizedAst = optimizer.optimize(ast);

// Check for optimization warnings
if (errorHandler.hasErrors()) {
    errorHandler.printErrors(); // These are warnings
}

// Continue with interpretation or code generation using optimizedAst
```

## What Gets Checked

### Semantic Analyzer Checks

1. **Undeclared Variables**
```d
print x  # Error: Variable 'x' is not declared
```

2. **Duplicate Declarations**
```d
var x := 1
var x := 2  # Error: Variable 'x' is already declared
```

3. **Return Outside Function**
```d
return 5  # Error: Return statement outside function
```

4. **Break/Continue Outside Loop**
```d
exit  # Error: Break statement outside loop
```

### Optimizer Transformations

1. **Constant Folding**
```d
# Before:
var x := 2 + 3

# After:
var x := 5
```

2. **Dead Branch Elimination**
```d
# Before:
if true then
    print 1
else
    print 2
end

# After:
print 1
```

3. **Unreachable Code Removal**
```d
# Before:
return 1
print "never executed"

# After:
return 1
# Warning: Unreachable code detected
```

4. **Unused Variable Removal**
```d
# Before:
var x := 5
print 10

# After:
print 10
# Warning: Unused variable 'x'
```

## Error Messages

All errors include line and column information:

```
Variable 'x' is not declared (line 3, column 7)
Return statement outside function (line 5, column 1)
Break statement outside loop (line 10, column 3)
Unreachable code detected after return (line 8, column 1)
Unused variable 'temp' (line 2, column 5)
```

## Integration with Existing Code

The semantic analysis fits naturally after parsing:

```
Source Code (.d file)
    ↓
Lexer (Token Stream)
    ↓
Parser (AST)
    ↓
Semantic Analyzer (Validated AST) ← NEW
    ↓
Optimizer (Optimized AST)          ← NEW
    ↓
Interpreter/Code Generator
```

## Advanced Features

### Scope Management

The analyzer properly handles nested scopes:

```d
var x := 1          # Global scope

func f() is
    var x := 2      # Function scope (shadows global x)
    
    if true then
        var x := 3  # Block scope (shadows function x)
        print x     # Prints 3
    end
    
    print x         # Prints 2
end

print x             # Prints 1
```

### Function Context

Returns are allowed in functions but not at global scope:

```d
func double(x) is
    return x * 2    # OK: inside function
end

return 5            # Error: outside function
```

### Loop Context

Break/continue are allowed in loops but not elsewhere:

```d
while true loop
    if x > 10 then
        exit        # OK: inside loop
    end
end

exit                # Error: outside loop
```

## Performance Considerations

- **Semantic Analysis**: O(n) where n is the number of AST nodes
- **Optimization**: Two-pass approach, O(2n) ≈ O(n)
- **Memory**: Symbol table grows with nested scopes, typically small

## Testing

Run semantic analysis tests:
```bash
mvn test -Dtest=SemanticAnalyzerTest
mvn test -Dtest=OptimizerTest
```

Or both:
```bash
mvn test -Dtest='SemanticAnalyzerTest,OptimizerTest'
```

## Extending the Analysis

To add new semantic checks:

1. Add logic to appropriate `visit*` method in `SemanticAnalyzer`
2. Use `errorHandler.addError()` to report issues
3. Add test cases in `SemanticAnalyzerTest`

To add new optimizations:

1. Add logic to appropriate `visit*` method in `Optimizer`
2. Return modified AST node or null to remove node
3. Add test cases in `OptimizerTest`
