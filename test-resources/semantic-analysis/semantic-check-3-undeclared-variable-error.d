// Example 1.3: Undeclared Variable (ERROR)
// This code demonstrates the semantic check for using variables before declaration

print "Starting program"

var x := y + 10  // ERROR: Variable 'y' is not declared

print x
