// Complex test program with functions and control flow
var factorial = function(n) -> {
    if (n <= 1) {
        return 1;
    } else {
        return n * factorial(n - 1);
    }
};

var numbers = [1, 2, 3, 4, 5];
var sum = 0;

for (var i = 0; i < numbers.length; i = i + 1) {
    sum = sum + numbers[i];
    print "Adding " + numbers[i] + ", sum is now " + sum;
}

var result = factorial(5);
print "Factorial of 5 is: " + result;

var lambda_square = lambda(x) -> x * x;
print "Square of 7 is: " + lambda_square(7);
