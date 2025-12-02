// Attempt to call inner function outside its lexical scope must fail
var outer := func() is
    var inner := func() is
        return 42
    end
    return inner()
end

print outer()
print inner()
