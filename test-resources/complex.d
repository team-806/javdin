var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

var numbers := [1, 2, 3, 4, 5]
var sum := 0

for value in numbers loop
    sum := sum + value
    print "Adding element", value, "sum is now", sum
end

var result := factorial(5)
print "Factorial of 5 is:", result

var square := func(x) => x * x
print "Square of 7 is:", square(7)
