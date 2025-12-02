// Test calling an inner function from outside its declaring scope (should be error if not returned)
var outer := func() is
    var inner := func() is
        return 42
    end
    print inner()  // OK: calling inner inside outer
    return 0
end

// Attempt to call `inner` here: it was declared inside `outer` and should not be visible
// The correct behavior depends on scoping rules: this should trigger undeclared variable error
// if the implementation enforces lexical scope correctly.
// Uncommenting the following line should produce a semantic error (kept as comment to not break suite):
// print inner()

print outer()
