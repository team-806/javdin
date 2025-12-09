print("a is integer")
var a := 123
print(a)
print("a is int?:")
print(a is int)

print("now a is string")
a := "string"
print(a)
print("a is int?:")
print(a is int)

print("now a is func")
a := func(x) is
    return x * x
end
print(a(5))
print("a is int?:")
print(a is int)

print("now a is tuple")
a := {a := 1}
print(a.a)
print("a is int?:")
print(a is int)