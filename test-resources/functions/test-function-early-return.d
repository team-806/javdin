// Function returns from inside nested loops only when threshold reached
var search := func(limit) is
    var total := 0
    for i in 1..10 loop
        var j := 1
        while j <= 10 loop
            total := total + i + j
            if total > limit => return total
            j := j + 1
        end
    end
    return total
end

print search(15)
print search(999)
