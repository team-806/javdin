// Example 1.1: Return Outside Function (FIXED)
// This shows the corrected version with return inside a function

var x := 10
print "Starting program"

var double := func(n) is
    return n * 2  // CORRECT: Return statement inside function
end

print double(x)
