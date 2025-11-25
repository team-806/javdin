var arr := []
var fn := func(x) => x * 2
arr[1] := fn
arr[2] := 42
arr[3] := "hello"
arr[4] := [1, 2, 3]
arr[5] := {a := 1}
print arr[1](5)
print arr[2]
print arr[3]
print arr[4][2]
print arr[5].a
