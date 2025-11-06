// Example 1.4: Duplicate Declaration (ERROR)
// This code demonstrates the semantic check for duplicate variable declarations

var x := 10
print x

var x := 20  // ERROR: Variable 'x' is already declared

print x
