// Example 3: Functions and complex expressions
var add := func(a, b) is
    return a + b
end

var square := func(x) => x * x

var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)  
    end
end

// Array and tuple literals
var numbers := [1, 2, 3, 4, 5]
var person := {name := 'Alice', age := 30, 42.5}

// Type checking
if factorial is func then
    print 'factorial is a function'
end

// Comparisons with both syntaxes  
if 10 /= 20 and 5 != 3 then
    print 'Different inequality operators'
end
