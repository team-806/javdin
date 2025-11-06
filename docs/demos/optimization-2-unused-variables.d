// Example 2.2: Unused Variable Removal
// This demonstrates detection and removal of unused variables

var used := 10
var unused1 := 20     // WARNING: Unused variable 'unused1'
var unused2 := 30     // WARNING: Unused variable 'unused2'

print used

var alsoUnused := 40  // WARNING: Unused variable 'alsoUnused'
