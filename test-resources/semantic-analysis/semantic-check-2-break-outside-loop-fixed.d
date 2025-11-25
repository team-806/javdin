// Example 1.2: Break Outside Loop (FIXED)
// This shows the corrected version with break inside a loop

var count := 0
print "Starting..."

loop
    print count
    count := count + 1
    
    if count = 5 then
        exit  // CORRECT: Break statement inside loop
    end
end

print "Done"
