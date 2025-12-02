#!/bin/bash
# Shared helpers for running Project D .d programs via the Javdin interpreter.

set -o pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
JAR_PATH="${JAR_PATH:-${REPO_ROOT}/target/javdin-1.0.0.jar}"
JAVA_CMD=(java -jar "$JAR_PATH")

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASSED=0
FAILED=0
EXPECTED_ERRORS=0

# Tests that should fail even if their filenames do not contain one of the
# generic error keywords. Paths are stored relative to the repository root.
FORCED_ERROR_TESTS=(
    "test-resources/arrays/test-array-negative-index.d"
    "test-resources/arrays/test-array-index-zero.d"
    "test-resources/tuples/test-tuple-out-of-range.d"
    "test-resources/tuples/test-tuple-index-zero.d"
    "test-resources/tuples/test-tuple-unknown-name.d"
    "test-resources/language-d-specific/test-mixed-type-concat.d"
    "test-resources/functions/test-invalid-function-call.d"
)

reset_counters() {
    PASSED=0
    FAILED=0
    EXPECTED_ERRORS=0
}

ensure_jar() {
    if [ ! -f "$JAR_PATH" ]; then
        echo "Missing $JAR_PATH. Run 'mvn package' before executing this suite." >&2
        return 1
    fi
}

rel_path() {
    local abs="$1"
    if [[ "$abs" == "$REPO_ROOT"* ]]; then
        echo "${abs#"$REPO_ROOT"/}"
    else
        echo "$abs"
    fi
}

is_forced_error() {
    local rel="$1"
    for forced in "${FORCED_ERROR_TESTS[@]}"; do
        if [[ "$forced" == "$rel" ]]; then
            return 0
        fi
    done
    return 1
}

should_expect_error() {
    local file="$1"
    local rel="$(rel_path "$file")"
    if is_forced_error "$rel"; then
        echo "error"
        return
    fi

    local lower="${rel,,}"
    if [[ "$lower" == *"-error"* ]] ||
       [[ "$lower" == *"error-"* ]] ||
       [[ "$lower" == *"invalid"* ]] ||
       [[ "$lower" == *"undeclared"* ]] ||
       [[ "$lower" == *"duplicate"* ]] ||
       [[ "$lower" == *"unknown"* ]] ||
       [[ "$lower" == *"unreachable"* ]] ||
       [[ "$lower" == *"bad"* ]] ||
       [[ "$lower" == *"fail"* ]] ||
       [[ "$lower" == *"negative"* ]] ||
       [[ "$lower" == *"mismatch"* ]] ||
       [[ "$lower" == *"invalid"* ]]; then
        echo "error"
    else
        echo "success"
    fi
}

print_expected_error_snippet() {
    local output="$1"
    local lines
    lines=$(echo "$output" | head -5)
    echo "$lines"
}

run_test_file() {
    local file="$1"
    local rel="$(rel_path "$file")"
    local expectation
    expectation="$(should_expect_error "$file")"
    local label="expect ${expectation}"

    printf "Running %s (%s)... " "$rel" "$label"

    local output
    if ! output="$(${JAVA_CMD[@]} "$file" 2>&1)"; then
        if [[ "$expectation" == "error" ]]; then
            echo -e "${BLUE}ERROR (as expected)${NC}"
            print_expected_error_snippet "$output"
            echo ""
            ((EXPECTED_ERRORS++))
        else
            echo -e "${RED}FAILED (unexpected error)${NC}"
            echo "$output"
            echo ""
            ((FAILED++))
        fi
        return
    fi

    if [[ "$expectation" == "error" ]]; then
        echo -e "${RED}FAILED (should have errored)${NC}"
        echo "$output"
        echo ""
        ((FAILED++))
    else
        echo -e "${GREEN}PASS${NC}"
        echo "$output"
        echo ""
        ((PASSED++))
    fi
}

run_suite() {
    local dir="$1"
    local label="${2:-$(rel_path "$dir")}"
    local maxdepth="${3:-10}"

    if [ ! -d "$dir" ]; then
        echo "Directory not found: $dir" >&2
        return 1
    fi

    mapfile -t files < <(find "$dir" -maxdepth "$maxdepth" -type f -name '*.d' | sort)
    if [ ${#files[@]} -eq 0 ]; then
        echo "No .d files under $dir"
        return 0
    fi

    echo "======================================"
    echo "$label"
    echo "======================================"
    echo ""

    for file in "${files[@]}"; do
        run_test_file "$file"
    done
}

run_fileset() {
    local label="$1"
    shift
    if [ $# -eq 0 ]; then
        echo "No files supplied to run_fileset" >&2
        return 1
    fi

    echo "======================================"
    echo "$label"
    echo "======================================"
    echo ""

    for file in "$@"; do
        run_test_file "$file"
    done
}

print_summary() {
    local caption="$1"
    echo "======================================"
    echo "$caption"
    echo "======================================"
    echo -e "${GREEN}Passed: $PASSED${NC}"
    echo -e "${BLUE}Expected Errors: $EXPECTED_ERRORS${NC}"
    echo -e "${RED}Failed: $FAILED${NC}"
    echo "Total: $((PASSED + EXPECTED_ERRORS + FAILED))"
    if [ $FAILED -eq 0 ]; then
        echo -e "\n${GREEN}All scenarios behaved as expected.${NC}"
    else
        echo -e "\n${RED}Some scenarios need attention.${NC}"
    fi
}
