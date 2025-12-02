// Test while loop basic behavior
var i := 1
while i <= 5 loop
    print i
    i := i + 1
end

// while loop with exit
var j := 0
while j < 10 loop
    if j = 3 => exit
    print j
    j := j + 1
end
print '"after-while"', j
