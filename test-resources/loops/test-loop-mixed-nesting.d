// Mix loop/while/for constructs with exits at different nesting levels
var trace := []
var outer := 0
loop
    var mid := 0
    while mid < 2 loop
        for value in [10, 20, 30] loop
            trace := trace + [outer * 100 + mid * 10 + value]
            if value = 20 => exit
        end
        mid := mid + 1
        if outer = 1 and mid = 2 => exit
    end
    outer := outer + 1
    if outer = 2 => exit
end
print trace
