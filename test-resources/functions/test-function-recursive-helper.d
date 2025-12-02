// Nested helper function must see itself for recursion
var sumDown := func(n) is
    var helper := func(current, acc) is
        if current = 0 => return acc
        return helper(current - 1, acc + current)
    end
    return helper(n, 0)
end

print sumDown(5)
print sumDown(0)
