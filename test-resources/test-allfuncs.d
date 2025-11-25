
var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        var add := func(a, b) is
            var square := func(x) => x * x
            return square(2)
        end
        print(add(1,2))
        return n * factorial(n - 1)
    end
end

print(factorial(5))
