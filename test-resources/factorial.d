var factorial := func(n) is
    if n <= 1 then
        return 1
    else
        return n * factorial(n - 1)
    end
end

for i in 1..5 loop
    print factorial(i)
    if i = 3 => exit
end