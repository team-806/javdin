// Returned inner function runs its own for-loop while capturing outer state
var makeStepper := func(start) is
    var base := start
    var stepper := func(count) is
        var total := 0
        for i in 1..count loop
            total := total + base + i
        end
        return total
    end
    return stepper
end

var s := makeStepper(5)
print s(3)
print makeStepper(0)(2)
