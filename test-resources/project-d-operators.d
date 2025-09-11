// Example 4: All operators and type indicators
// Arithmetic
var result := (10 + 5) * 3 - 2 / 1

// Comparison operators
var tests := [
    5 < 10,
    10 > 5,
    10 >= 10,
    5 <= 5,
    10 = 10,
    10 /= 5,
    10 != 5
]

// Logical operators  
var logic := true and false or not true xor false

// Type indicators
var types := {
    int_type := int,
    real_type := real, 
    bool_type := bool,
    string_type := string,
    array_type := [],
    tuple_type := {}
}

// Array access and assignment
var arr := [10, 20, 30]
arr[100] := func(x) => x + 1
arr[1000] := {a := 1, b := 2.7}

// Tuple access
var t := {first := 'hello', second := 'world', 3.14}
var x := t.first
var y := t.2
