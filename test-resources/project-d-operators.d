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

// Type indicators (used with 'is' operator)
var types := {
    int_check := result is int,
    real_check := 3.14 is real, 
    bool_check := true is bool,
    string_check := 'hello' is string,
    array_check := arr is [],
    tuple_check := t is {}
}

// Array access and assignment
var arr := [10, 20, 30]
arr[100] := func(x) => x + 1
arr[1000] := {a := 1, b := 2.7}

// Tuple access
var t := {first := 'hello', second := 'world', 3.14}
var x := t.first
var y := t.2
