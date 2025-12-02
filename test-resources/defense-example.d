// guess_{n+1} = (guess_n + number / guess_n) / 2

var number := 768
var guess := number / 2
var iterations := 5

print "Computing sqrt(", number, ") using Heron's method"
print "Initial guess:", guess

for i in 1..iterations loop
    var newGuess := (guess + number / guess) / 2
    print "Iteration ", i, ":", newGuess
    guess := newGuess
end

print "Approximate square root:", guess
print "Check:", guess * guess
