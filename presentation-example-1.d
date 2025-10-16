// Example 1: Factorial function with recursion
var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

var result := factorial(5)
print result
