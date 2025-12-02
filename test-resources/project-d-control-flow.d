// Example 2: Control flow with Project D syntax
var i := 0
loop
    print 'Hello'
    i := i + 1
    if i = 100 => exit
end

for j in 1..3 loop  
    print 'Hello from loop', j
end

var arr := [1, 2, 3, 4, 5]
var sum := 0
for element in arr loop
    sum := sum + element
end
print 'Sum is:', sum
