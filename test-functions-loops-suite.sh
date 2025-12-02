#!/bin/bash

# Targeted regression tests for advanced function and loop scenarios
# Requires an up-to-date target/javdin-1.0.0.jar (run `mvn package` first if needed)

set -u

JAR_PATH="target/javdin-1.0.0.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "Missing $JAR_PATH. Build the project before running this suite." >&2
    exit 1
fi

JAVA_CMD=(java -jar "$JAR_PATH")

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

passed=0
failed=0
expected_errors=0

echo "======================================"
echo "Function + Loop Test Suite"
echo "======================================"
echo ""

run_success() {
    local test_file=$1
    shift
    local expected_output
    expected_output="$(cat)"

    echo -n "Running $(basename "$test_file")... "
    if ! output="$(${JAVA_CMD[@]} "$test_file" 2>&1)"; then
        echo -e "${RED}FAILED (unexpected error)${NC}"
        echo "$output"
        echo ""
        ((failed++))
        return
    fi

    if [[ "$output" == "$expected_output" ]]; then
        echo -e "${GREEN}PASS${NC}"
        echo "$output"
        echo ""
        ((passed++))
    else
        echo -e "${RED}FAILED (output mismatch)${NC}"
        echo "Expected:" && echo "$expected_output"
        echo "Actual:" && echo "$output"
        echo ""
        ((failed++))
    fi
}

run_error() {
    local test_file=$1
    local required_snippet=$2

    echo -n "Running $(basename "$test_file") (expect error)... "
    if output="$(${JAVA_CMD[@]} "$test_file" 2>&1)"; then
        echo -e "${RED}FAILED (expected error but succeeded)${NC}"
        echo "$output"
        echo ""
        ((failed++))
        return
    fi

    if grep -q "$required_snippet" <<<"$output"; then
        echo -e "${BLUE}ERROR (expected)${NC}"
        echo "$output" | head -3
        echo ""
        ((expected_errors++))
    else
        echo -e "${RED}FAILED (wrong error message)${NC}"
        echo "$output"
        echo ""
        ((failed++))
    fi
}

# Function tests (success)
run_success "test-resources/functions/test-function-early-return.d" <<'EOF'
20
1010
EOF

run_success "test-resources/functions/test-function-recursive-helper.d" <<'EOF'
15
0
EOF

run_success "test-resources/functions/test-function-looped-closure.d" <<'EOF'
21
3
EOF

# Function tests (expected error)
run_error "test-resources/functions/test-inner-scope-error.d" "Variable 'inner' is not declared"

# Loop tests (success)
run_success "test-resources/loops/test-loop-exit-scope.d" <<'EOF'
2
EOF

run_success "test-resources/loops/test-loop-aggregate-iteration.d" <<'EOF'
66
EOF

run_success "test-resources/loops/test-loop-mixed-nesting.d" <<'EOF'
[10, 20, 20, 30, 110, 120, 120, 130]
EOF

echo "======================================"
echo -e "${GREEN}Passed: $passed${NC}"
echo -e "${BLUE}Expected Errors: $expected_errors${NC}"
echo -e "${RED}Failed: $failed${NC}"
echo "Total scenarios: $((passed + expected_errors + failed))"

echo ""
if [ $failed -eq 0 ]; then
    echo -e "${GREEN}All targeted function/loop tests behaved as expected.${NC}"
    exit 0
else
    echo -e "${RED}Some function/loop tests failed. Investigate output above.${NC}"
    exit 1
fi
