// Test literal types
x := none;
arr := [1, 2, 3];
emptyArr := [];
tup := {a := 1, b := 2};
mixedTup := {1, name := "test", 3};
emptyTup := {};
func1 := func(x, y) is
    print x + y;
end;
func2 := func(x) => x * 2;
