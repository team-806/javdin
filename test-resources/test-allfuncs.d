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
