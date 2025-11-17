var result := 42
var arr := [1, 2, 3]
var t := {a := 1}
var pi := 3.14
var truth := true
var greeting := "hello"

var types := {
    int_check := result is int,
    real_check := pi is real,
    bool_check := truth is bool,
    string_check := greeting is string,
    array_check := arr is [],
    tuple_check := t is {}
}
