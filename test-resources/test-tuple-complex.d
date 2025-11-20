var fn := func(x) => x + 10
var tup := {
    name := "test",
    value := 42,
    calc := fn,
    nested := {x := 1, y := 2}
}
print tup.name
print tup.value
print tup.calc(5)
print tup.nested.x
print tup.nested.y
