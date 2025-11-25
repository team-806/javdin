// Example 1.2: Break Outside Loop (ERROR)
// This code demonstrates the semantic check for break statements outside loops

var count := 0
print "Starting..."

if count = 0 then
    exit  // ERROR: Break statement outside loop
end

print "Done"
