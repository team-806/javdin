// Iterate over empty array, populated array, and tuple in a single run
var arr := [1, 2, 3]
var tup := {first:=10, second:=20, 30}
var total := 0

for value in [] loop
    total := total + 1000
end

for value in arr loop
    total := total + value
end

for value in tup loop
    total := total + value
end

print total
