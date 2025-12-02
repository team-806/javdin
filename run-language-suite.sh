#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/scripts/test-suite-common.sh"

ensure_jar || exit 1
reset_counters

run_suite "$REPO_ROOT/test-resources/language-d-specific" "Language-D Specific Suite"
print_summary "Language-D Specific Suite"

if [ $FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi
