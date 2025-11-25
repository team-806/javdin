// Example 2.3: Dead Branch Elimination
// This demonstrates removal of unreachable branches in conditionals

var x := 10

if true then
    print "This will always execute"
else
    print "This branch is dead and will be removed"
end

if false then
    print "This branch is dead and will be removed"
else
    print "This will always execute"
end

if 5 < 10 then
    print "Constant comparison - always true"
else
    print "Dead branch"
end
