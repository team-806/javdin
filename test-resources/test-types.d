var result := 42
var arr := [1, 2, 3]
var t := {a := 1}

var types := {
    int_check := result is int,
    real_check := 3.14 is real, 
    bool_check := true is bool,
    string_check := 'hello' is string,
    array_check := arr is [],
    tuple_check := t is {}
}
