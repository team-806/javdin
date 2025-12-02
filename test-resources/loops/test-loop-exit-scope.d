// Ensure exit only terminates the innermost active loop
var hits := 0
for i in 1..2 loop
    for j in 1..3 loop
        if j = 2 => exit
        hits := hits + 1
    end
end
print hits
