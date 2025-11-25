// Example 1.1: Return Outside Function (ERROR)
// This code demonstrates the semantic check for return statements outside functions

var x := 10
print "Starting program"

return x  // ERROR: Return statement outside function

print "This line is unreachable"
