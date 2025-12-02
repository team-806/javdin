#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/scripts/test-suite-common.sh"

ensure_jar || exit 1
reset_counters

mapfile -t all_tests < <(find "$REPO_ROOT/test-resources" -type f -name '*.d' | sort)
if [ ${#all_tests[@]} -eq 0 ]; then
    echo "No .d test programs found under test-resources/"
    exit 1
fi

echo "======================================"
echo "Full Test-Resources Sweep"
echo "======================================"
echo ""

for test_file in "${all_tests[@]}"; do
    run_test_file "$test_file"
 done

print_summary "Full Test-Resources Sweep"

if [ $FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi
