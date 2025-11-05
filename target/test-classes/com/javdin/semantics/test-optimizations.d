// Test optimizations
var unused_var := 10  // Should be removed
var used_var := 5 + 3  // Should become 8
var another_var := 2 * (3 + 4)  // Should become 14

if true then
    print "This will always execute"
else
    print "This will be removed"
end

function test() is
    return 10
    // Should warn: unreachable code
    print "This won't execute"
end

var result := used_var + another_var  // Should become 22