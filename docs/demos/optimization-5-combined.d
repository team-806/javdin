// Example 2.5: Combined Optimizations
// This demonstrates multiple optimizations working together

var x := 2 + 3              // Constant folding: becomes 5
var unused := 100           // Unused variable removal
var y := x * 2              

if true then                // Dead branch elimination
    print y
else
    print "Dead code"
end

var result := 10 = 10       // Constant folding: becomes true

if result then
    print "Always true"
end

var compute := func(val) is
    if val < 0 then
        return 0
        print "Unreachable"     // Unreachable code removal
        var z := 50             // Unreachable code removal
    end
    return val * 2
end

print compute(5)
