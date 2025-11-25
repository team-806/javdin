// Example 2.4: Unreachable Code Removal
// This demonstrates detection and removal of unreachable code after return

var compute := func(x) is
    if x < 0 then
        return 0
        print "Unreachable after return"  // WARNING: Unreachable code
        var y := 10                        // WARNING: Unreachable code
    end
    
    return x * 2
end

print compute(5)
print compute(-3)
