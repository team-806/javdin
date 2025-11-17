// Test precedence of logical operators
var a := true
var b := false
var c := true
var d := false

var result := a or b and c xor d
print result
