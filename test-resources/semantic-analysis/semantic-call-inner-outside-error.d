// Semantic test: calling an inner function declared in a nested scope from outside
// should be flagged as an undeclared variable / name error by semantic analyzer.
var outer := func() is
    var inner := func() is
        return 1
    end
    print inner()
end

// Attempt to call inner here - this should be an error for undeclared variable
print inner()  // EXPECT: semantic error (undeclared variable)
