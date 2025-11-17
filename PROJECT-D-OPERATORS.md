# Project D Operator Quick Reference

## Assignment vs Comparison - Key Difference!

### ⚠️ IMPORTANT: Project D is NOT like C/Java/Python!

| Operation | C/Java/Python | Project D | Token Type |
|-----------|---------------|-----------|------------|
| Assignment | `x = 5` | `x := 5` | `ASSIGN_OP` |
| Equality | `x == 5` | `x = 5` | `EQUAL` |
| Inequality | `x != 5` | `x /= 5` | `NOT_EQUAL_ALT` |

## All Project D Operators

### Arithmetic Operators
```d
+       Addition              PLUS
-       Subtraction           MINUS
*       Multiplication        MULTIPLY
/       Division              DIVIDE
```

### Comparison Operators
```d
=       Equality              EQUAL
/=      Inequality            NOT_EQUAL_ALT
<       Less than             LESS_THAN
<=      Less or equal         LESS_EQUAL
>       Greater than          GREATER_THAN
>=      Greater or equal      GREATER_EQUAL
```

### Logical Operators
```d
and     Logical AND           AND
or      Logical OR            OR
xor     Logical XOR           XOR
not     Logical NOT           NOT
```

### Assignment Operator
```d
:=      Assignment            ASSIGN_OP
```

### Type Check Operator
```d
is      Type check            IS
```

## Common Mistakes to Avoid

### ❌ WRONG (C/Java style)
```java
var x = 5           // Wrong! = is for comparison
if x == 5 then      // Wrong! == doesn't exist
if x != 5 then      // Wrong! != doesn't exist
```

###  CORRECT (Project D style)
```d
var x := 5          // Correct! := is for assignment
if x = 5 then       // Correct! = is for equality
if x /= 5 then      // Correct! /= is for inequality
```

## Examples from Project D Spec

### Variable Declaration
```d
var x := 5
var name := "Alice"
var isValid := true
```

### Comparisons in Conditionals
```d
if x = 10 then
    print "x equals 10"
end

if y /= 0 then
    print "y is not zero"
end

if a < b then
    print "a is less than b"
end
```

### Complete Example
```d
var count := 0
var max := 10

while count < max loop
    print count
    count := count + 1
    
    if count = 5 then
        print "Halfway there!"
    end
end

if count = max then
    print "Done!"
end
```

## Why This Design?

Project D uses `:=` for assignment (like Pascal, Ada, Go) to:
1. Make assignment visually distinct from comparison
2. Allow single `=` for equality (more mathematical)
3. Prevent common bugs from `=` vs `==` confusion

This is a deliberate design choice that makes the language safer and more readable.
