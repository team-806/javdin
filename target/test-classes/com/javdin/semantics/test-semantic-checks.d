// Test semantic checks
var x := 10

// Should error: break outside loop
break

// Should error: return outside function  
return 5

// Should error: variable not declared
y := 20

function test() is
    // Should be OK
    return 42
end

while true loop
    // Should be OK
    break
    // Should error: unreachable code
    x := 30
end