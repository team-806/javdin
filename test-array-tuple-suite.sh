#!/bin/bash

# Test runner for array and tuple tests
# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Array and Tuple Test Suite"
echo "======================================"
echo ""

passed=0
failed=0
expected_errors=0

run_test() {
    local test_file=$1
    local test_name=$(basename "$test_file" .d)
    local expect_error=$2  # "error" if we expect this test to fail
    
    echo -n "Running $test_name... "
    
    output=$(java -jar target/javdin-1.0.0.jar "$test_file" 2>&1)
    exit_code=$?
    
    if [ "$expect_error" = "error" ]; then
        # We expect an error
        if [ $exit_code -ne 0 ]; then
            echo -e "${BLUE}ERROR (expected)${NC}"
            echo "$output" | head -3
            echo ""
            ((expected_errors++))
        else
            echo -e "${RED}FAILED (expected error but got success)${NC}"
            echo "$output"
            echo ""
            ((failed++))
        fi
    else
        # We expect success
        if [ $exit_code -eq 0 ]; then
            echo -e "${GREEN}PASS${NC}"
            echo "$output"
            echo ""
            ((passed++))
        else
            echo -e "${RED}FAILED (unexpected error)${NC}"
            echo "$output"
            echo ""
            ((failed++))
        fi
    fi
}

# Array tests
echo "=== Array Tests (Success Cases) ==="
run_test "test-resources/test-empty-array.d"
run_test "test-resources/test-array-concatenation.d"
run_test "test-resources/test-array-sparse.d"
run_test "test-resources/test-array-mixed-types.d"
run_test "test-resources/test-array-modification.d"
run_test "test-resources/test-array-type-checking.d"
run_test "test-resources/test-array-nested.d"
run_test "test-resources/test-array-chained-concat.d"

echo ""
echo "=== Array Tests (Error Cases) ==="
run_test "test-resources/test-array-negative-index.d" "error"
run_test "test-resources/test-array-index-zero.d" "error"
run_test "test-resources/test-array-out-of-range.d"

echo ""
echo "=== Tuple Tests (Success Cases) ==="
run_test "test-resources/test-empty-tuple.d"
run_test "test-resources/test-tuple-concatenation.d"
run_test "test-resources/test-tuple-unnamed-only.d"
run_test "test-resources/test-tuple-mixed-naming.d"
run_test "test-resources/test-tuple-complex.d"
run_test "test-resources/test-tuple-modification.d"
run_test "test-resources/test-tuple-type-checking.d"
run_test "test-resources/test-tuple-chained-concat.d"

echo ""
echo "=== Tuple Tests (Error Cases) ==="
run_test "test-resources/test-tuple-out-of-range.d" "error"
run_test "test-resources/test-tuple-unknown-name.d" "error"
run_test "test-resources/test-tuple-index-zero.d" "error"

echo ""
echo "=== Type Error Cases ==="
run_test "test-resources/test-invalid-array-access.d" "error"
run_test "test-resources/test-invalid-tuple-access.d" "error"
run_test "test-resources/test-invalid-function-call.d" "error"
run_test "test-resources/test-mixed-type-concat.d" "error"

echo ""
echo "======================================"
echo "Test Summary"
echo "======================================"
echo -e "${GREEN}Passed: $passed${NC}"
echo -e "${BLUE}Expected Errors: $expected_errors${NC}"
echo -e "${RED}Failed: $failed${NC}"
echo "Total: $((passed + expected_errors + failed))"

if [ $failed -eq 0 ]; then
    echo -e "\n${GREEN}All tests behaved as expected!${NC}"
    exit 0
else
    echo -e "\n${RED}Some tests failed unexpectedly!${NC}"
    exit 1
fi
