// Test closure: inner function captures outer variable and escapes
var makeCounter := func() is
    var count := 0
    var inc := func() is
        count := count + 1
        return count
    end
    return inc
end

var c := makeCounter()
print c()  // Expect 1
print c()  // Expect 2
print c()  // Expect 3
