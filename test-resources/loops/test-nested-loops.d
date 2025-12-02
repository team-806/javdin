// Nested loops: for, while, loop blocks and exit interaction
var sum := 0
for i in 1..3 loop
    var k := 1
    while k <= 3 loop
        for j in 1..2 loop
            if i = 2 and j = 2 and k = 2 => exit
            sum := sum + i + j + k
        end
        k := k + 1
    end
end
print sum
