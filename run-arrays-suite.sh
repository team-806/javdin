#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/scripts/test-suite-common.sh"

ensure_jar || exit 1
reset_counters

run_suite "$REPO_ROOT/test-resources/arrays" "Array Suite"
print_summary "Array Suite"

if [ $FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi
